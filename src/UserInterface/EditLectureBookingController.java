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
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private TextField noOfPupilsTextField, noOfTeamsTextField, noOfTeachersTextField, gradeTextField,
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
    @FXML
    public Spinner hourSpinner;
    @FXML
    public Spinner minutSpinner;


    public void initialize() throws SQLException, ClassNotFoundException {
        BookingDataAccessor bda = new BookingDataAccessor(
                "org.postgresql.Driver",
                "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
                "jyjczxth",
                "nw51BNKhctporjIFT5Qhhm72jwGVJK95");

        topicChoiceBoxCreation(topicChoiceBox);
        lectureRoomChoiceBox.getItems().addAll("Savannelokale", "Biologisk lokale");
        categoryChoiceBox.getItems().addAll("Afventende","Aktive","Færdige","Arkiverede");

        safeButtonPress(bda);

        textfieldWithOnlyNumbers(noOfPupilsTextField);
        textfieldWithOnlyNumbers(noOfTeamsTextField);
        textfieldWithOnlyNumbers(noOfTeachersTextField);
        textfieldWithOnlyNumbers(gradeTextField);
        textfieldWithOnlyNumbers(zipCodeTextField);
        textfieldWithOnlyNumbers(schoolPhoneNumberTextField);
        textfieldWithOnlyNumbers(phoneNumberTextField);
        textfieldWithOnlyNumbers(eanNumberTextField);

        timeFieldCreation(hourSpinner,minutSpinner);
    }

    void initData() {
        LectureBookingCustomer temp = (LectureBookingCustomer) selectedLectureBooking.getCustomer();

        //Lecture information
        datePicker.setValue(selectedLectureBooking.getDateTime().toLocalDate());
        timeFieldInitialisation();
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
        selectedLectureBooking.setDateTime(LocalDateTime.of(datePicker.getValue(), LocalTime.parse(hourSpinner.getValue().toString() + ":" + minutSpinner.getValue().toString())));
        //selectedLectureBooking.setTime(hourSpinner.getValue().toString() + ":" + minutSpinner.getValue().toString());
        selectedLectureBooking.setParticipants(Integer.parseInt(noOfPupilsTextField.getText()));
        selectedLectureBooking.setNoOfTeams(Integer.parseInt(noOfTeamsTextField.getText()));
        selectedLectureBooking.setNoOfTeachers(Integer.parseInt(noOfTeachersTextField.getText()));
        ChoiceOfTopic topicChoice = topicChosen(topicChoiceBox.getSelectionModel().getSelectedItem().toString());
        selectedLectureBooking.setChoiceOfTopic(topicChoice);
        selectedLectureBooking.setGrade(Integer.parseInt(gradeTextField.getText()));
        LectureRoomType roomTypeChoice = LectureRoomType.roomTypeChoice(lectureRoomChoiceBox.getSelectionModel().getSelectedItem().toString());
        LectureRoom foo = new LectureRoom(FacilityState.OCCUPIED, roomTypeChoice);
        selectedLectureBooking.setLectureRoom(foo);
        Lecturer bar = new Lecturer(lecturerTextField.getText());
        selectedLectureBooking.setLecturer(bar);
        BookingStatus statusChoice = BookingStatus.statusChosen(categoryChoiceBox.getSelectionModel().getSelectedItem().toString());
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

    public static void topicChoiceBoxCreation(ChoiceBox topicChoiceBox) {
        topicChoiceBox.getItems().addAll("Dyr derhjemme", "Hverdagen i Zoo", "Krybdyr", "Grønlands dyr",
                "Afrikas savanner", "Aktiveringsværksted", "Sanseoplevelser", "Dyrs tilpasning og forskelligheder (Udskoling)",
                "Evolution/Klassifikation (Gymnasium)", "Aalborg Zoo som virksomhed (Handelsskole)");
    }

    private void safeButtonPress(BookingDataAccessor bda) {
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
    }


    public static void timeFieldCreation(Spinner hourSpinner, Spinner minutSpinner) {
        SpinnerValueFactory<Integer> valueFactoryHour =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
        hourSpinner.setValueFactory(valueFactoryHour);
        hourSpinner.setEditable(true);

        SpinnerValueFactory<Integer> valueFactoryMinuts =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        minutSpinner.setValueFactory(valueFactoryMinuts);
        minutSpinner.setEditable(true);
    }

    private void timeFieldInitialisation() {
        hourSpinner.getValueFactory().setValue(selectedLectureBooking.getDateTime().toLocalTime().getHour());
        minutSpinner.getValueFactory().setValue(selectedLectureBooking.getDateTime().toLocalTime().getMinute());
    }

    //aendre navn po metode senere.
    private void textfieldWithOnlyNumbers(TextField var) {
        var.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                var.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
}
