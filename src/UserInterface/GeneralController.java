package UserInterface;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

class GeneralController {

    void openNewPopUpWindow(String path) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (root != null) {
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            //stage.setTitle(windowTitle);
            stage.showAndWait();
        }
    }

    void showAlertBox(Alert.AlertType alertType, String title, String message) {
        Alert userCreationAlert = new Alert(alertType);
        userCreationAlert.setTitle(title);
        userCreationAlert.setHeaderText(null);
        userCreationAlert.setContentText(message);
        userCreationAlert.show();
    }
}
