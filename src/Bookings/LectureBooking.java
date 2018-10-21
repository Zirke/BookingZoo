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
    private String noOfTeams;
    private String noOfTeachers;
    private String grade;


    public LectureBooking(int id, BookingType bookingType, BookingStatus bookingStatus,
                          String creationDate, String date, String time, String participants, String customerComment,
                          String comment, LectureRoom lectureRoom, Lecturer lecturer, String choiceOfTopic,
                          String noOfTeams, String noOfTeachers, String grade, String customerContactPerson,
                          String customerPhoneNumber, String customerEmail, String schoolName, String zipCode, String city,
                          String commune, String schoolPhoneNumber, String eanNumber) {
        super(id, bookingType, bookingStatus, new LectureBookingCustomer(customerContactPerson, customerPhoneNumber,
                        customerEmail, schoolName, zipCode, city, commune, schoolPhoneNumber, eanNumber), creationDate, date,
                time, participants, customerComment, comment);
        this.lectureRoom = lectureRoom;
        this.lecturer = lecturer;
        this.choiceOfTopic = choiceOfTopic;

        this.noOfTeams = noOfTeams;
        this.noOfTeachers = noOfTeachers;
        this.grade = grade;
    }

    public LectureBooking(BookingType bookingType, BookingStatus bookingStatus,
                          String creationDate, String date, String time, String participants, String customerComment,
                          String comment, LectureRoom lectureRoom, Lecturer lecturer, String choiceOfTopic,
                          String noOfTeams, String noOfTeachers, String grade, String customerContactPerson,
                          String customerPhoneNumber, String customerEmail, String schoolName, String zipCode, String city,
                          String commune, String schoolPhoneNumber, String eanNumber) {
        super(bookingType, bookingStatus, new LectureBookingCustomer(customerContactPerson, customerPhoneNumber,
                        customerEmail, schoolName, zipCode, city, commune, schoolPhoneNumber, eanNumber), creationDate, date,
                time, participants, customerComment, comment);
        this.lectureRoom = lectureRoom;
        this.lecturer = lecturer;
        this.choiceOfTopic = choiceOfTopic;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LectureBooking)) return false;
        LectureBooking that = (LectureBooking) o;
        return Objects.equals(lectureRoom, that.lectureRoom) &&
                Objects.equals(lecturer, that.lecturer) &&
                Objects.equals(choiceOfTopic, that.choiceOfTopic) &&
                Objects.equals(noOfTeams, that.noOfTeams) &&
                Objects.equals(noOfTeachers, that.noOfTeachers) &&
                Objects.equals(grade, that.grade);
    }

    @Override
    public int hashCode() {

        return Objects.hash(lectureRoom, lecturer, choiceOfTopic, noOfTeams, noOfTeachers, grade);
    }

    @Override
    public String toString() {
        return getBookingType() + "\t \t \t" + getCustomer().getContactPerson() + "\t \t \t" + getDate();
    }
}
