package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.BookingDataAccessor;
import Bookings.FoodOrder;
import enums.BookingStatus;
import enums.BookingType;
import enums.ChoiceOfMenu;
import enums.FacilityState;
import facilities.Restaurant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.*;
import java.util.Optional;

public class ArrangementBookingCreationController extends GeneralController {
    private BookingDataAccessor bda;

    public void setBda(BookingDataAccessor bda) {
        this.bda = bda;
    }

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
                        createArrangementBookingFromInput();
                        closeWindow();
                    } catch (SQLException | ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

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

    private void createArrangementBookingFromInput() throws SQLException, ClassNotFoundException {
        LocalDate date = datePicker.getValue();
        RadioButton selectedTimeBtn = (RadioButton) timeGroup.getSelectedToggle();
        String time = selectedTimeBtn.getText();
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

        bda = new BookingDataAccessor(
                "org.postgresql.Driver",
                "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
                "jyjczxth",
                "nw51BNKhctporjIFT5Qhhm72jwGVJK95"
        );

        ArrangementBooking abook = new ArrangementBooking(
                BookingType.ARRANGEMENTBOOKING, BookingStatus.STATUS_ACTIVE, LocalDate.now(), date, time,
                noOfChildren, customerComment, comment, new FoodOrder(menuChoice), new Restaurant(FacilityState.UNOCCUPIED),
                childName, childAge, participant, guide, contactPerson, phoneNumber, email);

        bda.createArrBookManually(abook);
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
