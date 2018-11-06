package enums;

import customExceptions.InvalidEnumException;
import javafx.scene.control.RadioButton;

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
    };

    public static ChoiceOfMenu getChoiceOfMenu(RadioButton menuChoiceButton) throws InvalidEnumException {

        switch (menuChoiceButton.getText()) {
            case "Kakao, boller og kage":
                return MENU_ONE;
            case "Frikadeller og dessert":
                return MENU_TWO;
            case "Pasta m. kødsovs og dessert":
                return MENU_THREE;
            case "Nuggets og pommes frites":
                return MENU_FOUR;
            case "Ingen mad":
                return NO_FOOD;
            default:
                throw new InvalidEnumException("Menu chosen is not a valid choice: " + menuChoiceButton.getText());
        }
    }
}

