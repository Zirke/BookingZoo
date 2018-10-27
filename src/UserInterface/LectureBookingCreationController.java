package UserInterface;

import Bookings.BookingDataAccessor;
import Bookings.LectureBooking;
import Bookings.Lecturer;
import PostToCalendars.PostToGoogle;
import enums.*;
import facilities.LectureRoom;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mortbay.util.IO;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static UserInterface.EditLectureBookingController.timeFieldCreation;
import static UserInterface.EditLectureBookingController.topicChoiceBoxCreation;

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
    private ChoiceBox topicChoiceBox, lectureRoomChoiceBox;
    @FXML
    public Spinner hourSpinner;
    @FXML
    public Spinner minuteSpinner;


    @FXML
    public void initialize() {
        topicChoiceBoxCreation(topicChoiceBox);
        lectureRoomChoiceBox.getItems().addAll("Savannelokale", "Biologisk lokale");
        createBookingButton();

        textfieldWithOnlyNumbers(noOfPupilsTextField);
        textfieldWithOnlyNumbers(noOfTeamsTextField);
        textfieldWithOnlyNumbers(noOfTeachersTextField);
        textfieldWithOnlyNumbers(schoolPhoneNumberTextField);
        textfieldWithOnlyNumbers(gradeTextField);
        textfieldWithOnlyNumbers(zipCodeTextField);
        textfieldWithOnlyNumbers(eanNumberTextField);

        timeFieldCreation(hourSpinner,minuteSpinner);
        hourSpinner.getValueFactory().setValue(10);
        cancelButton.setOnMouseClicked(e -> closeWindow());
    }

    /*"Hverdagen i Zoo" and "Aalborg Zoo som virksomhed" does not occupy lecture rooms*/

    @FXML
    private void createNewLectureBookingFromInput() throws SQLException, ClassNotFoundException, IOException, GeneralSecurityException {
        LocalDate tempDate = datePicker.getValue();
        LocalTime tempTime = LocalTime.parse(hourSpinner.getValue().toString() + ":" + minuteSpinner.getValue().toString());
        LocalDateTime date = LocalDateTime.of(tempDate,tempTime);
        int numberOfPupils = Integer.parseInt(noOfPupilsTextField.getText());
        int numberOfTeams = Integer.parseInt(noOfTeamsTextField.getText());
        int numberOfTeachers = Integer.parseInt(noOfTeachersTextField.getText());
        ChoiceOfTopic topicChoice = null;

        topicChoice = topicChoice.topicChosen(topicChoiceBox.getSelectionModel().getSelectedItem().toString());

        int grade = Integer.parseInt(gradeTextField.getText());
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

        bda = new BookingDataAccessor(
                "org.postgresql.Driver",
                "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
                "jyjczxth",
                "nw51BNKhctporjIFT5Qhhm72jwGVJK95"
        );
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
