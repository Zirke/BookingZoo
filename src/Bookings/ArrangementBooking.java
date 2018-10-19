package Bookings;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class ArrangementBooking extends Booking {
    /*
    public enum choiceOfMenu {
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
    */
    private String noOfChildren;
    private String birthdayChildName;
    private String birthdayChildAge;
    private String formerParticipant;
    private String menuChosen;

    public ArrangementBooking(int id, bookingType type, String date, String time, String contactPerson, String phoneNumber,
                              String email, String comment, String noOfChildren, String birthdayChildName,
                              String birthdayChildAge, String formerParticipant, String menuChosen) {
        super(id, type, date, time, contactPerson, phoneNumber, email, comment);
        this.noOfChildren = noOfChildren;
        this.birthdayChildName = birthdayChildName;
        this.birthdayChildAge = birthdayChildAge;
        this.formerParticipant = formerParticipant;
        this.menuChosen = menuChosen;
    }

    public bookingType getType() {
        return type;
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

    public String getMenuChosen() {
        return menuChosen;
    }

    public void setMenuChosen(String menuChosen) {
        this.menuChosen = menuChosen;
    }

    public static ArrayList<ArrangementBooking> fetchArrBooks(Connection con) throws SQLException {

        ArrayList<ArrangementBooking> arr = new ArrayList<>();
        String sql = "SELECT * FROM arrangement_booking";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            ArrangementBooking abook = new ArrangementBooking(rs.getInt(1), bookingType.Boernefoedselsdag,
                    rs.getString(2), rs.getString(3),
                    rs.getString(7), rs.getString(8),
                    rs.getString(9), rs.getString(12),
                    String.valueOf(rs.getInt(4)), rs.getString(5),
                    String.valueOf(rs.getInt(6)), String.valueOf(rs.getInt(10)),
                    rs.getString(11));

            arr.add(abook);

        }
        return arr;
    }

    public void updateArrangementDatabase(ArrangementBooking arb, Connection con) throws SQLException {
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrangementBooking)) return false;
        ArrangementBooking that = (ArrangementBooking) o;
        return Objects.equals(noOfChildren, that.noOfChildren) &&
                Objects.equals(birthdayChildName, that.birthdayChildName) &&
                Objects.equals(birthdayChildAge, that.birthdayChildAge) &&
                Objects.equals(formerParticipant, that.formerParticipant) &&
                menuChosen.equals(that.menuChosen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noOfChildren, birthdayChildName, birthdayChildAge, formerParticipant, menuChosen);
    }

    @Override
    public String toString() {
        return getType() + "\t" +
                getContactPerson() + "\t" +
                getDate() + "\t" +
                getStatus();
    }
}
