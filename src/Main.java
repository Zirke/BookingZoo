import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/*
  Main Application.
*/

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("UserInterface/ChoiceOfType.fxml"));
            primaryStage.setScene(new Scene(root));
            //primaryStage.setTitle("Aalborg Zoo Booking System");
            //primaryStage.setMaximized(true);
            primaryStage.show();
            root.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
