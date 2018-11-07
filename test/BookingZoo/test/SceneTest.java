package BookingZoo.test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Ignore;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SceneTest {

    @Ignore
    public void start() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("C:\\Java-programmer\\GitHub\\BookingZoo\\src\\userInterface\\PromptScreen.fxml"));
            Stage primaryStage = new Stage();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            AnchorPane rootPane = (AnchorPane) primaryStage.getScene().getRoot();
            assertEquals(rootPane.getChildren().get(1).getAccessibleText(), "Alle");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
