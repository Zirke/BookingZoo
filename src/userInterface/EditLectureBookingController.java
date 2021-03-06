package userInterface;

import bookings.BookingDataAccessor;
import bookings.LectureBooking;
import bookings.Lecturer;
import customers.LectureBookingCustomer;
import enums.*;
import facilities.FacilityChecker;
import facilities.LectureRoom;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Optional;

import static enums.ChoiceOfTopic.topicChosen;
import static enums.Grade.gradeChosen;
import static userInterface.GeneralController.textfieldWithOnlyNumbers;

public class EditLectureBookingController {
    private BookingDataAccessor bda;
    private MainScreenController msc;
    private LectureBooking selectedLectureBooking;
    private HashMap<LocalDateTime, LectureBooking> lecRoomHashMap;

    void setLecRoomHashMap(HashMap<LocalDateTime, LectureBooking> lecRoomHashMap) {
        this.lecRoomHashMap = lecRoomHashMap;
    }

    void setSelectedLectureBooking(LectureBooking selectedLectureBooking) {
        this.selectedLectureBooking = selectedLectureBooking;
    }

    void setBda(BookingDataAccessor bda) {
        this.bda = bda;
    }

    void setMsc(MainScreenController msc) {
        this.msc = msc;
    }

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField noOfPupilsTextField, noOfTeamsTextField, noOfTeachersTextField,
            lecturerChosenTextField, schoolNameTextField, zipCodeTextField, cityTextField, schoolPhoneNumberTextField,
            eanNumberTextField, contactPersonTextField, phoneNumberTextField, emailTextField;
    @FXML
    private TextArea customerCommentTextArea, commentTextArea;
    @FXML
    private ChoiceBox timeChoiceBox, topicChoiceBox, gradeChoiceBox, lectureRoomChoiceBox;
    @FXML
    private ToggleGroup communeGroup, categoryGroup;
    @FXML
    private RadioButton communeRadioBtnYes, communeRadioBtnNo;
    @FXML
    private RadioButton activeRadioBtn, finishedRadioBtn, archivedRadioBtn;
    @FXML
    private Button saveAndCloseButton, cancelButton;

    public void initialize() {
        timeChoiceBox.getItems().addAll("10:15 - 11:15", "11:15 - 12:15", "12:15 - 13:15", "13:15 - 14:15");
        lectureRoomChoiceBox.getItems().addAll("Savannelokale", "Biologisk lokale", "Intet lokale valgt");
        gradeChoiceBox.getItems().addAll("Børnehaveklasse", "1. klasse", "2. klasse", "3. klasse", "4. klasse",
                "5. klasse", "6. klasse", "7. klasse", "8. klasse", "9. klasse", "10. klasse",
                "1.G", "2.G", "3.G");
        topicChoiceBox.getItems().addAll("Dyr derhjemme", "Hverdagen i Zoo", "Krybdyr", "Grønlands dyr",
                "Afrikas savanner", "Aktiveringsværksted", "Sanseoplevelser", "Dyrs tilpasning og forskelligheder (Udskoling)",
                "Evolution/Klassifikation (Gymnasium)", "Aalborg Zoo som virksomhed (Handelsskole)");

        saveButtonPress();

        textfieldWithOnlyNumbers(noOfPupilsTextField);
        textfieldWithOnlyNumbers(noOfTeamsTextField);
        textfieldWithOnlyNumbers(noOfTeachersTextField);
        textfieldWithOnlyNumbers(zipCodeTextField);
        textfieldWithOnlyNumbers(schoolPhoneNumberTextField);
        textfieldWithOnlyNumbers(phoneNumberTextField);
        textfieldWithOnlyNumbers(eanNumberTextField);
    }

