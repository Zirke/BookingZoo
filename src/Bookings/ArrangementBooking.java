package Bookings;

import Customers.Customer;
import enums.BookingStatus;
import enums.BookingType;
import enums.FacilityState;
import facilities.Restaurant;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class ArrangementBooking extends Booking {
    private FoodOrder menuChosen;
    private Restaurant restaurant;
    private String noOfChildren;
    private String birthdayChildName;
    private String birthdayChildAge;
    private String formerParticipant;
    private String guide;

    public ArrangementBooking(int id, BookingType bookingType, BookingStatus bookingStatus,
                              String creationDate, String date, String time, String participants, String customerComment,
                              String comment, FoodOrder menuChosen, Restaurant restaurant, String noOfChildren,
                              String birthdayChildName, String birthdayChildAge, String formerParticipant, String guide,
                              String customerContactPerson, String customerPhoneNumber, String customerEmail)
    {
        super(id, bookingType, bookingStatus, new Customer(customerContactPerson, customerPhoneNumber, customerEmail),
                creationDate, date, time, participants, customerComment, comment);
        this.menuChosen = menuChosen;
        this.restaurant = restaurant;
        this.noOfChildren = noOfChildren;
        this.birthdayChildName = birthdayChildName;
        this.birthdayChildAge = birthdayChildAge;
        this.formerParticipant = formerParticipant;
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

    public String getNoOfChildren() {
        return noOfChildren;
    }

    public void setNoOfChildren(String noOfChildren) {
        this.noOfChildren = noOfChildren;
    }

    public String getBirthdayChildName() {
        return birthdayChildName;
    }

    public void setBirthdayChildName(String birthdayChildName) {
        this.birthdayChildName = birthdayChildName;
    }

    public String getBirthdayChildAge() {
        return birthdayChildAge;
    }

    public void setBirthdayChildAge(String birthdayChildAge) {
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


    public static ArrayList<ArrangementBooking> fetchArrBooks(Connection con) throws SQLException {

        ArrayList<ArrangementBooking> arr = new ArrayList<>();

        String typeSpecific = "SELECT bookingid,food,restaurant," +
                "birthdaychildname,birthdaychildage,formerparticipant,guide FROM arrangement_booking";

        String general = "SELECT bookingid,status," +
                "customerid,creationdate,date,time,participants,customercomment,usercomment FROM booking";

        Statement stmt = con.createStatement();
        Statement stmt2 = con.createStatement();
        ResultSet rsTypeSpecific = stmt.executeQuery(typeSpecific);
        ResultSet rsGeneral = stmt2.executeQuery(general);


        while (rsGeneral.next()) {
            ArrangementBooking abook = null;
            if (rsTypeSpecific.next() && rsTypeSpecific.getInt("bookingid") == rsGeneral.getInt("bookingid")) {

                String customer = "SELECT customerid,contactperson,phonenumber,email FROM customer WHERE customerid = (?)";
                PreparedStatement pstmt = con.prepareStatement(customer);
                pstmt.setInt(1, rsGeneral.getInt("customerid"));
                ResultSet rsCustomer = pstmt.executeQuery();
                rsCustomer.next();

                abook = new ArrangementBooking(
                        rsGeneral.getInt("bookingid"), BookingType.ARRANGEMENTBOOKING, BookingStatus.STATUS_ACTIVE,
                        rsGeneral.getString("creationdate"), rsGeneral.getString("date"), rsGeneral.getString("time"),
                        Integer.toString(rsGeneral.getInt("participants")), rsGeneral.getString("customercomment"),
                        rsGeneral.getString("usercomment"), new FoodOrder(), new Restaurant(FacilityState.occupied),
                        Integer.toString(rsGeneral.getInt("participants")), rsTypeSpecific.getString("birthdaychildname"),
                        Integer.toString(rsTypeSpecific.getInt("birthdaychildage")), Boolean.toString(rsTypeSpecific.getBoolean("formerparticipant")),
                        rsTypeSpecific.getString("guide"), rsCustomer.getString("contactperson"),
                        rsCustomer.getString("phonenumber"), rsCustomer.getString("email")
                );
                rsTypeSpecific.next();
            }
            if(abook != null) {
                arr.add(abook);
            }
        }
        return arr;
    }

    /*public void updateArrangementDatabase(ArrangementBooking arb, Connection con) throws SQLException {
        String sql = "UPDATE arrangement_booking SET date=(?),time_slot=(?),children_amount=(?)," +
                "child_name=(?),age=(?),contact_person=(?),phone_number=(?),email=(?),previous_customer=(?)," +
                "food_choice=(?),customer_comment=(?),user_comment=(?) WHERE unique_id=(?)";

        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(13, arb.getId());
        pstmt.setString(1, arb.getDate());
        pstmt.setString(2, arb.getTime());
        pstmt.setInt(3, Integer.parseInt(arb.getNoOfChildren()));
        pstmt.setString(4, arb.getBirthdayChildName());
        pstmt.setInt(5, Integer.parseInt(arb.getBirthdayChildAge()));
        pstmt.setString(6, arb.getContactPerson());
        pstmt.setString(7, arb.getPhoneNumber());
        pstmt.setString(8, arb.getEmail());
        pstmt.setInt(9, Integer.parseInt(arb.getFormerParticipant()));
        pstmt.setString(10, arb.getMenuChosen());
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
                Objects.equals(noOfChildren, that.noOfChildren) &&
                Objects.equals(birthdayChildName, that.birthdayChildName) &&
                Objects.equals(birthdayChildAge, that.birthdayChildAge) &&
                Objects.equals(formerParticipant, that.formerParticipant) &&
                Objects.equals(guide, that.guide);
    }

    @Override
    public int hashCode() {

        return Objects.hash(menuChosen, restaurant, noOfChildren, birthdayChildName, birthdayChildAge, formerParticipant, guide);
    }

    @Override
    public String toString() {
        return getBookingType() + "\t \t \t" + getCustomer().getContactPerson() + "\t \t \t" + getDate();
    }
}
