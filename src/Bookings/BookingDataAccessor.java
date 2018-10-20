package Bookings;

import enums.BookingStatus;
import enums.BookingType;
import enums.FacilityState;
import enums.LectureRoomType;
import facilities.LectureRoom;
import facilities.Restaurant;

import java.sql.*;
import java.util.ArrayList;

public class BookingDataAccessor {
    private Connection connection;

    public BookingDataAccessor(String driverClassName, String host, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName(driverClassName);
        connection = DriverManager.getConnection(host, user, password);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<ArrangementBooking> fetchArrBooks() throws SQLException {

        ArrayList<ArrangementBooking> arr = new ArrayList<>();

        String typeSpecific = "SELECT bookingid,food,restaurant," +
                "birthdaychildname,birthdaychildage,formerparticipant,guide FROM arrangement_booking ";

        String general = "SELECT bookingid,status,customerid,creationdate,date,time," +
                "participants,customercomment,usercomment FROM booking WHERE bookingid = (?)";

        Statement stmtSpecific = connection.createStatement();
        ResultSet rsTypeSpecific = stmtSpecific.executeQuery(typeSpecific);

        while (rsTypeSpecific.next()) {
            ArrangementBooking abook;

            PreparedStatement pstmtGeneral = connection.prepareStatement(general);
            pstmtGeneral.setInt(1, rsTypeSpecific.getInt("bookingid"));
            ResultSet rsGeneral = pstmtGeneral.executeQuery();
            rsGeneral.next();

            String customer = "SELECT customerid,contactperson,phonenumber,email FROM customer WHERE customerid = (?)";
            PreparedStatement pstmtCustomer = connection.prepareStatement(customer);
            pstmtCustomer.setInt(1, rsGeneral.getInt("customerid"));
            ResultSet rsCustomer = pstmtCustomer.executeQuery();
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

            if (abook != null) {
                arr.add(abook);
            }
        }
        return arr;
    }

    public ArrayList<LectureBooking> fetchLecBooks() throws SQLException {

        ArrayList<LectureBooking> arr = new ArrayList<>();

        String typeSpecific = "SELECT bookingid,lectureroom,lecturer,choiceoftopic,noofteams,noofteachers,grade " +
                "FROM lecture_booking";

        String general = "SELECT bookingid,status," +
                "customerid,creationdate,date,time,participants,customercomment,usercomment FROM booking WHERE bookingid = (?)";

        Statement stmtSpecific = connection.createStatement();
        ResultSet rsTypeSpecific = stmtSpecific.executeQuery(typeSpecific);

        while (rsTypeSpecific.next()) {
            LectureBooking lbook = null;

            PreparedStatement pstmtGeneral = connection.prepareStatement(general);
            pstmtGeneral.setInt(1, rsTypeSpecific.getInt("bookingid"));
            ResultSet rsGeneral = pstmtGeneral.executeQuery();
            rsGeneral.next();

            String customer = "SELECT customerid,contactperson,phonenumber,email FROM customer WHERE customerid = (?)";
            String customerSpecific = "SELECT customerid,schoolname,zipcode,city,commune,schoolphonenumber,eannumber " +
                    "FROM lecture_booking_customer WHERE customerid = (?)";

            PreparedStatement pstmtCustomer = connection.prepareStatement(customer);
            PreparedStatement pstmtSpecificCustomer = connection.prepareStatement(customerSpecific);
            pstmtCustomer.setInt(1, rsGeneral.getInt("customerid"));
            pstmtSpecificCustomer.setInt(1, rsGeneral.getInt("customerid"));
            ResultSet rsCustomer = pstmtCustomer.executeQuery();
            ResultSet rsCustomerSpecific = pstmtSpecificCustomer.executeQuery();
            rsCustomer.next();
            rsCustomerSpecific.next();

            lbook = new LectureBooking(
                    rsGeneral.getInt("bookingid"), BookingType.LECTUREBOOKING, BookingStatus.STATUS_ACTIVE,
                    rsGeneral.getString("creationdate"), rsGeneral.getString("date"), rsGeneral.getString("time"),
                    Integer.toString(rsGeneral.getInt("participants")), rsGeneral.getString("customercomment"),
                    rsGeneral.getString("usercomment"), new LectureRoom(FacilityState.occupied, LectureRoomType.biologicalType),
                    new Lecturer(), rsTypeSpecific.getString("choiceoftopic"),
                    Integer.toString(rsGeneral.getInt("participants")), Integer.toString(rsTypeSpecific.getInt("noofteams")),
                    Integer.toString(rsTypeSpecific.getInt("noofteachers")), rsTypeSpecific.getString("grade"),
                    rsCustomer.getString("contactperson"), rsCustomer.getString("phonenumber"),
                    rsCustomer.getString("email"), rsCustomerSpecific.getString("schoolname"),
                    Integer.toString(rsCustomerSpecific.getInt("zipcode")), rsCustomerSpecific.getString("city"),
                    Boolean.toString(rsCustomerSpecific.getBoolean("commune")), rsCustomerSpecific.getString("schoolphonenumber"),
                    Integer.toString(rsCustomerSpecific.getInt("eannumber"))
            );

            if (lbook != null) {
                arr.add(lbook);
            }
        }
        return arr;
    }

    public void createArrBookManually(ArrangementBooking abook) throws SQLException {
        //Insert data into booking table
        String general = "INSERT INTO booking (bookingtypeid, status, creationdate, date, time, participants, customercomment, usercomment)" +
                "VALUES ((2),(?),(?),(?),(?),(?),(?),(?))";

        PreparedStatement pstmtGeneral = connection.prepareStatement(general);
        pstmtGeneral.setString(1, abook.getBookingStatus().toString());
        pstmtGeneral.setString(2, abook.getCreationDate());
        pstmtGeneral.setString(3, abook.getDate());
        pstmtGeneral.setString(4, abook.getTime());
        pstmtGeneral.setInt(5, Integer.valueOf(abook.getParticipants()));
        pstmtGeneral.setString(6, abook.getCustomerComment());
        pstmtGeneral.setString(7, abook.getComment());
        pstmtGeneral.executeUpdate();

        //Get Auto-Generated ID of this booking and customer
        String getLastID = "SELECT bookingid,customerid FROM booking ORDER BY bookingid DESC LIMIT 1";

        Statement stmt = connection.prepareStatement(getLastID);
        ResultSet rs = ((PreparedStatement) stmt).executeQuery();
        rs.next();
        int currentBookingID = rs.getInt(1);
        int currentCustomerID = rs.getInt(2);

        //Insert data into arrangement_booking table
        String typeSpecific = "INSERT INTO arrangement_booking (bookingid,bookingtypeid,food,restaurant,birthdaychildname,birthdaychildage,formerparticipant,guide)" +
                "VALUES ((?),(2),(?),(?),(?),(?),(?),(?))";

        PreparedStatement pstmtTypeSpecific = connection.prepareStatement(typeSpecific);
        pstmtTypeSpecific.setInt(1, currentBookingID);
        pstmtTypeSpecific.setString(2, abook.getMenuChosen().toString());
        pstmtTypeSpecific.setInt(3, 2);
        pstmtTypeSpecific.setString(4, abook.getBirthdayChildName());
        pstmtTypeSpecific.setInt(5, Integer.valueOf(abook.getBirthdayChildAge()));
        pstmtTypeSpecific.setBoolean(6, Boolean.parseBoolean(abook.getFormerParticipant()));
        pstmtTypeSpecific.setString(7, abook.getGuide());
        pstmtTypeSpecific.executeUpdate();

        //Insert data into customer table
        String customer = "INSERT INTO customer (customerid,contactperson,phonenumber,email)" +
                "VALUES ((?),(?),(?),(?))";

        PreparedStatement pstmtCustomer = connection.prepareStatement(customer);
        pstmtCustomer.setInt(1, currentCustomerID);
        pstmtCustomer.setString(2, abook.getCustomer().getContactPerson());
        pstmtCustomer.setString(3, abook.getCustomer().getPhoneNumber());
        pstmtCustomer.setString(4, abook.getCustomer().getEmail());
        pstmtCustomer.executeUpdate();
    }
}
