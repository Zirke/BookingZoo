package enums;

import customExceptions.InvalidEnumException;

public enum RestaurantType {
    ROOM1{
        @Override
        public String toString() {
            return "Lokale 1";
        }
    },
    ROOM2{
        @Override
        public String toString() {
            return "Lokale 2";
        }
    },
    CANTEEN{
        @Override
        public String toString() {
            return "Kantine";
        }
    };

    public static RestaurantType roomTypeChoice(String input) throws InvalidEnumException {

        switch (input) {
            case "Lokale 1":
                return ROOM1;
            case "Lokale 2":
                return ROOM2;
            case "Kantine":
                return CANTEEN;
            default:
                throw new InvalidEnumException("Input string is not a valid restaurant room: " + input);
        }
    }
}
