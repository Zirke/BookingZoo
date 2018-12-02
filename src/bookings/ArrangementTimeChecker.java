package bookings;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

public class ArrangementTimeChecker {
    private HashMap<LocalDateTime, ArrangementBooking> ArrTimeHashMap;
    private ArrangementBooking tempBook;

    public ArrangementTimeChecker(HashMap<LocalDateTime, ArrangementBooking> arrTimeHashMap, ArrangementBooking tempBook) {
        ArrTimeHashMap = arrTimeHashMap;
        this.tempBook = tempBook;
    }

    public boolean isChosenTimeOccupied(LocalDateTime date){
        return (ArrTimeHashMap.containsKey(date) && (!ArrTimeHashMap.get(date).equals(tempBook))) &&
                (ArrTimeHashMap.containsKey(date.plusMinutes(1)) && (!ArrTimeHashMap.get(date.plusMinutes(1)).equals(tempBook)));
    }

    public ArrangementBooking alert(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText("For den valgte tidsrum og dato er der allerede \n2 børnefødselsdage");

        Optional<ButtonType> alertChoice = alert.showAndWait();
        if (alertChoice.isPresent() && alertChoice.get() == ButtonType.OK) {
            return null;
        }
        return null;
    }
}
