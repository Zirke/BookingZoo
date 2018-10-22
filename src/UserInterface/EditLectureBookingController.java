package UserInterface;

import Bookings.BookingDataAccessor;
import Bookings.LectureBooking;
import Bookings.Lecturer;
import Customers.LectureBookingCustomer;
import enums.BookingStatus;
import enums.ChoiceOfTopic;
import enums.FacilityState;
import enums.LectureRoomType;
import facilities.LectureRoom;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

import static enums.ChoiceOfTopic.topicChosen;

public class EditLectureBookingController {
    private LectureBooking selectedLectureBooking;

    void setSelectedLectureBooking(LectureBooking selectedLectureBooking) {
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
    private ToggleGroup communeGroup;
    @FXML
    private RadioButton communeRadioBtnYes, communeRadioBtnNo;
    @FXML
    private Button saveAndCloseButton, cancelButton;

    public void initialize() throws SQLException, ClassNotFoundException {
        BookingDataAccessor bda = new BookingDataAccessor(
                "org.postgresql.Driver",
                "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
                "jyjczxth",
                "nw51BNKhctporjIFT5Qhhm72jwGVJK95");

        topicChoiceBox.getItems().addAll("Dyr derhjemme", "Hverdagen i Zoo", "Krybdyr", "Grønlands dyr",
                "Afrikas savanner", "Aktiveringsværksted", "Sanseoplevelser", "Dyrs tilpasning og forskelligheder (Udskoling)",
                "Evolution/Klassifikation (Gymnasium)", "Aalborg Zoo som virksomhed (Handelsskole)");

        lectureRoomChoiceBox.getItems().addAll("Savannelokale", "Biologisk lokale");

        categoryChoiceBox.getItems().addAll("Afventende","Aktiv","Færdig","Arkiveret");

        saveAndCloseButton.setOnMouseClicked(e -> {
            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
            alert2.setContentText("Er den indtastede information korrekt?");

            Optional<ButtonType> alertChoice2 = alert2.showAndWait();

            if (alertChoice2.get() == ButtonType.OK) {
                try {
                    bda.editLecBook(overwriteSelectedLectureBooking());
                    closeWindow();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        noOfPupilsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                noOfPupilsTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        noOfTeamsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                noOfTeamsTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        noOfTeachersTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                noOfTeachersTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        gradeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                gradeTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        zipCodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                zipCodeTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        eanNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                eanNumberTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

    }

    void initData() {
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
        if(temp.getCommune().equals("Ja")){
            communeRadioBtnYes.setSelected(true); }
            else if (temp.getCommune().equals("Nej")){
            communeRadioBtnNo.setSelected(true); }
        schoolPhoneNumberTextField.setText(temp.getSchoolPhoneNumber());
        eanNumberTextField.setText(String.valueOf(temp.getEanNumber()));
        contactPersonTextField.setText(temp.getContactPerson());
        phoneNumberTextField.setText(temp.getPhoneNumber());
        emailTextField.setText(temp.getEmail());

        customerCommentTextArea.setText(selectedLectureBooking.getCustomerComment());
        commentTextArea.setText(selectedLectureBooking.getComment());
    }

    private LectureBooking overwriteSelectedLectureBooking() {
        selectedLectureBooking.setDate(datePicker.getValue());
        selectedLectureBooking.setTime(timeTextField.getText());
        selectedLectureBooking.setParticipants(Integer.parseInt(noOfPupilsTextField.getText()));
        selectedLectureBooking.setNoOfTeams(Integer.parseInt(noOfTeamsTextField.getText()));
        selectedLectureBooking.setNoOfTeachers(Integer.parseInt(noOfTeachersTextField.getText()));
        ChoiceOfTopic topicChoice;
        topicChoice = topicChosen(topicChoiceBox.getSelectionModel().getSelectedItem().toString());
        selectedLectureBooking.setChoiceOfTopic(topicChoice);
        selectedLectureBooking.setGrade(Integer.parseInt(gradeTextField.getText()));
        LectureRoomType roomTypeChoice;
        roomTypeChoice = LectureRoomType.roomTypeChoice(lectureRoomChoiceBox.getSelectionModel().getSelectedItem().toString());
        LectureRoom foo = new LectureRoom(FacilityState.OCCUPIED, roomTypeChoice);
        selectedLectureBooking.setLectureRoom(foo);
        Lecturer bar = new Lecturer(lecturerTextField.getText());
        selectedLectureBooking.setLecturer(bar);
        BookingStatus statusChoice;
        statusChoice = BookingStatus.statusChosen(categoryChoiceBox.getSelectionModel().getSelectedItem().toString());
        selectedLectureBooking.setBookingStatus(statusChoice);

        RadioButton selectedCommuneAnswer = (RadioButton) communeGroup.getSelectedToggle();
        LectureBookingCustomer temp = new LectureBookingCustomer(contactPersonTextField.getText(), phoneNumberTextField.getText(),
                emailTextField.getText(), schoolNameTextField.getText(), Integer.parseInt(zipCodeTextField.getText()),
                cityTextField.getText(), selectedCommuneAnswer.getText(), schoolPhoneNumberTextField.getText(),
                Long.parseLong(eanNumberTextField.getText()));
        selectedLectureBooking.setCustomer(temp);

        return selectedLectureBooking;
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


}
