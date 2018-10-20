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

    public void setSelectedLectureBooking(LectureBooking selectedLectureBooking) {
        this.selectedLectureBooking = selectedLectureBooking;
    }

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField timeTextField, noOfPupilsTextField, noOfTeamsTextField, noOfTeachersTextField, gradeTextField,
            lecturerChosenTextField, schoolNameTextField, zipCodeTextField, cityTextField, schoolPhoneNumberTextField,
            eanNumberTextField, contactPersonTextField, phoneNumberTextField, emailTextField;
    @FXML
    private ChoiceBox topicChoiceBox, lectureRoomChoiceBox, categoryChoiceBox;
    @FXML
    private Button saveAndCloseButton, cancelButton;

    @FXML
    private void initialize() {
        //LectureBookingCustomer temp = (LectureBookingCustomer) selectedLectureBooking.getCustomer();

        //datePicker.setValue(LocalDate.of(2018,15,15)); //TODO does not work HARDCODED
        //timeTextField.setText(selectedLectureBooking.getTime());
        //noOfPupilsTextField.setText(selectedLectureBooking.getParticipants());
        //noOfTeamsTextField.setText(selectedLectureBooking.getNoOfTeams());
        noOfTeachersTextField.setText(selectedLectureBooking.getNoOfTeachers());
        //topicChoiceBox.setValue(selectedLectureBooking.getChoiceOfTopic());
        //gradeTextField.setText(selectedLectureBooking.getGrade());
        //lectureRoomChoiceBox.setValue(selectedLectureBooking.getLectureRoom().toString());
        //lecturerChosenTextField.setText(selectedLectureBooking.getLecturer().toString());
        //categoryChoiceBox.setValue(selectedLectureBooking.getBookingStatus());
        //schoolNameTextField.setText(temp.getSchoolName());
        //zipCodeTextField.setText(temp.getZipCode());
        //cityTextField.setText(temp.getCity());
        //schoolPhoneNumberTextField.setText(temp.getSchoolPhoneNumber());
        //eanNumberTextField.setText(temp.getEanNumber());
        //contactPersonTextField.setText(temp.getContactPerson());
        //phoneNumberTextField.setText(temp.getPhoneNumber());
        //emailTextField.setText(temp.getEmail());

/*
        topicChoiceBox.getItems().addAll("Dyr derhjemme", "Hverdagen i Zoo", "Krybdyr", "Grønlands dyr",
                "Afrikas savanner", "Aktiveringsværksted", "Sanseoplevelser", "Dyrs tilpasning og forskelligheder (Udskoling)",
                "Evolution/Klassifikation (Gymnasium)", "Aalborg Zoo som virksomhed (Handelsskole)");

        lectureRoomChoiceBox.getItems().addAll("Savannelokale", "Biologisk lokale");
        */
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


}
