package UserInterface;

import Bookings.Booking;
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

import java.sql.SQLException;
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

    public void setUpcomingBookings(ArrayList<Booking> upcomingBookings) {
        this.upcomingBookings = upcomingBookings;
    }

    public void setController(MainScreenController controller) {
        this.controller = controller;
    }

    public void initialize(){

        doneButton.setOnMouseClicked(e ->{
            Stage stage = (Stage) doneButton.getScene().getWindow();
            stage.close();
        });

        UpcomingBookingTable.getSelectionModel().selectedIndexProperty().addListener(e ->{
            controller.showSelectedBookingInformation(UpcomingBookingTable);

            Stage stage = (Stage) doneButton.getScene().getWindow();
            stage.close();
        });
    }

    void initData() {
        loadBookingsToTableView(upcomingBookings);
    }

    private void loadBookingsToTableView(ArrayList<Booking> listOfbooking) {
        if(listOfbooking.size() == 0) throw new NoUpcomingBookingException(); //unhandled exception.

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingType"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        dateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDateTime().toLocalDate().toString()));
        daysUntil.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(Duration.between( LocalDateTime.now(), cellData.getValue().getDateTime()).toDays()+1)));

        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        for (Booking booking : listOfbooking) {
            bookings.addAll(booking);
        }
        UpcomingBookingTable.setItems(bookings);
    }
}
