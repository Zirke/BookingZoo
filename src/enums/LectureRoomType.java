package enums;

import customExceptions.InvalidEnumException;

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
    },
    NOT_CHOSEN {
        @Override
        public String toString() {
            return "Intet lokale valgt";
        }
    };

    public static LectureRoomType roomTypeChoice(String input) throws InvalidEnumException {

        switch (input) {
            case "Savannelokale":
                return SAVANNAH_TYPE;
            case "Biologisk lokale":
                return BIOLOGICAL_TYPE;
            case "Intet lokale valgt":
                return NOT_CHOSEN;
            default:
                throw new InvalidEnumException("Input string is not a valid lecture room: " + input);
        }
    }
}
