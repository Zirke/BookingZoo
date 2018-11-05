package bookings;

import enums.ChoiceOfMenu;

public class FoodOrder {
    private String orderDate;
    private String cancellationDate;
    private ChoiceOfMenu choiceOfMenu;

    public FoodOrder(String orderDate, String cancellationDate, ChoiceOfMenu choiceOfMenu) {
        this.orderDate = orderDate;
        this.cancellationDate = cancellationDate;
        this.choiceOfMenu = choiceOfMenu;
    }

    public FoodOrder(ChoiceOfMenu choiceOfMenu) {
        this.choiceOfMenu = choiceOfMenu;
    }

    public FoodOrder(String orderDate, ChoiceOfMenu choiceOfMenu) {
        this.orderDate = orderDate;
        this.choiceOfMenu = choiceOfMenu;
    }

    public FoodOrder() {
    }


    public ChoiceOfMenu getChoiceOfMenu() {
        return choiceOfMenu;
    }

    @Override
    public String toString() {
        return choiceOfMenu.toString();
    }
}
