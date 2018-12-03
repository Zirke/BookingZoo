package facilities;

import bookings.LectureBooking;
import enums.ChoiceOfTopic;
import enums.FacilityState;
import enums.LectureRoomType;
import exception.IllegalFacilityException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

public class FacilityChecker {
    private HashMap<LocalDateTime, LectureBooking> lecRoomHashMap;
    private LectureBooking selectedLectureBooking;

    public FacilityChecker(HashMap<LocalDateTime, LectureBooking> lecRoomHashMap, LectureBooking selectedLectureBooking) {
        this.lecRoomHashMap = lecRoomHashMap;
        this.selectedLectureBooking = selectedLectureBooking;
    }

    public Boolean isChosenFacilityOccupied(int i){
        if(i == 0) {
            return isTimeAlreadyChosen(selectedLectureBooking.getDateTime()) &&
                    isFacilityOccupied(selectedLectureBooking.getDateTime());
        }else if(i == 1){
            return isTimeAlreadyChosen(selectedLectureBooking.getDateTime().plusMinutes(1)) &&
                    isFacilityOccupied(selectedLectureBooking.getDateTime().plusMinutes(1));
        }else{
            return null;
        }
    }

    private Boolean isTimeAlreadyChosen(LocalDateTime i){
        return lecRoomHashMap.containsKey(i) && (!lecRoomHashMap.get(i).equals(selectedLectureBooking));
    }

    private Boolean isFacilityOccupied(LocalDateTime i){
        return lecRoomHashMap.get(i).getLectureRoom().getState().equals(FacilityState.OCCUPIED) &&
                lecRoomHashMap.get(i).getLectureRoom().getType().equals(selectedLectureBooking.getLectureRoom().getType());
    }

    public void alertWhenFacilityException(String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ikke muligt");
        alert.setContentText(msg);

        Optional<ButtonType> alertChoice = alert.showAndWait();

        if (alertChoice.get() == ButtonType.OK) {
            selectedLectureBooking.getLectureRoom().setType(LectureRoomType.WRONG_ROOM);
        }
    }

    public void facilityCheckForUniqueTopics(){
        isFacilityLegal(ChoiceOfTopic.GROENDLANDS_DYR, LectureRoomType.BIOLOGICAL_TYPE);
        isFacilityLegal(ChoiceOfTopic.AFRIKAS_SAVANNER, LectureRoomType.BIOLOGICAL_TYPE);
        isFacilityLegal(ChoiceOfTopic.EVOLUTION, LectureRoomType.SAVANNAH_TYPE);
    }

    private void isFacilityLegal(ChoiceOfTopic topic, LectureRoomType room){
        try {
            if (selectedLectureBooking.getChoiceOfTopic().equals(topic) &&
                    selectedLectureBooking.getLectureRoom().getType().equals(room)) {
                throw new IllegalFacilityException(selectedLectureBooking.getLectureRoom());
            }
        }catch (IllegalFacilityException e){
            String facilityIncompatible = topic.toString() + " kan ikke afholdes i\n" + room.toString();
            alertWhenFacilityException(facilityIncompatible);
        }
    }
}
