package enums;

public enum LectureRoomType {
    SAVANNAH_TYPE {
        @Override
        public String toString() {
            return "Savannelokale";
        }
    },
    BIOLOGICAL_TYPE {
        @Override
        public String toString() {
            return "Biologisk lokale";
        }
    }
}
