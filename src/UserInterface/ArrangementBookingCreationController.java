package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.BookingDataAccessor;
import Bookings.FoodOrder;
import enums.BookingStatus;
import enums.BookingType;
import enums.FacilityState;
import facilities.Restaurant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Date;
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
            phoneNumberTextField, emailTextField;
    @FXML
    private TextArea customerCommentTextArea, commentTextArea;

    @FXML
    private Button createAndCloseButton, cancelButton;

    public void initialize() {
        createAndCloseButton.setOnMouseClicked(e -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Vil du oprette?");
            alert.setContentText("Er den indtastede information korrekt?");

            Optional<ButtonType> foo = alert.showAndWait();

            if (foo.get() == ButtonType.OK) {
                try {
                    createArrangementBookingFromInput();
                    closeWindow();
                } catch (SQLException | ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });

        cancelButton.setOnMouseClicked(e -> closeWindow());
    }

    private void createArrangementBookingFromInput() throws SQLException, ClassNotFoundException {
        String date = datePicker.getValue().toString();
        String time = timeGroup.getSelectedToggle().toString();
        String noOfChildren = noOfChildrenTextField.getText();
        String childName = childNameTextField.getText();
        String childAge = childAgeTextField.getText();
        String contactPerson = contactPersonTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String email = emailTextField.getText();
        String participant = participantGroup.getSelectedToggle().toString();
        String menuChoice = menuGroup.getSelectedToggle().toString();
        String customerComment = customerCommentTextArea.getText();
        String comment = commentTextArea.getText();


        //TODO Finish
        if (!(datePicker.getValue() != null || !timeGroup.getSelectedToggle().isSelected() || noOfChildrenTextField.getText().isEmpty() || childNameTextField.getText().isEmpty())) {
            System.out.println("TOM!");
        }

        bda = new BookingDataAccessor(
                "org.postgresql.Driver",
                "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
                "jyjczxth",
                "nw51BNKhctporjIFT5Qhhm72jwGVJK95"
        );

        ArrangementBooking abook = new ArrangementBooking(
                BookingType.ARRANGEMENTBOOKING, BookingStatus.STATUS_ACTIVE, new Date().toString(), date, time, noOfChildren,
                customerComment, comment, new FoodOrder(menuChoice), new Restaurant(FacilityState.unoccupied), noOfChildren, childName,
                childAge, participant, "Jens", contactPerson, phoneNumber, email);
        try {
            bda.createArrBookManually(abook);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
