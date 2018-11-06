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
    },
    STATUS_DELETED {
        @Override
        public String toString() {
            return "Slettet";
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
            case "Arkiveret":
                return STATUS_ARCHIVED;
            case "Slettet":
                return STATUS_DELETED;
            default:
                throw new IllegalArgumentException();
        }
    }
}
