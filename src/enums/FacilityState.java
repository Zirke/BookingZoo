package enums;

public enum FacilityState {
    OCCUPIED {
        @Override
        public String toString() {
            return "Optaget";
        }
    },
    UNOCCUPIED {
        @Override
        public String toString() {
            return "Ledig";
        }
    },
    NOT_SPECIFIED {
        @Override
        public String toString() {
            return "Ikke valgt";
        }
    }
}
