package enums;

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
    }
}
