import Bookings.BookingDataAccessor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/*
  Main Application.
*/

public class Main extends Application {
    BookingDataAccessor connection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws SQLException, ClassNotFoundException {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("UserInterface/MainScreen.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Aalborg Zoo Booking System");
            primaryStage.setMaximized(true);
            primaryStage.show();
            root.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
