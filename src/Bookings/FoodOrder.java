package Bookings;

public class FoodOrder {
    private String orderDate;
    private String cancellationDate;
    private String menuChosen;

    public FoodOrder(String orderDate, String cancellationDate, String menuChosen) {
        this.orderDate = orderDate;
        this.cancellationDate = cancellationDate;
        this.menuChosen = menuChosen;
    }

    public FoodOrder(String menuChosen) {
        this.menuChosen = menuChosen;
    }

    public FoodOrder(String orderDate, String menuChosen) {
        this.orderDate = orderDate;
        this.menuChosen = menuChosen;
    }

    public FoodOrder() {
    }
}
