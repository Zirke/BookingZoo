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
    };

    public static LectureRoomType roomTypeChoice(String input) {

        switch (input) {
            case "Savannelokale":
                return SAVANNAH_TYPE;
            case "Biologisk lokale":
                return BIOLOGICAL_TYPE;
            default:
                throw new IllegalArgumentException();
        }
    }
}
