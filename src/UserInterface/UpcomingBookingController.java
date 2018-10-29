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
import java.util.ArrayList;

public class UpcomingBookingController {

    @FXML
    public TableColumn statusColumn, typeColumn, contactColumn;
    public TableColumn<Booking, String> dateColumn;
    public TableView UpcomingBookingTable;
    public Button doneButton;

    ArrayList<Booking> upcomingBookings;
    MainScreenController controller;

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
        UpcomingBookingTable.setOnMouseClicked(e -> {
            controller.showSelectedBookingInformation(UpcomingBookingTable);
            Stage stage = (Stage) doneButton.getScene().getWindow();
            stage.close();
        });
    }

    void initData() {
        try {
            loadBookingsToTableView(upcomingBookings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBookingsToTableView(ArrayList<Booking> listOfbooking) throws SQLException {
        if(listOfbooking.size() == 0) throw new NoUpcomingBookingException(); //unhandled exception.

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingType"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        dateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDateTime().toLocalDate().toString()));


        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        for (Booking booking : listOfbooking) {
            bookings.addAll(booking);
        }
        UpcomingBookingTable.setItems(bookings);

    }
}