    void initData() {
        LectureBookingCustomer temp = (LectureBookingCustomer) selectedLectureBooking.getCustomer();

        //Lecture information
        datePicker.setValue(selectedLectureBooking.getDateTime().toLocalDate());
        String tempTime;
        switch (selectedLectureBooking.getDateTime().toString()) {
            case "10:15 - 11:15":
                tempTime = "10:15 - 11:15";
                break;
            case "11:15 - 12:15":
                tempTime = "11:15 - 12:15";
                break;
            case "12:15 - 13:15":
                tempTime = "12:15 - 13:15";
                break;
            default:
                tempTime = "13:15 - 14:15";
                break;
        }
        timeChoiceBox.setValue(tempTime);
        noOfPupilsTextField.setText(String.valueOf(selectedLectureBooking.getParticipants()));
        noOfTeamsTextField.setText(String.valueOf(selectedLectureBooking.getNoOfTeams()));
        noOfTeachersTextField.setText(String.valueOf(selectedLectureBooking.getNoOfTeachers()));
        topicChoiceBox.setValue(selectedLectureBooking.getChoiceOfTopic().toString());
        gradeChoiceBox.setValue(String.valueOf(selectedLectureBooking.getGrade()));
        lectureRoomChoiceBox.setValue(selectedLectureBooking.getLectureRoom().toString());
        lecturerChosenTextField.setText(selectedLectureBooking.getLecturer().toString());
        switch (selectedLectureBooking.getBookingStatus().toString()) {
            case "Aktiv":
                activeRadioBtn.setSelected(true);
                break;
            case "Færdig":
                finishedRadioBtn.setSelected(true);
                break;
            case "Arkiveret":
                archivedRadioBtn.setSelected(true);
                break;
        }
        //Customer information
        schoolNameTextField.setText(temp.getSchoolName());
        zipCodeTextField.setText(String.valueOf(temp.getZipCode()));
        cityTextField.setText(temp.getCity());
        if (temp.getCommune().equals("Ja")) {
            communeRadioBtnYes.setSelected(true);
        } else if (temp.getCommune().equals("Nej")) {
            communeRadioBtnNo.setSelected(true);
        }
        schoolPhoneNumberTextField.setText(temp.getSchoolPhoneNumber());
        eanNumberTextField.setText(String.valueOf(temp.getEanNumber()));
        contactPersonTextField.setText(temp.getContactPerson());
        phoneNumberTextField.setText(temp.getPhoneNumber());
        emailTextField.setText(temp.getEmail());
        customerCommentTextArea.setText(selectedLectureBooking.getCustomerComment());
        commentTextArea.setText(selectedLectureBooking.getComment());
    }

    private LectureBooking overwriteSelectedLectureBooking() {
        LocalTime tempTime;
        switch (timeChoiceBox.getValue().toString()) {
            case "10:15 - 11:15":
                tempTime = LocalTime.of(10, 15, 00);
                break;
            case "11:15 - 12:15":
                tempTime = LocalTime.of(11, 15, 00);
                break;
            case "12:15 - 13:15":
                tempTime = LocalTime.of(12, 15, 00);
                break;
            default:
                tempTime = LocalTime.of(13, 15, 00);
                break;
        }

        selectedLectureBooking.setDateTime(LocalDateTime.of(datePicker.getValue(), tempTime));
        selectedLectureBooking.setParticipants(Integer.parseInt(noOfPupilsTextField.getText()));
        selectedLectureBooking.setNoOfTeams(Integer.parseInt(noOfTeamsTextField.getText()));
        selectedLectureBooking.setNoOfTeachers(Integer.parseInt(noOfTeachersTextField.getText()));
        ChoiceOfTopic topicChoice = topicChosen(topicChoiceBox.getSelectionModel().getSelectedItem().toString());
        selectedLectureBooking.setChoiceOfTopic(topicChoice);
        Grade grade = gradeChosen(gradeChoiceBox.getSelectionModel().getSelectedItem().toString());
        selectedLectureBooking.setGrade(grade);
        LectureRoomType roomTypeChoice = LectureRoomType.roomTypeChoice(lectureRoomChoiceBox.getSelectionModel().getSelectedItem().toString());
        LectureRoom foo = new LectureRoom(FacilityState.OCCUPIED, roomTypeChoice);
        selectedLectureBooking.setLectureRoom(foo);
        Lecturer bar = new Lecturer(lecturerChosenTextField.getText());
        selectedLectureBooking.setLecturer(bar);
        RadioButton bookingStatus = (RadioButton) categoryGroup.getSelectedToggle();
        BookingStatus statusChoice = BookingStatus.statusChosen(bookingStatus.getText());
        selectedLectureBooking.setBookingStatus(statusChoice);
        selectedLectureBooking.setCustomerComment(customerCommentTextArea.getText());
        selectedLectureBooking.setComment(commentTextArea.getText());

        //Facility system
        FacilityChecker checker = new FacilityChecker(lecRoomHashMap, selectedLectureBooking);
        Boolean isChosenFacilityOccupied = checker.isChosenFacilityOccupied(0);
        Boolean isChosenFacilityOccupiedPlus1Minute = checker.isChosenFacilityOccupied(1);
        if (isChosenFacilityOccupied || isChosenFacilityOccupiedPlus1Minute) {
            String facilityOccupiedString = "Det valgte lokale " + selectedLectureBooking.getLectureRoom().getType() + " er allerede optaget";
            checker.alertWhenFacilityException(facilityOccupiedString);
            return selectedLectureBooking;
        }
        checker.facilityCheckForUniqueTopics();

        selectedLectureBooking.setCustomer(createLectureBookingCustomer());

        return selectedLectureBooking;
    }

