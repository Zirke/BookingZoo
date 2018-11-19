package userInterface;

import enums.BookingType;
import exception.IllegalBookingTypeException;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class PromptScreenController {

    @FXML
    private ProgressIndicator progressIndicator;

    public void initialize() {
        progressIndicator.setVisible(false);
        progressIndicator.setStyle(" -fx-progress-color: #f4b342;");
    }

    @FXML
    public void loadChosenBookingTypeToMainScreen(ActionEvent actionEvent) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent root = loader.load();

            MainScreenController controller = loader.getController();

            Button chosenBtn = (Button) actionEvent.getSource();
            String textOfBtn = chosenBtn.getText();

            Stage currentStage = (Stage) chosenBtn.getScene().getWindow();
            currentStage.getIcons().add(new Image("imageView/zooicon.png"));
            currentStage.close();

            switch (textOfBtn) {
                case "Børnefødselsdage":
                    controller.setTypeOfBooking(BookingType.ARRANGEMENTBOOKING);
                    //controller.showArrangementStatisticsInfo();
                    break;
                case "Skoletjenester":
                    controller.setTypeOfBooking(BookingType.LECTUREBOOKING);
                    //controller.showStatisticInfo();
                    break;
                case "Alle":
                    controller.setTypeOfBooking(BookingType.ALL_BOOKING_TYPES);
                    break;
                default:
                    throw new IllegalBookingTypeException();
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Aalborg Zoo Booking System");
            stage.setMaximized(true);
            root.requestFocus();
            stage.show();

            controller.showPendingBookingPopUp();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    public void setProgressIndicatorVisible(){
        progressIndicator.setVisible(true);
    }
}
