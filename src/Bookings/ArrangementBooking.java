package Bookings;

import Customers.Customer;
import enums.BookingStatus;
import enums.BookingType;
import facilities.Restaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class ArrangementBooking extends Booking {
    private FoodOrder menuChosen;
    private Restaurant restaurant;
    private String birthdayChildName;
    private int birthdayChildAge;
    private String formerParticipant;
    private String guide;

    public ArrangementBooking(int id, BookingType bookingType, BookingStatus bookingStatus,
                              LocalDate creationDate, LocalDateTime date, int participants, String customerComment,
                              String comment, FoodOrder menuChosen, Restaurant restaurant, String birthdayChildName,
                              int birthdayChildAge, String formerParticipant, String guide, String customerContactPerson,
                              String customerPhoneNumber, String customerEmail) {
        super(id, bookingType, bookingStatus, new Customer(customerContactPerson, customerPhoneNumber, customerEmail),
                creationDate, date, participants, customerComment, comment);
        this.menuChosen = menuChosen;
        this.restaurant = restaurant;
        this.birthdayChildName = birthdayChildName;
        this.birthdayChildAge = birthdayChildAge;
        this.formerParticipant = formerParticipant;
        this.guide = guide;
    }

    public ArrangementBooking(BookingType bookingType, BookingStatus bookingStatus,
                              LocalDate creationDate, LocalDateTime date, int participants, String customerComment,
                              String comment, FoodOrder menuChosen, Restaurant restaurant,
                              String birthdayChildName, int birthdayChildAge, String formerParticipant, String guide,
                              String customerContactPerson, String customerPhoneNumber, String customerEmail) {
        super(bookingType, bookingStatus, new Customer(customerContactPerson, customerPhoneNumber, customerEmail),
                creationDate, date, participants, customerComment, comment);
        this.menuChosen = menuChosen;
        this.restaurant = restaurant;
        this.birthdayChildName = birthdayChildName;
        this.birthdayChildAge = birthdayChildAge;
        this.formerParticipant = formerParticipant;
        this.guide = guide;
    }

    public ArrangementBooking(BookingType bookingType, BookingStatus bookingStatus, LocalDate creationDate, LocalDateTime date, int participants, String customerComment, String comment, FoodOrder menuChosen,
                              Restaurant restaurant, String birthdayChildName, int birthdayChildAge, String formerParticipant,
                              String guide, Customer customer) {
        super(bookingType, bookingStatus, customer,
                creationDate, date, participants, customerComment, comment);
        this.menuChosen = menuChosen;
        this.restaurant = restaurant;
        this.birthdayChildName = birthdayChildName;
        this.birthdayChildAge = birthdayChildAge;
        this.formerParticipant = formerParticipant;
        this.guide = guide;
    }

    public ArrangementBooking(BookingStatus bookingStatus, int participants, FoodOrder menuChosen, String birthdayChildName,
                              int birthdayChildAge, String guide, String customerContactPerson, String customerPhoneNumber,
                              String customerEmail) {
        super(bookingStatus, participants, new Customer(customerContactPerson, customerPhoneNumber, customerEmail));
        this.menuChosen = menuChosen;
        this.birthdayChildName = birthdayChildName;
        this.birthdayChildAge = birthdayChildAge;
        this.guide = guide;
    }

    public FoodOrder getMenuChosen() {
        return menuChosen;
    }

    public void setMenuChosen(FoodOrder menuChosen) {
        this.menuChosen = menuChosen;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getBirthdayChildName() {
        return birthdayChildName;
    }

    public void setBirthdayChildName(String birthdayChildName) {
        this.birthdayChildName = birthdayChildName;
    }

    public int getBirthdayChildAge() {
        return birthdayChildAge;
    }

    public void setBirthdayChildAge(int birthdayChildAge) {
        this.birthdayChildAge = birthdayChildAge;
    }

    public String getFormerParticipant() {
        return formerParticipant;
    }

    public void setFormerParticipant(String formerParticipant) {
        this.formerParticipant = formerParticipant;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    /*public void updateArrangementDatabase(ArrangementBooking arb, Connection con) throws SQLException {
        String sql = "UPDATE arrangement_booking SET date=(?),time_slot=(?),children_amount=(?)," +
                "child_name=(?),age=(?),contact_person=(?),phone_number=(?),email=(?),previous_customer=(?)," +
                "food_choice=(?),customer_comment=(?),user_comment=(?) WHERE unique_id=(?)";

        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(13, arb.getId());
        pstmt.setString(1, arb.getDateTime());
        pstmt.setString(2, arb.getTime());
        pstmt.setInt(3, Integer.parseInt(arb.getNoOfChildren()));
        pstmt.setString(4, arb.getBirthdayChildName());
        pstmt.setInt(5, Integer.parseInt(arb.getBirthdayChildAge()));
        pstmt.setString(6, arb.getContactPerson());
        pstmt.setString(7, arb.getPhoneNumber());
        pstmt.setString(8, arb.getEmail());
        pstmt.setInt(9, Integer.parseInt(arb.getFormerParticipant()));
        pstmt.setString(10, arb.getChoiceOfMenu());
        pstmt.setString(11, arb.getComment());
        pstmt.setString(12, arb.getComment());

        pstmt.executeUpdate();
    }
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrangementBooking)) return false;
        ArrangementBooking that = (ArrangementBooking) o;
        return Objects.equals(menuChosen, that.menuChosen) &&
                Objects.equals(restaurant, that.restaurant) &&
                Objects.equals(birthdayChildName, that.birthdayChildName) &&
                Objects.equals(birthdayChildAge, that.birthdayChildAge) &&
                Objects.equals(formerParticipant, that.formerParticipant) &&
                Objects.equals(guide, that.guide);
    }

    @Override
    public int hashCode() {

        return Objects.hash(menuChosen, restaurant, birthdayChildName, birthdayChildAge, formerParticipant, guide);
    }

    @Override
    public String toString() {
        return getBookingType() + "\t \t \t" + getCustomer().getContactPerson() + "\t \t \t" + getDateTime();
    }
}
