package userInterface;

import bookings.BookingDataAccessor;
import bookings.LectureBooking;
import bookings.Lecturer;
import enums.*;
import facilities.LectureRoom;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;


public class LectureBookingCreationController {

    private BookingDataAccessor bda;

    public void setBda(BookingDataAccessor bda) {
        this.bda = bda;
    }

    @FXML
    private Button createAndCloseButton, cancelButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField noOfPupilsTextField, noOfTeamsTextField, noOfTeachersTextField, gradeTextField,
            lecturerChosenTextField, schoolNameTextField, zipCodeTextField, cityTextField, schoolPhoneNumberTextField,
            eanNumberTextField, contactPersonTextField, phoneNumberTextField, emailTextField;

    @FXML
    private TextArea customerCommentTextArea, commentTextArea;

    @FXML
    private ToggleGroup communeGroup;

    @FXML
    private ChoiceBox timeChoiceBox, topicChoiceBox, lectureRoomChoiceBox;


    @FXML
    public void initialize() {
        createBookingButton();
        timeChoiceBox.getItems().addAll("10:15 - 11:15", "11:15 - 12:15", "12:15 - 13:15", "13:15 - 14:15");
        lectureRoomChoiceBox.getItems().addAll("Savannelokale", "Biologisk lokale");
        topicChoiceBox.getItems().addAll("Dyr derhjemme", "Hverdagen i Zoo", "Krybdyr", "Grønlands dyr",
                "Afrikas savanner", "Aktiveringsværksted", "Sanseoplevelser", "Dyrs tilpasning og forskelligheder (Udskoling)",
                "Evolution/Klassifikation (Gymnasium)", "Aalborg Zoo som virksomhed (Handelsskole)");

        textfieldWithOnlyNumbers(noOfPupilsTextField);
        textfieldWithOnlyNumbers(noOfTeamsTextField);
        textfieldWithOnlyNumbers(noOfTeachersTextField);
        textfieldWithOnlyNumbers(schoolPhoneNumberTextField);
        textfieldWithOnlyNumbers(zipCodeTextField);
        textfieldWithOnlyNumbers(eanNumberTextField);

        /*
        timeFieldCreation(hourSpinner,minuteSpinner);
        hourSpinner.getValueFactory().setValue(10);
        cancelButton.loadChosenBookingTypeToMainScreen(e -> closeWindow());
        */
    }

    /*"Hverdagen i Zoo" and "Aalborg Zoo som virksomhed" does not occupy lecture rooms*/

    @FXML
    private void createNewLectureBookingFromInput() throws SQLException, ClassNotFoundException, IOException, GeneralSecurityException {
        LocalDate tempDate = datePicker.getValue();
        //LocalTime tempTime = LocalTime.parse(hourSpinner.getValue().toString() + ":" + minuteSpinner.getValue().toString());
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

        LocalDateTime date = LocalDateTime.of(tempDate,tempTime);
        int numberOfPupils = Integer.parseInt(noOfPupilsTextField.getText());
        int numberOfTeams = Integer.parseInt(noOfTeamsTextField.getText());
        int numberOfTeachers = Integer.parseInt(noOfTeachersTextField.getText());
        ChoiceOfTopic topicChoice = null;

        topicChoice = ChoiceOfTopic.topicChosen(topicChoiceBox.getSelectionModel().getSelectedItem().toString());

        String grade = gradeTextField.getText();
        LectureRoom lectureRoomChosen = null;
        if (lectureRoomChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Savannelokale")) {
            lectureRoomChosen = new LectureRoom(FacilityState.OCCUPIED, LectureRoomType.SAVANNAH_TYPE);
        } else if (lectureRoomChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Biologisk lokale")) {
            lectureRoomChosen = new LectureRoom(FacilityState.OCCUPIED, LectureRoomType.BIOLOGICAL_TYPE);
        }

        String lecturerChosen = lecturerChosenTextField.getText();
        String schoolName = schoolNameTextField.getText();
        int zipCode = Integer.parseInt(zipCodeTextField.getText());
        String city = cityTextField.getText();
        RadioButton selectedCommuneBtn = (RadioButton) communeGroup.getSelectedToggle();
        String commune = selectedCommuneBtn.getText();
        String schoolPhoneNumber = schoolPhoneNumberTextField.getText();
        long eanNumber = Long.parseLong(eanNumberTextField.getText());
        String contactPerson = contactPersonTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String email = emailTextField.getText();

        String customerComment = customerCommentTextArea.getText();
        String comment = commentTextArea.getText();

        LectureBooking lbook = new LectureBooking(BookingType.LECTUREBOOKING, BookingStatus.STATUS_ACTIVE,
                LocalDate.now(), date, numberOfPupils, customerComment, comment, lectureRoomChosen,
                new Lecturer(lecturerChosen, LecturerStatus.OCCUPIED), topicChoice, numberOfTeams, numberOfTeachers,
                grade, contactPerson, phoneNumber, email, schoolName, zipCode, city, commune, schoolPhoneNumber, eanNumber);

        bda.createLecBookManually(lbook);
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void createBookingButton() {
        createAndCloseButton.setOnMouseClicked(e -> {
            if (datePicker.getValue() == null  || noOfPupilsTextField.getText().isEmpty() ||
                    noOfTeamsTextField.getText().isEmpty() || noOfTeachersTextField.getText().isEmpty() || topicChoiceBox.getSelectionModel().getSelectedItem() == null ||
                    gradeTextField.getText().isEmpty() || lectureRoomChoiceBox.getSelectionModel().getSelectedItem() == null ||
                    lecturerChosenTextField.getText().isEmpty() || schoolNameTextField.getText().isEmpty() ||
                    zipCodeTextField.getText().isEmpty() || cityTextField.getText().isEmpty() || !communeGroup.getSelectedToggle().isSelected() ||
                    schoolPhoneNumberTextField.getText().isEmpty() || eanNumberTextField.getText().isEmpty() ||
                    contactPersonTextField.getText().isEmpty() || phoneNumberTextField.getText().isEmpty() || emailTextField.getText().isEmpty()) {
                Alert alert1 = new Alert(Alert.AlertType.WARNING);
                alert1.setHeaderText("Tjek alle felter");
                alert1.setContentText("Et eller flere felter mangler information");
                alert1.showAndWait();
            } else {
                Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
                alert2.setContentText("Er den indtastede information korrekt?");

                Optional<ButtonType> alertChoice2 = alert2.showAndWait();

                if (alertChoice2.get() == ButtonType.OK) {
                    try {
                        createNewLectureBookingFromInput();
                        closeWindow();
                    } catch (SQLException | ClassNotFoundException | IOException | GeneralSecurityException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void textfieldWithOnlyNumbers(TextField var) {
        var.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                var.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
}