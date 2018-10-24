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
            case "Aktive":
                return STATUS_ACTIVE;
            case "Færdige":
                return STATUS_DONE;
            case "Arkiverede":
                return STATUS_ARCHIVED;
            case "Slettede":
                return STATUS_DELETED;
            default:
                throw new IllegalArgumentException();
        }
    }
}
