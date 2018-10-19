package enums;

public enum FacilityState {
    occupied {
        @Override
        public String toString() {
            return "Optaget";
        }
    },
    unoccupied {
        @Override
        public String toString() {
            return "Ledig";
        }
    }
}
