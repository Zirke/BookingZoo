package UserInterface;

import Bookings.Booking;
import Bookings.LectureBooking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class PendingController {
    @FXML
    private ChoiceBox roomChoiceBox;

    @FXML
    private ListView bookingList;

    @FXML
    private Label dateLabel, timeLabel, pupilNoLabel, teamNoLabel, teacherNoLabel, gradeLabel, topicChoiceLabel, schoolNameLabel, schoolPhoneNumberLabel, zipcodeLabel, cityLabel, communeLabel, phoneNumberLabel, contactPersonLabel;

    @FXML
    public void initialize() {
        LectureBooking booking1 = new LectureBooking(Booking.bookingType.Skoletjeneste, "12/10/2019", "12:45", "Jens Jensen", 30406010, "mail@mail.com", "Kommentaren her er lol", 5, 5, 5, 5, "Dyr Derhjemme", "Aalborg Skole", 9000, "Aalborg", 30124531, 324324);

        ArrayList<LectureBooking> listOfBookings = new ArrayList<>();
        listOfBookings.add(booking1);

        loadBookingsToListView(listOfBookings);
    }

    private void loadBookingsToListView(ArrayList<LectureBooking> listOfBookings) {
        ObservableList<LectureBooking> bookings = FXCollections.observableArrayList();
        for (LectureBooking booking : listOfBookings) {
            bookings.addAll(booking);
            if (bookingList.getSelectionModel().getSelectedItem().equals(booking)) {
                showSelectedBookingInformation(booking);
            }
        }
        bookingList.setItems(bookings);
    }

    private void showSelectedBookingInformation(LectureBooking selectedBooking) {
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
        communeLabel.setText(selectedBooking.get);


    }

}
