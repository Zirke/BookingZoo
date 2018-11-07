package userInterface;

import bookings.Booking;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import statistics.Statistic;

import java.util.ArrayList;

public class StatisticController {
    public HBox hboxWithCharts;
    public VBox dataVBOx;
    private MainScreenController mainScreenController;
    private ArrayList<Booking> lectureBookings;

    void setMainScreenController(MainScreenController mainScreenController) {
        this.mainScreenController = mainScreenController;
    }

    void setLectureBookings(ArrayList<Booking> lectureBookings) {
        this.lectureBookings = lectureBookings;
    }

    void initialise(){
        Label amountOfStudent = new Label();
        

        dataVBOx.getChildren().add(amountOfStudent);
    }
}
