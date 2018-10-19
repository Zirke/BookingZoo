package facilities;

import enums.FacilityState;
import enums.LectureRoomType;

public class LectureRoom extends Facility {
    private LectureRoomType type;

    public LectureRoom(FacilityState state, LectureRoomType type) {
        super(state);
        this.type = type;
    }

    public LectureRoomType getType() {
        return type;
    }

    public void setType(LectureRoomType type) {
        this.type = type;
    }
}
