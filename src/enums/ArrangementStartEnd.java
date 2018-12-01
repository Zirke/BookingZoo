package enums;

import exception.InvalidEnumException;

public enum ArrangementStartEnd {

    CHOICE_ONE {
        @Override
        public String toString() {
            return "10:00:00";
        }
    },
    CHOICE_TWO {
        @Override
        public String toString() {
            return "12:30:00";
        }
    };

    public static ArrangementStartEnd timeChoice(String input) throws InvalidEnumException {
        switch (input) {
            case "10:00 - 12:00":
                return CHOICE_ONE;
            case "12:30 - 14:00":
                return CHOICE_TWO;
            default:
                throw new InvalidEnumException("Input string is not a valid time: " + input);
        }
    }

}
