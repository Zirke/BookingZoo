package Bookings;

import Customers.LectureBookingCustomer;
import enums.BookingStatus;
import enums.BookingType;
import facilities.LectureRoom;

import java.util.Objects;

public class LectureBooking extends Booking {
    private LectureRoom lectureRoom;
    private Lecturer lecturer;
    private String choiceOfTopic;
    private String noOfPupils;
    private String noOfTeams;
    private String noOfTeachers;
    private String grade;

    public LectureBooking(int id, BookingType bookingType, BookingStatus bookingStatus,
                          String creationDate, String date, String time, String participants, String customerComment,
                          String comment, LectureRoom lectureRoom, Lecturer lecturer, String choiceOfTopic,
                          String noOfPupils, String noOfTeams, String noOfTeachers, String grade, String customerContactPerson,
                          String customerPhoneNumber, String customerEmail, String schoolName, String zipCode, String city,
                          String commune, String schoolPhoneNumber, String eanNumber) {
        super(id, bookingType, bookingStatus, new LectureBookingCustomer(customerContactPerson, customerPhoneNumber,
                        customerEmail, schoolName, zipCode, city, commune, schoolPhoneNumber, eanNumber), creationDate, date,
                time, participants, customerComment, comment);
        this.lectureRoom = lectureRoom;
        this.lecturer = lecturer;
        this.choiceOfTopic = choiceOfTopic;
        this.noOfPupils = noOfPupils;
        this.noOfTeams = noOfTeams;
        this.noOfTeachers = noOfTeachers;
        this.grade = grade;
    }

    public LectureRoom getLectureRoom() {
        return lectureRoom;
    }

    public void setLectureRoom(LectureRoom lectureRoom) {
        this.lectureRoom = lectureRoom;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    public String getChoiceOfTopic() {
        return choiceOfTopic;
    }

    public void setChoiceOfTopic(String choiceOfTopic) {
        this.choiceOfTopic = choiceOfTopic;
    }

    public String getNoOfPupils() {
        return noOfPupils;
    }

    public void setNoOfPupils(String noOfPupils) {
        this.noOfPupils = noOfPupils;
    }

    public String getNoOfTeams() {
        return noOfTeams;
    }

    public void setNoOfTeams(String noOfTeams) {
        this.noOfTeams = noOfTeams;
    }

    public String getNoOfTeachers() {
        return noOfTeachers;
    }

    public void setNoOfTeachers(String noOfTeachers) {
        this.noOfTeachers = noOfTeachers;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    /*
    public static ArrayList<LectureBooking> fetchSchoolBookings(Connection con) throws SQLException {

        ArrayList<LectureBooking> sch = new ArrayList<>();
        String sql = "SELECT * FROM school_booking";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            LectureBooking sbook = new LectureBooking(rs.getInt(1), BookingType.Skoletjeneste,
                    rs.getString(2), rs.getString(3),
                    rs.getString(17), rs.getString(18),
                    rs.getString(19), rs.getString(20),
                    String.valueOf(rs.getInt(4)), String.valueOf(rs.getString(5)),
                    String.valueOf(rs.getInt(6)), String.valueOf(rs.getInt(8)),
                    rs.getString(7), rs.getString(11), String.valueOf(rs.getString(12)),
                    rs.getString("city"), String.valueOf(rs.getString("aalborg_county")),
                    rs.getString("school_phone"), String.valueOf(rs.getString("ean_number"))
            );

            sch.add(sbook);

        }
        return sch;
    }
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LectureBooking)) return false;
        LectureBooking that = (LectureBooking) o;
        return Objects.equals(lectureRoom, that.lectureRoom) &&
                Objects.equals(lecturer, that.lecturer) &&
                Objects.equals(choiceOfTopic, that.choiceOfTopic) &&
                Objects.equals(noOfPupils, that.noOfPupils) &&
                Objects.equals(noOfTeams, that.noOfTeams) &&
                Objects.equals(noOfTeachers, that.noOfTeachers) &&
                Objects.equals(grade, that.grade);
    }

    @Override
    public int hashCode() {

        return Objects.hash(lectureRoom, lecturer, choiceOfTopic, noOfPupils, noOfTeams, noOfTeachers, grade);
    }

    @Override
    public String toString() {
        return getBookingType() + "\t \t \t" + getCustomer().getContactPerson() + "\t \t \t" + getDate();
    }
}
