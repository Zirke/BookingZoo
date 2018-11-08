package userInterface;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

class GeneralController {

    private static GeneralController me;

    GeneralController() {
    }

    public static GeneralController get() {
        if (me == null) {
            me = new GeneralController();
        }
        return me;
    }

    private EventHandler<MouseEvent> consumeMouseEventfilter = (MouseEvent mouseEvent) -> {
        if (((Toggle) mouseEvent.getSource()).isSelected()) {
            mouseEvent.consume();
        }
    };

    void addAlwaysOneSelectedSupport(final ToggleGroup toggleGroup) {
        toggleGroup.getToggles().addListener((ListChangeListener.Change<? extends Toggle> c) -> {
            while (c.next()) {
                for (final Toggle addedToggle : c.getAddedSubList()) {
                    addConsumeMouseEventfilter(addedToggle);
                }
            }
        });
        toggleGroup.getToggles().forEach(this::addConsumeMouseEventfilter);
    }

    private void addConsumeMouseEventfilter(Toggle toggle) {
        ((ToggleButton) toggle).addEventFilter(MouseEvent.MOUSE_PRESSED, consumeMouseEventfilter);
        ((ToggleButton) toggle).addEventFilter(MouseEvent.MOUSE_RELEASED, consumeMouseEventfilter);
        ((ToggleButton) toggle).addEventFilter(MouseEvent.MOUSE_CLICKED, consumeMouseEventfilter);
    }


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

    Alert showAlertBox(Alert.AlertType alertType, String title, String message) {
        Alert userCreationAlert = new Alert(alertType);
        userCreationAlert.setTitle(title);
        userCreationAlert.setHeaderText(null);
        userCreationAlert.setContentText(message);
        userCreationAlert.show();

        return userCreationAlert;
    }
}
