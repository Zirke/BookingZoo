package Bookings;

import Customers.LectureBookingCustomer;
import enums.BookingStatus;
import enums.BookingType;
import enums.ChoiceOfTopic;
import facilities.LectureRoom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class LectureBooking extends Booking {
    private LectureRoom lectureRoom;
    private Lecturer lecturer;
    private ChoiceOfTopic choiceOfTopic;
    private int noOfTeams;
    private int noOfTeachers;
    private int grade;


    public LectureBooking(int id, BookingType bookingType, BookingStatus bookingStatus,
                          LocalDate creationDate, LocalDateTime date, int participants, String customerComment,
                          String comment, LectureRoom lectureRoom, Lecturer lecturer, ChoiceOfTopic choiceOfTopic,
                          int noOfTeams, int noOfTeachers, int grade, String customerContactPerson,
                          String customerPhoneNumber, String customerEmail, String schoolName, int zipCode, String city,
                          String commune, String schoolPhoneNumber, long eanNumber) {
        super(id, bookingType, bookingStatus, new LectureBookingCustomer(customerContactPerson, customerPhoneNumber,
                        customerEmail, schoolName, zipCode, city, commune, schoolPhoneNumber, eanNumber), creationDate, date,
                participants, customerComment, comment);
        this.lectureRoom = lectureRoom;
        this.lecturer = lecturer;
        this.choiceOfTopic = choiceOfTopic;

        this.noOfTeams = noOfTeams;
        this.noOfTeachers = noOfTeachers;
        this.grade = grade;
    }

    public LectureBooking(BookingType bookingType, BookingStatus bookingStatus,
                          LocalDate creationDate, LocalDateTime date, int participants, String customerComment,
                          String comment, LectureRoom lectureRoom, Lecturer lecturer, ChoiceOfTopic choiceOfTopic,
                          int noOfTeams, int noOfTeachers, int grade, String customerContactPerson,
                          String customerPhoneNumber, String customerEmail, String schoolName, int zipCode, String city,
                          String commune, String schoolPhoneNumber, long eanNumber) {
        super(bookingType, bookingStatus, new LectureBookingCustomer(customerContactPerson, customerPhoneNumber,
                        customerEmail, schoolName, zipCode, city, commune, schoolPhoneNumber, eanNumber), creationDate, date,
                participants, customerComment, comment);
        this.lectureRoom = lectureRoom;
        this.lecturer = lecturer;
        this.choiceOfTopic = choiceOfTopic;
        this.noOfTeams = noOfTeams;
        this.noOfTeachers = noOfTeachers;
        this.grade = grade;
    }

    public LectureBooking(BookingType bookingType, BookingStatus bookingStatus, LocalDate creationDate, LocalDateTime date, int participants,
                          String customerComment, String comment, LectureRoom lectureRoom, Lecturer lecturer,
                          ChoiceOfTopic choiceOfTopic, int noOfTeams, int noOfTeachers, int grade, LectureBookingCustomer customer) {
        super(bookingType, bookingStatus, customer, creationDate, date,
                participants, customerComment, comment);
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

    public ChoiceOfTopic getChoiceOfTopic() {
        return choiceOfTopic;
    }

    public void setChoiceOfTopic(ChoiceOfTopic choiceOfTopic) {
        this.choiceOfTopic = choiceOfTopic;
    }

    public int getNoOfTeams() {
        return noOfTeams;
    }

    public void setNoOfTeams(int noOfTeams) {
        this.noOfTeams = noOfTeams;
    }

    public int getNoOfTeachers() {
        return noOfTeachers;
    }

    public void setNoOfTeachers(int noOfTeachers) {
        this.noOfTeachers = noOfTeachers;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
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
        return "LectureBooking" + getBookingStatus() + getId();
    }
}
