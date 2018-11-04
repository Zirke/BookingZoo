package UserInterface;

import enums.BookingType;
import exception.IllegalBookingTypeException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class PromtScreenController {


    @FXML
    public void loadChosenBookingTypeToMainScreen(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent root = loader.load();

            MainScreenController controller = loader.getController();

            Button chosenBtn = (Button) actionEvent.getSource();
            String textOfBtn = chosenBtn.getText();

            Stage currentStage = (Stage) chosenBtn.getScene().getWindow();
            currentStage.close();

            if(textOfBtn.equals("Børnefødselsdag")){
                controller.setTypeOfBooking(BookingType.ARRANGEMENTBOOKING);
                controller.initialiseBookingType();
            }else if(textOfBtn.equals( "Skoletjeneste")){
                controller.setTypeOfBooking(BookingType.LECTUREBOOKING);
                controller.initialiseBookingType();
            } else if(textOfBtn.equals("Alle")){
                controller.setTypeOfBooking(BookingType.ALL_BOOKING_TYPES);
                controller.initialiseBookingType();
            }else{
                throw new IllegalBookingTypeException();
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Aalborg Zoo Booking System");
            stage.setMaximized(true);
            root.requestFocus();
            stage.show();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}