    private LectureBookingCustomer createLectureBookingCustomer() {
        RadioButton selectedCommuneAnswer = (RadioButton) communeGroup.getSelectedToggle();
        return new LectureBookingCustomer(contactPersonTextField.getText(), phoneNumberTextField.getText(),
                emailTextField.getText(), schoolNameTextField.getText(), Integer.parseInt(zipCodeTextField.getText()),
                cityTextField.getText(), selectedCommuneAnswer.getText(), schoolPhoneNumberTextField.getText(),
                Long.parseLong(eanNumberTextField.getText()));
    }

    private void saveButtonPress() {
        saveAndCloseButton.setOnMouseClicked(e -> {
            if (datePicker.getValue() == null || noOfPupilsTextField.getText().isEmpty() ||
                    noOfTeamsTextField.getText().isEmpty() || noOfTeachersTextField.getText().isEmpty() || topicChoiceBox.getSelectionModel().getSelectedItem() == null ||
                    gradeChoiceBox.getSelectionModel().getSelectedItem() == null || lectureRoomChoiceBox.getSelectionModel().getSelectedItem() == null ||
                    lecturerChosenTextField.getText().isEmpty() || schoolNameTextField.getText().isEmpty() ||
                    zipCodeTextField.getText().isEmpty() || cityTextField.getText().isEmpty() || !communeGroup.getSelectedToggle().isSelected() ||
                    schoolPhoneNumberTextField.getText().isEmpty() || eanNumberTextField.getText().isEmpty() ||
                    contactPersonTextField.getText().isEmpty() || phoneNumberTextField.getText().isEmpty() || emailTextField.getText().isEmpty()) {
                GeneralController.showAlertBox(Alert.AlertType.WARNING, "Tjek alle felter", "Et eller flere felter mangler input");
            }

            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
            alert2.setContentText("Er den indtastede information korrekt?");

            Optional<ButtonType> alertChoice2 = alert2.showAndWait();

            LectureBooking editedBooking = overwriteSelectedLectureBooking();
            if (editedBooking.getLectureRoom().getType() != LectureRoomType.WRONG_ROOM) {
                if (alertChoice2.get() == ButtonType.OK) {
                    try {
                        closeWindow();
                        bda.editLecBook(editedBooking);
                        msc.refetchBookingsFromDataBase();
                        msc.getBookingTableView().getSelectionModel().select(null);
                        msc.displayInformationOfSelectedBooking(msc.getBookingTableView());
                    } catch (SQLException e1) {
                        try {
                            bda = BookingDataAccessor.connect();
                        } catch (ClassNotFoundException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
