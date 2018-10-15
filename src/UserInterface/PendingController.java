package UserInterface;

import Bookings.LectureBooking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Cell;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class PendingController {
    @FXML
    private ChoiceBox roomChoiceBox;

    @FXML
    private ListView bookingList;

    @FXML
    public void initialize() {
        LectureBooking booking1 = new LectureBooking("Skoletjeneste", "Peter Jensen", "05/12/2018");
        LectureBooking booking2 = new LectureBooking("Skoletjeneste", "Peter Jensen", "05/12/2018");
        LectureBooking booking3 = new LectureBooking("Skoletjeneste", "Peter Jensen", "05/12/2018");
        LectureBooking booking4 = new LectureBooking("Skoletjeneste", "Peter Jensen", "05/12/2018");
        LectureBooking booking5 = new LectureBooking("Skoletjeneste", "Peter Jensen", "05/12/2018");
        LectureBooking booking6 = new LectureBooking("Skoletjeneste", "Peter Jensen", "05/12/2018");

        ArrayList<LectureBooking> listOfBookings = new ArrayList<>();
        listOfBookings.add(booking1);
        listOfBookings.add(booking2);
        listOfBookings.add(booking3);
        listOfBookings.add(booking4);
        listOfBookings.add(booking5);
        listOfBookings.add(booking6);

        loadBookingsToListView(listOfBookings);

    }

    private void loadBookingsToListView(ArrayList<LectureBooking> listOfBookings) {
        ObservableList<LectureBooking> bookings = FXCollections.observableArrayList();
        for (LectureBooking booking : listOfBookings) {
            bookings.addAll(booking);
        /*
        ObservableList<String> bookings = FXCollections.observableArrayList(
                "Type" +  "\tKontaktperson" + "Dato", "Type" + "Kontaktperson" + "Dato", "Type" + "Kontaktperson" + "Dato"
        );
        */
        bookingList.setItems(bookings);

    }
}
