package UserInterface;

import Bookings.Booking;
import exception.IllegalBookingTypeException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChoiceOfTypeController {



    @FXML
    public void setOnMouseClicked(javafx.event.ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent root = loader.load();

            MainScreenController controller = loader.getController();
            Button chosenBtn = (Button) actionEvent.getSource();
            String type = chosenBtn.getText();
            if(type.equals("Børnefødselsdag")){
                controller.setTypeOfBooking("Børnefødselsdag");
                controller.initialiseChoiceOfBooking();
            }else if(type.equals( "Skoletjeneste")){
                controller.setTypeOfBooking("Skoletjeneste");
                controller.initialiseChoiceOfBooking();
            } else if(type.equals("Alle")){
                controller.setTypeOfBooking("Alle");
                controller.initialiseChoiceOfBooking();
            }else{
                throw new IllegalBookingTypeException();
            }

            Stage stage = new Stage();
            stage.setTitle("Aalborg Zoo Booking System");
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
            root.requestFocus();
            stage.show();
            Stage currentStage = (Stage) chosenBtn.getScene().getWindow();
            currentStage.close();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}
