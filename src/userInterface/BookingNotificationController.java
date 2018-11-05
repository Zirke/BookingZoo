package userInterface;

import bookings.ArrangementBooking;
import bookings.Booking;
import bookings.LectureBooking;
import enums.BookingType;
import exception.NoUpcomingBookingException;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BookingNotificationController {

    private ArrayList<Booking> upcomingBookings;
    private MainScreenController controller;

    @FXML
    public TableColumn<Booking, String> daysUntil;
    public TableColumn statusColumn, typeColumn, contactColumn;
    public TableColumn<Booking, String> dateColumn;
    public TableView UpcomingBookingTable;
    public Button doneButton;
    private BookingType typeOfBooking;

    public void setTypeOfBooking(BookingType typeOfBooking) {
        this.typeOfBooking = typeOfBooking;
    }

    public void setUpcomingBookings(ArrayList<Booking> upcomingBookings) {
        this.upcomingBookings = upcomingBookings;
    }

    public void setController(MainScreenController controller) {
        this.controller = controller;
    }

    public void initialize() {

        doneButton.setOnMouseClicked(e -> {
            Stage stage = (Stage) doneButton.getScene().getWindow();
            stage.close();
        });

        UpcomingBookingTable.getSelectionModel().selectedIndexProperty().addListener(e -> {
            controller.displayInformationOfSelectedBooking();

            Stage stage = (Stage) doneButton.getScene().getWindow();
            stage.close();
        });
    }

    void initData() {
        loadBookingsToTableView(upcomingBookings);
    }

    private void loadBookingsToTableView(ArrayList<Booking> listOfbooking) {
        ObservableList<Booking> bookings = FXCollections.observableArrayList();

        if (listOfbooking.size() == 0) throw new NoUpcomingBookingException(); //unhandled exception.

        switch (typeOfBooking) {
            case ALL_BOOKING_TYPES:
                for (Booking i : listOfbooking) {
                    bookings.add(i);
                }
                break;
            case LECTUREBOOKING:
                for (Booking i : listOfbooking) {
                    if (i.getClass().equals(LectureBooking.class)) {
                        bookings.add(i);
                    }
                }
                break;
            case ARRANGEMENTBOOKING:
                for (Booking i : listOfbooking) {
                    if (i.getClass().equals(ArrangementBooking.class)) {
                        bookings.add(i);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingType"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        dateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDateTime().toLocalDate().toString()));
        daysUntil.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(Duration.between(LocalDateTime.now(), cellData.getValue().getDateTime()).toDays() + 1)));

        UpcomingBookingTable.setItems(bookings);
    }
}
