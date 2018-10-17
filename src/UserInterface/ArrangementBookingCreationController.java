package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ArrangementBookingCreationController {
    @FXML
    private Button cancelButton;

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
