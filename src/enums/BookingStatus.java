package enums;

public enum BookingStatus {
    STATUS_PENDING {
        @Override
        public String toString() {
            return "Afventende";
        }
    },
    STATUS_ACTIVE {
        @Override
        public String toString() {
            return "Aktiv";
        }
    },
    STATUS_DONE {
        @Override
        public String toString() {
            return "Færdig";
        }
    },
    STATUS_ARCHIVED {
        @Override
        public String toString() {
            return "Arkiveret";
        }
    };

    public static BookingStatus statusChosen(String input) {
        switch (input) {
            case "Afventende":
                return STATUS_PENDING;
            case "Aktiv":
                return STATUS_ACTIVE;
            case "Færdig":
                return STATUS_DONE;
            case "Aktiveret":
                return STATUS_ARCHIVED;
            default:
                throw new IllegalArgumentException();
        }
    }
}
