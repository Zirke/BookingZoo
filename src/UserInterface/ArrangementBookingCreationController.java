package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ArrangementBookingCreationController extends GeneralController {
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

    @FXML
    public void createArrangementBookingFromInput() {
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

        System.out.println("Booking created:\n" + date + time + noOfChildren + childName + childAge + contactPerson + phoneNumber + email + participant + menuChoice + customerComment + comment);

        showAlertBox(Alert.AlertType.CONFIRMATION, "", "Er den indtastede information korrekt?");
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
