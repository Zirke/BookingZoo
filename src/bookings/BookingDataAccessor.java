package bookings;

import customers.LectureBookingCustomer;
import enums.*;
import exception.NoBookingsInDatabaseException;
import exception.NoNewBookingsInDatabaseException;
import facilities.LectureRoom;
import facilities.Restaurant;
import javafx.scene.control.Alert;
import userInterface.GeneralController;
import userInterface.MainScreenController;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;

import static postToCalendars.PostToGoogle.*;

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

    public static BookingDataAccessor connect() throws SQLException, ClassNotFoundException {
        BookingDataAccessor bda = null;
        try{
            bda = new BookingDataAccessor(
                "org.postgresql.Driver",
                "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
                "jyjczxth",
                "nw51BNKhctporjIFT5Qhhm72jwGVJK95"
        );
        } catch (SQLException e){
            MainScreenController.cantConnect();
        }
        return bda;
    }

    public ArrayList<Booking> fetchArrBooks() throws SQLException, NoBookingsInDatabaseException {

        ArrayList<Booking> arr = null;

        String general = "SELECT * FROM booking WHERE bookingtypeid = 2";

        Statement stmtSpecific = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        ResultSet rsGeneral = stmtSpecific.executeQuery(general);

        if (rsGeneral.next()) {
            rsGeneral.previous();
            arr = fetchFromDatabase(rsGeneral);
        }
        //connection.close();
        if (arr != null) {
            return arr;
        }
        throw new NoBookingsInDatabaseException();
    }

    public ArrayList<Booking> fetchLecBooks() throws SQLException, NoBookingsInDatabaseException {

        ArrayList<Booking> arr = null;

        String general = "SELECT * FROM booking WHERE bookingtypeid = 1";

        Statement stmtGeneral = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        ResultSet rsGeneral = stmtGeneral.executeQuery(general);
        if (rsGeneral.next()) {
            rsGeneral.previous();
            arr = fetchFromDatabase(rsGeneral);
        }
        //connection.close();
        if (arr != null) {
            return arr;
        }
        throw new NoBookingsInDatabaseException();
    }

    public void createArrBookManually(ArrangementBooking abook) throws SQLException {
        //Insert data into booking table
        String general = "INSERT INTO booking (bookingtypeid, status, creationdate, datetime, participants, customercomment, usercomment, facilitystate)" +
                "VALUES ((2),(?),(?),(?),(?),(?),(?),(?))";

        PreparedStatement pstmtGeneral = connection.prepareStatement(general);
        pstmtGeneral.setString(1, abook.getBookingStatus().name());
        pstmtGeneral.setDate(2, java.sql.Date.valueOf(abook.getCreationDate()));
        pstmtGeneral.setTimestamp(3, java.sql.Timestamp.valueOf(abook.getDateTime()));
        pstmtGeneral.setInt(4, abook.getParticipants());
        pstmtGeneral.setString(5, abook.getCustomerComment());
        pstmtGeneral.setString(6, abook.getComment());
        pstmtGeneral.setString(7, abook.getRestaurant().getState().name());
        pstmtGeneral.executeUpdate();

        //Get Auto-Generated ID of this booking and customer
        String getLastID = "SELECT bookingid,customerid FROM booking ORDER BY bookingid DESC LIMIT 1";

        Statement stmt = connection.prepareStatement(getLastID);
        ResultSet rs = ((PreparedStatement) stmt).executeQuery();
        rs.next();
        int currentBookingID = rs.getInt(1);
        int currentCustomerID = rs.getInt(2);

        //Insert data into arrangement_booking table
        String typeSpecific = "INSERT INTO arrangement_booking (bookingid,bookingtypeid,food,restauranttype,birthdaychildname,birthdaychildage,formerparticipant,guide)" +
                "VALUES ((?),(2),(?),(?),(?),(?),(?),(?))";

        PreparedStatement pstmtTypeSpecific = connection.prepareStatement(typeSpecific);
        pstmtTypeSpecific.setInt(1, currentBookingID);
        pstmtTypeSpecific.setString(2, abook.getMenuChosen().getChoiceOfMenu().name());
        pstmtTypeSpecific.setString(3, abook.getRestaurant().getType().name());
        pstmtTypeSpecific.setString(4, abook.getBirthdayChildName());
        pstmtTypeSpecific.setInt(5, abook.getBirthdayChildAge());
        pstmtTypeSpecific.setString(6, abook.getFormerParticipant());
        pstmtTypeSpecific.setString(7, abook.getGuide());


        //Insert data into customer table
        String customer = "INSERT INTO customer (customerid,contactperson,phonenumber,email)" +
                "VALUES ((?),(?),(?),(?))";

        PreparedStatement pstmtCustomer = connection.prepareStatement(customer);
        pstmtCustomer.setInt(1, currentCustomerID);
        pstmtCustomer.setString(2, abook.getCustomer().getContactPerson());
        pstmtCustomer.setString(3, abook.getCustomer().getPhoneNumber());
        pstmtCustomer.setString(4, abook.getCustomer().getEmail());

        //Execute Updates
        pstmtTypeSpecific.executeUpdate();
        pstmtCustomer.executeUpdate();

        try {
            postToCalendar(abook);
        } catch (IOException e) {
            GeneralController.showAlertBox(Alert.AlertType.WARNING, "Fejl med Google Calendar",
                    "Kontakt IT for at løse problemet.\n" + e.getMessage());
        }

        //connection.close();
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


            try {
                deleteBookingInCalendar(book);
            } catch (IOException e) {
                GeneralController.showAlertBox(Alert.AlertType.WARNING, "Fejl med Google Calendar",
                        "Kontakt IT for at løse problemet.\n " + e.getMessage());
            }
        }
            //connection.close();
    }

    public void createLecBookManually(LectureBooking lbook) throws SQLException {
        //Insert data into booking table
        String general = "INSERT INTO booking (bookingtypeid, status, creationdate, datetime, participants, customercomment, usercomment,facilitystate)" +
                "VALUES ((1),(?),(?),(?),(?),(?),(?),(?))";

        PreparedStatement pstmtGeneral = connection.prepareStatement(general);
        pstmtGeneral.setString(1, lbook.getBookingStatus().name());
        pstmtGeneral.setDate(2, java.sql.Date.valueOf(lbook.getCreationDate()));
        pstmtGeneral.setTimestamp(3, java.sql.Timestamp.valueOf(lbook.getDateTime()));
        pstmtGeneral.setInt(4, lbook.getParticipants());
        pstmtGeneral.setString(5, lbook.getCustomerComment());
        pstmtGeneral.setString(6, lbook.getComment());
        pstmtGeneral.setString(7, lbook.getLectureRoom().getState().name());
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
        pstmtTypeSpecific.setString(2,lbook.getLectureRoom().getType().name());
        pstmtTypeSpecific.setString(3, lbook.getLecturer().toString());
        pstmtTypeSpecific.setString(4, lbook.getChoiceOfTopic().name());
        pstmtTypeSpecific.setInt(5, lbook.getNoOfTeams());
        pstmtTypeSpecific.setInt(6, lbook.getNoOfTeachers());
        pstmtTypeSpecific.setString(7, lbook.getGrade().name());


        //Insert data into customer table
        String customer = "INSERT INTO customer (customerid,contactperson,phonenumber,email)" +
                "VALUES ((?),(?),(?),(?))";

        PreparedStatement pstmtCustomer = connection.prepareStatement(customer);
        pstmtCustomer.setInt(1, currentCustomerID);
        pstmtCustomer.setString(2, lbook.getCustomer().getContactPerson());
        pstmtCustomer.setString(3, lbook.getCustomer().getPhoneNumber());
        pstmtCustomer.setString(4, lbook.getCustomer().getEmail());


        //Insert data into lecture_booking_customer table
        LectureBookingCustomer temp = (LectureBookingCustomer) lbook.getCustomer();
        String lecture_customer = "INSERT INTO lecture_booking_customer (customerid,schoolname,zipcode,city,commune,schoolphonenumber,eannumber)" +
                "VALUES ((?),(?),(?),(?),(?),(?),(?))";
        
        PreparedStatement pstmtCustomerSpecific = connection.prepareStatement(lecture_customer);
        pstmtCustomerSpecific.setInt(1, currentCustomerID);
        pstmtCustomerSpecific.setString(2, temp.getSchoolName());
        pstmtCustomerSpecific.setInt(3, temp.getZipCode());
        pstmtCustomerSpecific.setString(4, temp.getCity());
        pstmtCustomerSpecific.setString(5, temp.getCommune());
        pstmtCustomerSpecific.setString(6, temp.getSchoolPhoneNumber());
        pstmtCustomerSpecific.setLong(7, temp.getEanNumber());


        //Execute updates
        pstmtTypeSpecific.executeUpdate();
        pstmtCustomer.executeUpdate();
        pstmtCustomerSpecific.executeUpdate();

        try {
            postToCalendar(lbook);
        } catch (IOException e) {
            GeneralController.showAlertBox(Alert.AlertType.WARNING, "Fejl med Google Calendar",
                    "Kontakt IT for at løse problemet.\n " + e.getMessage());
        }
        //connection.close();
    }

    public void changeBookingStatus(Booking book, BookingStatus status) throws SQLException {

        String changeStatus = "UPDATE booking SET status = (?) WHERE bookingid=(?)";
        PreparedStatement pstmt = connection.prepareStatement(changeStatus);
        pstmt.setString(1, status.name());
        pstmt.setInt(2, book.getId());

        pstmt.executeUpdate();
    }

    public void editArrBook(ArrangementBooking abook) throws SQLException {
        String getCustomerID = "SELECT customerid FROM booking WHERE bookingid=(?)";
        PreparedStatement pstmtGetCustomerID = connection.prepareStatement(getCustomerID);
        pstmtGetCustomerID.setInt(1,abook.getId());
        ResultSet rsCustomerID = pstmtGetCustomerID.executeQuery();
        rsCustomerID.next();
        int currentCustomerID = rsCustomerID.getInt(1);

        String editBooking = "UPDATE booking SET datetime=(?), participants=(?), customercomment=(?), usercomment=(?), facilitystate=(?)" +
                "WHERE bookingid=(?)";
        String editLectureBooking = "UPDATE arrangement_booking SET food=(?), restauranttype=(?), birthdaychildname=(?), birthdaychildage=(?), formerparticipant=(?), guide=(?)" +
                "WHERE bookingid=(?)";
        String editCustomer = "UPDATE customer SET contactperson=(?), phonenumber=(?), email=(?)" +
                "WHERE customerid=(?)";

        PreparedStatement pstmtGeneral = connection.prepareStatement(editBooking);
        pstmtGeneral.setTimestamp(1, Timestamp.valueOf(abook.getDateTime()));
        pstmtGeneral.setInt(2,abook.getParticipants()); pstmtGeneral.setString(3,abook.getCustomerComment());
        pstmtGeneral.setString(4,abook.getComment()); pstmtGeneral.setString(5, abook.getRestaurant().getState().name());
        pstmtGeneral.setInt(6,abook.getId());


        PreparedStatement pstmtTypeSpecific = connection.prepareStatement(editLectureBooking);
        pstmtTypeSpecific.setString(1,abook.getMenuChosen().getChoiceOfMenu().name());
        pstmtTypeSpecific.setString(2,abook.getRestaurant().getType().name());
        pstmtTypeSpecific.setString(3,abook.getBirthdayChildName()); pstmtTypeSpecific.setInt(4,abook.getBirthdayChildAge());
        pstmtTypeSpecific.setString(5,abook.getFormerParticipant()); pstmtTypeSpecific.setString(6,abook.getGuide());
        pstmtTypeSpecific.setInt(7,abook.getId());

        PreparedStatement pstmtCustomer = connection.prepareStatement(editCustomer);
        pstmtCustomer.setString(1,abook.getCustomer().getContactPerson()); pstmtCustomer.setString(2,abook.getCustomer().getPhoneNumber());
        pstmtCustomer.setString(3,abook.getCustomer().getEmail()); pstmtCustomer.setInt(4,currentCustomerID);

        //Execute Updates
        pstmtGeneral.executeUpdate();
        changeBookingStatus(abook, abook.getBookingStatus());
        pstmtTypeSpecific.executeUpdate();
        pstmtCustomer.executeUpdate();

        if (!abook.getBookingStatus().equals(BookingStatus.STATUS_ARCHIVED)) {
            try {
                updateCalendar(abook);
            } catch (IOException e) {
                GeneralController.showAlertBox(Alert.AlertType.WARNING, "Fejl med Google Calendar",
                        "Kontakt IT for at løse problemet.\n " + e.getMessage());
            }
        }
        //connection.close();
    }

    public void editLecBook(LectureBooking lbook) throws SQLException {

        String getCustomerID = "SELECT customerid FROM booking WHERE bookingid=(?)";
        PreparedStatement pstmtGetCustomerID = connection.prepareStatement(getCustomerID);
        pstmtGetCustomerID.setInt(1,lbook.getId());
        ResultSet rsCustomerID = pstmtGetCustomerID.executeQuery();
        rsCustomerID.next();
        int currentCustomerID = rsCustomerID.getInt(1);

        String editBooking = "UPDATE booking SET datetime=(?), participants=(?), customercomment=(?), usercomment=(?), facilitystate=(?)" +
                "WHERE bookingid=(?)";
        String editLectureBooking = "UPDATE lecture_booking SET lectureroom=(?), lecturer=(?), choiceoftopic=(?), noofteams=(?), noofteachers=(?), grade=(?)" +
                "WHERE bookingid=(?)";
        String editCustomer = "UPDATE customer SET contactperson=(?), phonenumber=(?), email=(?)" +
                "WHERE customerid=(?)";
        String editLectureCustomer = "UPDATE lecture_booking_customer SET schoolname=(?), zipcode=(?), city=(?), commune=(?), schoolphonenumber=(?), eannumber=(?)" +
                "WHERE customerid=(?)";

        PreparedStatement pstmtGeneral = connection.prepareStatement(editBooking);
        pstmtGeneral.setTimestamp(1, Timestamp.valueOf(lbook.getDateTime()));
        pstmtGeneral.setInt(2,lbook.getParticipants()); pstmtGeneral.setString(3,lbook.getCustomerComment());
        pstmtGeneral.setString(4,lbook.getComment()); pstmtGeneral.setString(5, lbook.getLectureRoom().getState().name());
        pstmtGeneral.setInt(6,lbook.getId());


        PreparedStatement pstmtTypeSpecific = connection.prepareStatement(editLectureBooking);
        pstmtTypeSpecific.setString(1,lbook.getLectureRoom().getType().name()); pstmtTypeSpecific.setString(2,lbook.getLecturer().toString());
        pstmtTypeSpecific.setString(3,lbook.getChoiceOfTopic().name()); pstmtTypeSpecific.setInt(4,lbook.getNoOfTeams());
        pstmtTypeSpecific.setInt(5, lbook.getNoOfTeachers());
        pstmtTypeSpecific.setString(6, lbook.getGrade().name());
        pstmtTypeSpecific.setInt(7,lbook.getId());

        PreparedStatement pstmtCustomer = connection.prepareStatement(editCustomer);
        pstmtCustomer.setString(1,lbook.getCustomer().getContactPerson()); pstmtCustomer.setString(2,lbook.getCustomer().getPhoneNumber());
        pstmtCustomer.setString(3,lbook.getCustomer().getEmail()); pstmtCustomer.setInt(4,currentCustomerID);

        LectureBookingCustomer temp = (LectureBookingCustomer) lbook.getCustomer();
        PreparedStatement pstmtCustomerSpecific = connection.prepareStatement(editLectureCustomer);
        pstmtCustomerSpecific.setString(1,temp.getSchoolName()); pstmtCustomerSpecific.setInt(2,temp.getZipCode());
        pstmtCustomerSpecific.setString(3,temp.getCity()); pstmtCustomerSpecific.setString(4,temp.getCommune());
        pstmtCustomerSpecific.setString(5,temp.getSchoolPhoneNumber()); pstmtCustomerSpecific.setLong(6,temp.getEanNumber());
        pstmtCustomerSpecific.setInt(7,currentCustomerID);

        //Execute Updates
        pstmtGeneral.executeUpdate();
        changeBookingStatus(lbook, lbook.getBookingStatus());
        pstmtTypeSpecific.executeUpdate();
        pstmtCustomer.executeUpdate();
        pstmtCustomerSpecific.executeUpdate();

        if (!lbook.getBookingStatus().equals(BookingStatus.STATUS_ARCHIVED)) {
            try {
                updateCalendar(lbook);
            } catch (IOException e) {
                GeneralController.showAlertBox(Alert.AlertType.WARNING, "Fejl med Google Calendar",
                        "Kontakt IT for at løse problemet.\n " + e.getMessage());
            }
        }

        //connection.close();

    }
    public int getLastID()throws SQLException{
        String getLastID = "SELECT bookingid,customerid FROM booking ORDER BY bookingid DESC LIMIT 1";

        Statement stmt = connection.prepareStatement(getLastID);
        ResultSet rs = ((PreparedStatement) stmt).executeQuery();
        rs.next();
        int currentBookingID = rs.getInt(1);

        return currentBookingID;
    }

    private ArrayList<Booking> fetchFromDatabase(ResultSet rsGeneral) throws SQLException {
        ArrayList<Booking> arr = new ArrayList<>();
        rsGeneral.next();
        if (rsGeneral.getInt("bookingtypeid") == 2) {
            rsGeneral.previous();


            String typeSpecific = "SELECT bookingid,food,restauranttype," +
                    "birthdaychildname,birthdaychildage,formerparticipant,guide FROM arrangement_booking WHERE bookingid = (?)";

            while (rsGeneral.next()) {
                ArrangementBooking abook;

                PreparedStatement pstmtTypeSpecific = connection.prepareStatement(typeSpecific);
                pstmtTypeSpecific.setInt(1, rsGeneral.getInt("bookingid"));
                ResultSet rsTypeSpecific = pstmtTypeSpecific.executeQuery();
                rsTypeSpecific.next();

                String customer = "SELECT customerid,contactperson,phonenumber,email FROM customer WHERE customerid = (?)";
                PreparedStatement pstmtCustomer = connection.prepareStatement(customer);
                pstmtCustomer.setInt(1, rsGeneral.getInt("customerid"));
                ResultSet rsCustomer = pstmtCustomer.executeQuery();
                rsCustomer.next();

                abook = new ArrangementBooking(
                        rsGeneral.getInt("bookingid"), BookingType.ARRANGEMENTBOOKING, BookingStatus.valueOf(rsGeneral.getString("status")),
                        rsGeneral.getDate("creationdate").toLocalDate(), rsGeneral.getTimestamp("datetime").toLocalDateTime(),
                        rsGeneral.getInt("participants"), rsGeneral.getString("customercomment"),
                        rsGeneral.getString("usercomment"), new FoodOrder(ChoiceOfMenu.valueOf(rsTypeSpecific.getString("food"))),
                        new Restaurant(FacilityState.valueOf(rsGeneral.getString("facilitystate")),RestaurantType.valueOf(rsTypeSpecific.getString("restauranttype"))),
                        rsTypeSpecific.getString("birthdaychildname"),
                        rsTypeSpecific.getInt("birthdaychildage"), rsTypeSpecific.getString("formerparticipant"),
                        rsTypeSpecific.getString("guide"), rsCustomer.getString("contactperson"),
                        rsCustomer.getString("phonenumber"), rsCustomer.getString("email")
                );

                arr.add(abook);
            }
            return arr;
        } else if (rsGeneral.getInt("bookingtypeid") == 1) {
            rsGeneral.previous();

            String typeSpecific = "SELECT bookingid,lectureroom,lecturer,choiceoftopic,noofteams,noofteachers,grade " +
                    "FROM lecture_booking WHERE bookingid = (?)";

            while (rsGeneral.next()) {
                LectureBooking lbook;

                PreparedStatement pstmtTypeSpecific = connection.prepareStatement(typeSpecific);
                pstmtTypeSpecific.setInt(1, rsGeneral.getInt("bookingid"));
                ResultSet rsTypeSpecific = pstmtTypeSpecific.executeQuery();
                rsTypeSpecific.next();

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
                        rsGeneral.getInt("bookingid"), BookingType.LECTUREBOOKING, BookingStatus.valueOf(rsGeneral.getString("status")),
                        rsGeneral.getDate("creationdate").toLocalDate(), rsGeneral.getTimestamp("datetime").toLocalDateTime(),
                        rsGeneral.getInt("participants"), rsGeneral.getString("customercomment"),
                        rsGeneral.getString("usercomment"), new LectureRoom(FacilityState.valueOf(rsGeneral.getString("facilitystate")), LectureRoomType.valueOf(rsTypeSpecific.getString("lectureroom"))),
                        new Lecturer(rsTypeSpecific.getString("lecturer")), ChoiceOfTopic.valueOf(rsTypeSpecific.getString("choiceoftopic")), rsTypeSpecific.getInt("noofteams"),
                        rsTypeSpecific.getInt("noofteachers"), Grade.valueOf(rsTypeSpecific.getString("grade")),
                        rsCustomer.getString("contactperson"), rsCustomer.getString("phonenumber"),
                        rsCustomer.getString("email"), rsCustomerSpecific.getString("schoolname"),
                        rsCustomerSpecific.getInt("zipcode"), rsCustomerSpecific.getString("city"),
                        rsCustomerSpecific.getString("commune"), rsCustomerSpecific.getString("schoolphonenumber"),
                        rsCustomerSpecific.getLong("eannumber")
                );

                if (lbook != null) {
                    arr.add(lbook);
                }
            }
            //connection.close();
            return arr;
        }
        throw new InputMismatchException();
    }


    public ArrayList<Booking> refreshBookings(ArrayList<Booking> currentBookingsList) throws SQLException, NoNewBookingsInDatabaseException {
        int lastID = 0;
        for (Booking book : currentBookingsList) {
            if (lastID < book.getId()){
                lastID = book.getId();
            }
        }
        ArrayList<Booking> arr = new ArrayList<>();

        String sql = "SELECT * FROM booking WHERE bookingid > (?)";
        PreparedStatement pstmtGeneral = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmtGeneral.setInt(1,lastID);

        ResultSet rsGeneral = pstmtGeneral.executeQuery();
        if (rsGeneral.next()) {
            rsGeneral.previous();
            arr = fetchFromDatabase(rsGeneral);
        }
        /*if (arr.isEmpty() && currentBookingsList != null) {
            System.out.println("No new bookings in db");
        }
        currentBookingsList.addAll(arr);*/
        /*if (!arr.isEmpty()) {*/
            return arr;
        /*} else throw new NoNewBookingsInDatabaseException();*/
    }
}
