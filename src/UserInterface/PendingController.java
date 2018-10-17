package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.Booking;
import Bookings.LectureBooking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;

public class PendingController {
    @FXML
    private ChoiceBox roomChoiceBox;
    @FXML
    private GridPane bookingInformationPane;
    @FXML
    private Button createNewBookingButton, activeButton;
    @FXML
    private TextArea customerCommentArea;
    @FXML
    private ListView bookingListView;
    @FXML
    private Label bookingTypeLabel, bookingStatusLabel, dateLabel, timeLabel, pupilNoLabel, teamNoLabel, teacherNoLabel,
            gradeLabel, topicChoiceLabel, schoolNameLabel, schoolPhoneNumberLabel, zipcodeLabel, cityLabel, communeLabel,
            phoneNumberLabel, contactPersonLabel, emailLabel, eanLabel;

    @FXML
    public void initialize() {
        LectureBooking booking1 = new LectureBooking(Booking.bookingType.Skoletjeneste, "12/10/2019",
                "12:45", "Jens Jensen", "30406010", "mail@mail.com",
                "Kommentaren her er lol", "5", "5", "5", "5",
                "Dyr Derhjemme", "Aalborg Skole", "9000", "Aalborg",
                "30124531", "324324", "1561561561");

        ArrangementBooking booking2 = new ArrangementBooking(Booking.bookingType.Boernefoedselsdag, "28/12/2018",
                "10:00", "Gitte Hansen", "97744210", "mail@mail.com",
                "Min datter har nøddeallergi", "17", "Laura Hansen",
                "9", "Nej", ArrangementBooking.choiceOfMenu.MENU_ONE);

        LectureBooking booking3 = new LectureBooking(Booking.bookingType.Skoletjeneste, "12/10/2019",
                "12:45", "Peter Petersen", "30406010", "mail@mail.com",
                "Kommentaren her er lol", "5", "5", "5", "5",
                "Dyr Derhjemme", "Aalborg Skole", "9000", "Aalborg",
                "30124531", "324324", "1561561561");

        ArrayList<Booking> listOfBookings = new ArrayList<>();
        listOfBookings.add(booking1);
        listOfBookings.add(booking2);
        listOfBookings.add(booking3);

        loadBookingsToListView(listOfBookings);

        /* Event handlers */
        bookingListView.setOnMouseClicked(e -> showSelectedBookingInformation());

        createNewBookingButton.setOnMouseClicked(e -> changeToActiveBookings());
    }

    private void loadBookingsToListView(ArrayList<Booking> listOfBookings) {
        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        for (Booking booking : listOfBookings) {
            bookings.addAll(booking);
        }
        bookingListView.setItems(bookings);
    }

    private void showSelectedBookingInformation() {
        //for (Booking temp : listOfBookings) {
        if (bookingListView.getSelectionModel().getSelectedItem() instanceof LectureBooking) {
            lectureBookingInformation((LectureBooking) bookingListView.getSelectionModel().getSelectedItem());
        } else if (bookingListView.getSelectionModel().getSelectedItem() instanceof ArrangementBooking) {
            arrangementBookingInformation((ArrangementBooking) bookingListView.getSelectionModel().getSelectedItem());
        }
    }

    //TODO change the statusLabel according to the status of the booking
    private void lectureBookingInformation(LectureBooking selectedBooking) {
        bookingTypeLabel.setText((selectedBooking.getType().toString()));
        bookingStatusLabel.setText(selectedBooking.getStatus().toString());
        dateLabel.setText(selectedBooking.getDate());
        timeLabel.setText(selectedBooking.getTime());
        pupilNoLabel.setText(selectedBooking.getNoOfPupils());
        teamNoLabel.setText(selectedBooking.getNoOfTeams());
        teacherNoLabel.setText(selectedBooking.getNoOfTeachers());
        gradeLabel.setText(selectedBooking.getGrade());
        topicChoiceLabel.setText(selectedBooking.getChoiceOfTopic());
        schoolNameLabel.setText(selectedBooking.getSchoolName());
        schoolPhoneNumberLabel.setText(selectedBooking.getSchoolPhoneNumber());
        zipcodeLabel.setText(selectedBooking.getZipCode());
        cityLabel.setText(selectedBooking.getCity());
        communeLabel.setText(selectedBooking.getCommune());
        phoneNumberLabel.setText(selectedBooking.getPhoneNumber());
        contactPersonLabel.setText(selectedBooking.getContactPerson());
        emailLabel.setText(selectedBooking.getEmail());
        eanLabel.setText(selectedBooking.getEanNumber());
        customerCommentArea.setText(selectedBooking.getComment());
        customerCommentArea.setEditable(false);
    }

    private void arrangementBookingInformation(ArrangementBooking selectedBooking) {
        System.out.println("Arrangement booking clicked: " + selectedBooking.toString());
    }

    @FXML
    private void changeToActiveBookings() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateNewBooking.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (root != null) {
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.showAndWait();
        }
    }

}

