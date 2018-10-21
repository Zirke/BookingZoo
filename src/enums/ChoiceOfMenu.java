package enums;

public enum ChoiceOfMenu {
    MENU_ONE {
        @Override
        public String toString() {
            return "Kakao, boller og kage";
        }
    },
    MENU_TWO {
        @Override
        public String toString() {
            return "Frikadeller og dessert";
        }
    },
    MENU_THREE {
        @Override
        public String toString() {
            return "Pasta m. kødsovs og dessert";
        }
    },
    MENU_FOUR {
        @Override
        public String toString() {
            return "Nuggets og pommes frites";
        }
    },
    NO_FOOD {
        @Override
        public String toString() {
            return "Ingen mad";
        }
    },
}

