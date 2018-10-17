package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class LectureBookingCreationController {
    @FXML
    private Button cancelButton;
    @FXML
    private ChoiceBox topicChoiceBox, lectureRoomChoiceBox;

    @FXML
    public void initialize() {
        topicChoiceBox.getItems().addAll("Dyr derhjemme", "Hverdagen i Zoo", "Krybdyr", "Grønlands dyr",
                "Afrikas savanner", "Aktiveringsværksted", "Sanseoplevelser", "Dyrs tilpasning og forskelligheder (Udskoling)",
                "Evolution/Klassifikation (Gymnasium)", "Aalborg Zoo som virksomhed (Handelsskole)");

        lectureRoomChoiceBox.getItems().addAll("Savannelokale", "Biologisk lokale");
    }

    /*"Hverdagen i Zoo" and "Aalborg Zoo som virksomhed" does not occupy lecture rooms*/

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
