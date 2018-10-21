package enums;

public enum LecturerStatus {
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
