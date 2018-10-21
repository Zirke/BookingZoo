package Bookings;

import Customers.LectureBookingCustomer;
import enums.BookingStatus;
import enums.BookingType;
import enums.FacilityState;
import enums.LectureRoomType;
import facilities.LectureRoom;
import facilities.Restaurant;

import java.math.BigInteger;
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
                    rsGeneral.getString("usercomment"), new FoodOrder(), new Restaurant(FacilityState.OCCUPIED), rsTypeSpecific.getString("birthdaychildname"),
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
                    rsGeneral.getString("usercomment"), new LectureRoom(FacilityState.OCCUPIED, LectureRoomType.biologicalType),
                    new Lecturer(), rsTypeSpecific.getString("choiceoftopic"), Integer.toString(rsTypeSpecific.getInt("noofteams")),
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

    public void deleteBooking(Booking book) throws SQLException {

        //Get CustomerID
        String getCustomerID = "SELECT customerid FROM booking WHERE bookingid=(?)";
        PreparedStatement pstmtCustomerID = connection.prepareStatement(getCustomerID);
        pstmtCustomerID.setInt(1, book.getId());
        ResultSet rsCustomerID = pstmtCustomerID.executeQuery();
        rsCustomerID.next();
        int customerID = rsCustomerID.getInt(1);

        String removeTypeBooking = null;
        String removeTypeCustomer = null;
        String removeCustomer = null;
        String removeBooking = null;

        //Check if booking is of type Arrangement Booking or Lecture Booking
        if (book instanceof ArrangementBooking) {
            //Create query strings
            removeTypeBooking = "DELETE FROM arrangement_booking WHERE bookingid=(?)";
            removeCustomer = "DELETE FROM customer WHERE customerid=(?)";
            removeBooking = "DELETE FROM booking WHERE bookingid=(?)";
            //Insert values
            PreparedStatement pstmtTypeBooking = connection.prepareStatement(removeTypeBooking);
            pstmtTypeBooking.setInt(1, book.getId());
            PreparedStatement pstmtCustomer = connection.prepareStatement(removeCustomer);
            pstmtCustomer.setInt(1, customerID);
            PreparedStatement pstmtBooking = connection.prepareStatement(removeBooking);
            pstmtBooking.setInt(1, book.getId());
            //Execute updates in correct order
            pstmtTypeBooking.executeUpdate();
            pstmtCustomer.executeUpdate();
            pstmtBooking.executeUpdate();
        } else if (book instanceof LectureBooking) {
            //Create query strings
            removeTypeBooking = "DELETE FROM lecture_booking WHERE bookingid=(?)";
            removeTypeCustomer = "DELETE FROM lecture_booking_customer WHERE customerid=(?)";
            removeCustomer = "DELETE FROM customer WHERE customerid=(?)";
            removeBooking = "DELETE FROM booking WHERE bookingid=(?)";
            //Insert values
            PreparedStatement pstmtTypeBooking = connection.prepareStatement(removeTypeBooking);
            pstmtTypeBooking.setInt(1, book.getId());
            PreparedStatement pstmtTypeCustomer = connection.prepareStatement(removeTypeCustomer);
            pstmtTypeCustomer.setInt(1, customerID);
            PreparedStatement pstmtCustomer = connection.prepareStatement(removeCustomer);
            pstmtCustomer.setInt(1, customerID);
            PreparedStatement pstmtBooking = connection.prepareStatement(removeBooking);
            pstmtBooking.setInt(1, book.getId());
            //Execute updates in correct order
            pstmtTypeBooking.executeUpdate();
            pstmtTypeCustomer.executeUpdate();
            pstmtCustomer.executeUpdate();
            pstmtBooking.executeUpdate();
        }
    }

    public void createLecBookManually(LectureBooking lbook) throws SQLException {
        //Insert data into booking table
        String general = "INSERT INTO booking (bookingtypeid, status, creationdate, date, time, participants, customercomment, usercomment)" +
                "VALUES ((1),(?),(?),(?),(?),(?),(?),(?))";

        PreparedStatement pstmtGeneral = connection.prepareStatement(general);
        pstmtGeneral.setString(1, lbook.getBookingStatus().toString());
        pstmtGeneral.setString(2, lbook.getCreationDate());
        pstmtGeneral.setString(3, lbook.getDate());
        pstmtGeneral.setString(4, lbook.getTime());
        pstmtGeneral.setInt(5, Integer.valueOf(lbook.getParticipants()));
        pstmtGeneral.setString(6, lbook.getCustomerComment());
        pstmtGeneral.setString(7, lbook.getComment());
        pstmtGeneral.executeUpdate();

        //Get Auto-Generated ID of this booking and customer
        String getLastID = "SELECT bookingid,customerid FROM booking ORDER BY bookingid DESC LIMIT 1";

        Statement stmt = connection.prepareStatement(getLastID);
        ResultSet rs = ((PreparedStatement) stmt).executeQuery();
        rs.next();
        int currentBookingID = rs.getInt(1);
        int currentCustomerID = rs.getInt(2);

        //Insert data into arrangement_booking table
        String typeSpecific = "INSERT INTO lecture_booking (bookingid,bookingtypeid,lectureroom,lecturer,choiceoftopic,noofteams,noofteachers,grade)" +
                "VALUES ((?),(1),(?),(?),(?),(?),(?),(?))";

        PreparedStatement pstmtTypeSpecific = connection.prepareStatement(typeSpecific);
        pstmtTypeSpecific.setInt(1, currentBookingID);
        pstmtTypeSpecific.setInt(2,2); //Integer.valueOf(lbook.getLectureRoom().toString())
        pstmtTypeSpecific.setInt(3, 3); //Integer.valueOf(lbook.getLecturer().toString())
        pstmtTypeSpecific.setInt(4, 4); //Integer.valueOf(lbook.getChoiceOfTopic())
        pstmtTypeSpecific.setInt(5, Integer.valueOf(lbook.getNoOfTeams()));
        pstmtTypeSpecific.setInt(6, Integer.valueOf(lbook.getNoOfTeachers()));
        pstmtTypeSpecific.setString(7, lbook.getGrade());
        pstmtTypeSpecific.executeUpdate();

        //Insert data into customer table
        String customer = "INSERT INTO customer (customerid,contactperson,phonenumber,email)" +
                "VALUES ((?),(?),(?),(?))";

        PreparedStatement pstmtCustomer = connection.prepareStatement(customer);
        pstmtCustomer.setInt(1, currentCustomerID);
        pstmtCustomer.setString(2, lbook.getCustomer().getContactPerson());
        pstmtCustomer.setString(3, lbook.getCustomer().getPhoneNumber());
        pstmtCustomer.setString(4, lbook.getCustomer().getEmail());
        pstmtCustomer.executeUpdate();

        //Insert data into lecture_booking_customer table
        LectureBookingCustomer temp = (LectureBookingCustomer) lbook.getCustomer();
        String lecture_customer = "INSERT INTO lecture_booking_customer (customerid,schoolname,zipcode,city,commune,schoolphonenumber,eannumber)" +
                "VALUES ((?),(?),(?),(?),(?),(?),(?))";
        
        PreparedStatement pstmtCustomerSpecific = connection.prepareStatement(lecture_customer);
        pstmtCustomerSpecific.setInt(1, currentCustomerID);
        pstmtCustomerSpecific.setString(2, temp.getSchoolName());
        pstmtCustomerSpecific.setInt(3, Integer.valueOf(temp.getZipCode()));
        pstmtCustomerSpecific.setString(4, temp.getCity());
        pstmtCustomerSpecific.setBoolean(5, Boolean.parseBoolean(temp.getCommune()));
        pstmtCustomerSpecific.setString(6, temp.getSchoolPhoneNumber());
        pstmtCustomerSpecific.setLong(7,Long.valueOf(temp.getEanNumber()));
        pstmtCustomerSpecific.executeUpdate();
    }
}
