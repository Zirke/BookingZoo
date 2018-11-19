package userInterface;

import bookings.ArrangementBooking;
import bookings.BookingDataAccessor;
import bookings.FoodOrder;
import enums.*;
import facilities.Restaurant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class ArrangementBookingCreationController extends GeneralController {

    private BookingDataAccessor bda;
    private ArrangementBooking createdBooking;
    private MainScreenController msc;

    void setBda(BookingDataAccessor bda) {
        this.bda = bda;
    }

    void setMsc(MainScreenController msc) {
        this.msc = msc;
    }

    @FXML
    private ChoiceBox restaurantChoiceBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ToggleGroup timeGroup, participantGroup, menuGroup;

    @FXML
    private TextField noOfChildrenTextField, childNameTextField, childAgeTextField, contactPersonTextField,
            phoneNumberTextField, emailTextField, guideTextField;

    @FXML
    private TextArea customerCommentTextArea, commentTextArea;

    @FXML
    private Button createAndCloseButton, cancelButton;

    public void initialize() {
        restaurantChoiceBox.setValue(RestaurantType.NO_CHOICE.toString());

        for(RestaurantType i : RestaurantType.values()){
            restaurantChoiceBox.getItems().add(i);
        }

        createAndCloseButton.setOnMouseClicked(e -> {
            if (datePicker.getValue() == null || !timeGroup.getSelectedToggle().isSelected() || noOfChildrenTextField.getText().isEmpty() || childNameTextField.getText().isEmpty() ||
                    childAgeTextField.getText().isEmpty() || contactPersonTextField.getText().isEmpty() || phoneNumberTextField.getText().isEmpty() || emailTextField.getText().isEmpty() ||
                    guideTextField.getText().isEmpty() || !participantGroup.getSelectedToggle().isSelected() || !menuGroup.getSelectedToggle().isSelected()) {
                Alert alert1 = new Alert(Alert.AlertType.WARNING);
                alert1.setHeaderText("Tjek alle felter");
                alert1.setContentText("Et eller flere felter mangler information");

                Optional<ButtonType> alertChoice1 = alert1.showAndWait();
            } else {
                Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
                alert2.setContentText("Er den indtastede information korrekt?");

                Optional<ButtonType> alertChoice2 = alert2.showAndWait();

                if (alertChoice2.get() == ButtonType.OK) {

                    try {
                        closeWindow();
                        createArrangementBookingFromInput();
                        ArrangementBooking temp = createdBooking;
                        msc.fetchOnlyNewBookingsFromDataBase();
                        msc.getBookingTableView().getSelectionModel().select(temp);
                        msc.displayInformationOfSelectedBooking(msc.getBookingTableView());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        //TODO Make a static method for this (Also in lectureBookingCreationController)
        noOfChildrenTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                noOfChildrenTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        childAgeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                childAgeTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        cancelButton.setOnMouseClicked(e -> closeWindow());
    }

    private void createArrangementBookingFromInput() throws SQLException {
        LocalDate tempDate = datePicker.getValue();
        RadioButton selectedTimeBtn = (RadioButton) timeGroup.getSelectedToggle();
        LocalTime tempTime;
        if(selectedTimeBtn.getText().equals("10:00 - 12:00")){
            tempTime = LocalTime.of(10,00,00);
        } else{
            tempTime = LocalTime.of(12,30,00);
        }
        LocalDateTime date = LocalDateTime.of(tempDate,tempTime);

        int noOfChildren = Integer.parseInt(noOfChildrenTextField.getText());
        String childName = childNameTextField.getText();
        int childAge = Integer.parseInt(childAgeTextField.getText());
        String contactPerson = contactPersonTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String email = emailTextField.getText();
        String guide = guideTextField.getText();
        RadioButton selectedParticipantBtn = (RadioButton) participantGroup.getSelectedToggle();
        String participant = selectedParticipantBtn.getText();
        RadioButton selectedMenuBtn = (RadioButton) menuGroup.getSelectedToggle();
        ChoiceOfMenu menuChoice = ChoiceOfMenu.getChoiceOfMenu(selectedMenuBtn);
        String customerComment = customerCommentTextArea.getText();
        String comment = commentTextArea.getText();

        ArrangementBooking abook = new ArrangementBooking(
                BookingType.ARRANGEMENTBOOKING, BookingStatus.STATUS_ACTIVE, LocalDate.now(), date,
                noOfChildren, customerComment, comment, new FoodOrder(menuChoice), new Restaurant(FacilityState.OCCUPIED,
                RestaurantType.roomTypeChoice(restaurantChoiceBox.getSelectionModel().getSelectedItem().toString())),
                childName, childAge, participant, guide, contactPerson, phoneNumber, email);

        createdBooking = abook;
        bda.createArrBookManually(abook);
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
