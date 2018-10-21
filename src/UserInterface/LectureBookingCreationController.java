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
import java.time.LocalDate;
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
            if (datePicker.getValue() == null || timeTextField.getText().isEmpty() || noOfPupilsTextField.getText().isEmpty() ||
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
                    } catch (SQLException | ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }
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

        cancelButton.setOnMouseClicked(e -> closeWindow());
    }

    /*"Hverdagen i Zoo" and "Aalborg Zoo som virksomhed" does not occupy lecture rooms*/

    @FXML
    public void createNewLectureBookingFromInput() throws SQLException, ClassNotFoundException {
        LocalDate date = datePicker.getValue();
        String time = timeTextField.getText();
        int numberOfPupils = Integer.parseInt(noOfPupilsTextField.getText());
        int numberOfTeams = Integer.parseInt(noOfTeamsTextField.getText());
        int numberOfTeachers = Integer.parseInt(noOfTeachersTextField.getText());
        ChoiceOfTopic topicChoice = null;
        if (topicChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Dyr derhjemme")){
            topicChoice = ChoiceOfTopic.DYR_DERHJEMME;
        } else if (topicChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Hverdagen i Zoo")){
            topicChoice = ChoiceOfTopic.HVERDAG_ZOO;
        } else if (topicChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Krybdyr")){
            topicChoice = ChoiceOfTopic.KRYBDYR;
        } else if (topicChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Grønlands dyr")){
            topicChoice = ChoiceOfTopic.GROENDLANDS_DYR;
        } else if (topicChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Afrikas savanner")){
            topicChoice = ChoiceOfTopic.AFRIKAS_SAVANNER;
        } else if (topicChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Aktiveringsværksted")){
            topicChoice = ChoiceOfTopic.AKTIVERINGSVAERKSTED;
        } else if (topicChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Sanseoplevelser")){
            topicChoice = ChoiceOfTopic.SANSEOPLEVELSER;
        } else if (topicChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Dyrs tilpasning og forskelligheder (Udskoling)")){
            topicChoice = ChoiceOfTopic.DYRS_TILPASNING;
        } else if (topicChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Evolution/Klassifikation (Gymnasium)")){
            topicChoice = ChoiceOfTopic.EVOLUTION;
        } else if (topicChoiceBox.getSelectionModel().getSelectedItem().toString().equals("Aalborg Zoo som virksomhed (Handelsskole)")){
            topicChoice = ChoiceOfTopic.ZOO_SOM_VIRKSOMHED;
        }

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
                LocalDate.now(), date, time, numberOfPupils, customerComment, comment, lectureRoomChosen,
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
}
