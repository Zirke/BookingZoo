package enums;

public enum ChoiceOfMenu {
    MENU_ONE {
        @Override
        public String toString() {
            return "Klassisk menu med kakao, boller og kage 98,-";
        }
    },
    MENU_TWO {
        @Override
        public String toString() {
            return "Menu med frikadeller og dessert 75,-";
        }
    },
    MENU_THREE {
        @Override
        public String toString() {
            return "Menu med pasta og kødsovs og dessert 75,-";
        }
    },
    MENU_FOUR {
        @Override
        public String toString() {
            return "Menu med nuggets og pommes frites 50,-";
        }
    },
    NO_FOOD {
        @Override
        public String toString() {
            return "Mad fra Skovbakken ikke ønsket";
        }
    },
}

