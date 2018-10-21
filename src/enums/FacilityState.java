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
    }
}
