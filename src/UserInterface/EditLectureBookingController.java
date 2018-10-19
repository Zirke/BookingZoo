package UserInterface;

import Bookings.LectureBooking;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditLectureBookingController {
    private LectureBooking selectedLectureBooking;

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField timeTextField, noOfPupilsTextField, noOfTeamsTextField, noOfTeachersTextField, gradeTextField,
            lecturerChosenTextField, schoolNameTextField, zipCodeTextField, cityTextField, schoolPhoneNumberTextField,
            eanNumberTextField, contactPersonTextField, phoneNumberTextField, emailTextField;
    @FXML
    private ChoiceBox topicChoiceBox, lectureRoomChoiceBox;
    @FXML
    private Button saveAndCloseButton, cancelButton;

    @FXML
    private void initialize() {
        //timeTextField.setText(this.selectedLectureBooking.);


        topicChoiceBox.getItems().addAll("Dyr derhjemme", "Hverdagen i Zoo", "Krybdyr", "Grønlands dyr",
                "Afrikas savanner", "Aktiveringsværksted", "Sanseoplevelser", "Dyrs tilpasning og forskelligheder (Udskoling)",
                "Evolution/Klassifikation (Gymnasium)", "Aalborg Zoo som virksomhed (Handelsskole)");

        lectureRoomChoiceBox.getItems().addAll("Savannelokale", "Biologisk lokale");
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


}
