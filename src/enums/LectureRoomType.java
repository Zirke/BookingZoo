package enums;

public enum LectureRoomType {
    savannahType {
        @Override
        public String toString() {
            return "Savannelokale";
        }
    },
    biologicalType {
        @Override
        public String toString() {
            return "Biologisk lokale";
        }
    }
}
