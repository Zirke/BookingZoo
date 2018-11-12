package facilities;

import enums.FacilityState;
import enums.LectureRoomType;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LectureRoom)) return false;
        LectureRoom that = (LectureRoom) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
