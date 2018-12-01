package enums;

import exception.InvalidEnumException;

public enum BookingType {
    LECTUREBOOKING {
        @Override
        public String toString() {
            return "Skoletjeneste";
        }
    },
    ARRANGEMENTBOOKING {
        @Override
        public String toString() {
            return "Børnefødselsdag";
        }
    },
    ALL_BOOKING_TYPES {
        @Override
        public String toString() {
            return "Alle";
        }
    };

    public static BookingType getBookingType(String input) throws InvalidEnumException {

        switch (input) {
            case "Skoletjeneste":
                return LECTUREBOOKING;
            case "Børnefødselsdag":
                return ARRANGEMENTBOOKING;
            case "Alle":
                return ALL_BOOKING_TYPES;
                default:
                    throw new InvalidEnumException("Input string is not a valid booking type: " + input);
        }
    }


}