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
    }
}
