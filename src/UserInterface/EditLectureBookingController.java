package UserInterface;

import Bookings.LectureBooking;
import Customers.LectureBookingCustomer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
            lecturerTextField, schoolNameTextField, zipCodeTextField, cityTextField, schoolPhoneNumberTextField,
            eanNumberTextField, contactPersonTextField, phoneNumberTextField, emailTextField;
    @FXML
    private TextArea customerCommentTextArea, commentTextArea;
    @FXML
    private ChoiceBox topicChoiceBox, lectureRoomChoiceBox, categoryChoiceBox;
    @FXML
    private Button saveAndCloseButton, cancelButton;

    public void initialize() {
        topicChoiceBox.getItems().addAll("Dyr derhjemme", "Hverdagen i Zoo", "Krybdyr", "Grønlands dyr",
                "Afrikas savanner", "Aktiveringsværksted", "Sanseoplevelser", "Dyrs tilpasning og forskelligheder (Udskoling)",
                "Evolution/Klassifikation (Gymnasium)", "Aalborg Zoo som virksomhed (Handelsskole)");

        lectureRoomChoiceBox.getItems().addAll("Savannelokale", "Biologisk lokale");

        categoryChoiceBox.getItems().addAll("Afventende","Aktiv","Færdig","Arkiveret");

    }

    public void initData() {
        LectureBookingCustomer temp = (LectureBookingCustomer) selectedLectureBooking.getCustomer();

        //Lecture information
        datePicker.setValue(selectedLectureBooking.getDate());
        timeTextField.setText(selectedLectureBooking.getTime());
        noOfPupilsTextField.setText(String.valueOf(selectedLectureBooking.getParticipants()));
        noOfTeamsTextField.setText(String.valueOf(selectedLectureBooking.getNoOfTeams()));
        noOfTeachersTextField.setText(String.valueOf(selectedLectureBooking.getNoOfTeachers()));
        topicChoiceBox.setValue(selectedLectureBooking.getChoiceOfTopic().toString());
        gradeTextField.setText(String.valueOf(selectedLectureBooking.getGrade()));
        lectureRoomChoiceBox.setValue(selectedLectureBooking.getLectureRoom().toString());
        lecturerTextField.setText(selectedLectureBooking.getLecturer().toString());
        categoryChoiceBox.setValue(selectedLectureBooking.getBookingStatus().toString());

        //Customer information
        schoolNameTextField.setText(temp.getSchoolName());
        zipCodeTextField.setText(String.valueOf(temp.getZipCode()));
        cityTextField.setText(temp.getCity());
        schoolPhoneNumberTextField.setText(temp.getSchoolPhoneNumber());
        eanNumberTextField.setText(String.valueOf(temp.getEanNumber()));
        contactPersonTextField.setText(temp.getContactPerson());
        phoneNumberTextField.setText(temp.getPhoneNumber());
        emailTextField.setText(temp.getEmail());

        customerCommentTextArea.setText(selectedLectureBooking.getCustomerComment());
        commentTextArea.setText(selectedLectureBooking.getComment());
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


}
