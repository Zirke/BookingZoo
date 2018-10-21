package UserInterface;

import Bookings.BookingDataAccessor;
import Bookings.LectureBooking;
import Bookings.Lecturer;
import enums.*;
import facilities.LectureRoom;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Date;

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
    private TextField timeTextField, noOfPupilsTextField, noOfTeamsTextField, noOfTeachersTextField, gradeTextField,
            lecturerChosenTextField, schoolNameTextField, zipCodeTextField, cityTextField, schoolPhoneNumberTextField,
            eanNumberTextField, contactPersonTextField, phoneNumberTextField, emailTextField;

    @FXML
    private TextArea customerCommentTextArea, commentTextArea;

    @FXML
    private ToggleGroup communeGroup;

    @FXML
    private ChoiceBox topicChoiceBox, lectureRoomChoiceBox;

    @FXML
    public void initialize() {
        topicChoiceBox.getItems().addAll("Dyr derhjemme", "Hverdagen i Zoo", "Krybdyr", "Grønlands dyr",
                "Afrikas savanner", "Aktiveringsværksted", "Sanseoplevelser", "Dyrs tilpasning og forskelligheder (Udskoling)",
                "Evolution/Klassifikation (Gymnasium)", "Aalborg Zoo som virksomhed (Handelsskole)");

        lectureRoomChoiceBox.getItems().addAll("Savannelokale", "Biologisk lokale");

        createAndCloseButton.setOnMouseClicked(e -> {
            try {
                createNewLecturetBookingFromInput();
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });
    }

    /*"Hverdagen i Zoo" and "Aalborg Zoo som virksomhed" does not occupy lecture rooms*/

    @FXML
    public void createNewLecturetBookingFromInput() throws SQLException, ClassNotFoundException {
        String date = datePicker.getValue().toString();
        String time = timeTextField.getText();
        String numberOfPupils = noOfPupilsTextField.getText();
        String numberOfTeams = noOfTeamsTextField.getText();
        String numberOfTeachers = noOfTeachersTextField.getText();
        String topicChoice = topicChoiceBox.getSelectionModel().getSelectedItem().toString();
        String grade = gradeTextField.getText();
        //LectureRoom lectureRoomChosen = (LectureRoom) lectureRoomChoiceBox.getSelectionModel().getSelectedItem();
        String lecturerChosen = lecturerChosenTextField.getText();
        String schoolName = schoolNameTextField.getText();
        String zipCode = zipCodeTextField.getText();
        String city = cityTextField.getText();
        String commune = communeGroup.getSelectedToggle().getProperties().toString();
        String schoolPhoneNumber = schoolPhoneNumberTextField.getText();
        String eanNumber = eanNumberTextField.getText();
        String contactPerson = contactPersonTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String email = emailTextField.getText();

        String customerComment = customerCommentTextArea.getText();
        String comment = commentTextArea.getText();

        LectureBooking lbook = new LectureBooking(BookingType.LECTUREBOOKING, BookingStatus.STATUS_ACTIVE,
                new Date().toString(), date, time, numberOfPupils, customerComment, comment, new LectureRoom(FacilityState.OCCUPIED, LectureRoomType.biologicalType),
                new Lecturer(lecturerChosen, LecturerStatus.OCCUPIED), topicChoice, numberOfTeams, numberOfTeachers, grade, contactPerson, phoneNumber,
                email, schoolName, zipCode, city, commune, schoolPhoneNumber, eanNumber);


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
}
