package builders;

import Bookings.LectureBooking;
import Bookings.Lecturer;
import Customers.LectureBookingCustomer;
import enums.BookingStatus;
import enums.BookingType;
import enums.ChoiceOfTopic;
import facilities.LectureRoom;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LectureBuilder {
    private enums.BookingType bookingType;
    private enums.BookingStatus bookingStatus;
    private LectureBookingCustomer customer;
    private LocalDate creationDate;
    private LocalDateTime date;
    private String time;
    private int participants;
    private String customerComment;
    private String comment;

    private LectureRoom lectureRoom;
    private Lecturer lecturer;
    private ChoiceOfTopic choiceOfTopic;
    private int noOfTeams;
    private int noOfTeachers;
    private String grade;

    public LectureBuilder setBookingType(BookingType bookingType){
        this.bookingType = bookingType;
        return this;
    }

    public LectureBuilder setBookingStatus(BookingStatus bookingStatus){
        this.bookingStatus = bookingStatus;
        return this;
    }

    public LectureBuilder setCustomer(String contactPerson, String phoneNumber, String email, String schoolName, int zipCode,
                                      String city, String commune, String schoolPhoneNumber, long eanNumber){
        this.customer = new LectureBookingCustomer(contactPerson, phoneNumber, email, schoolName, zipCode, city, commune,
                schoolPhoneNumber, eanNumber);
        return this;
    }

    public LectureBuilder setCreationDate(LocalDate creationDate){
        this.creationDate = creationDate;
        return this;
    }

    public LectureBuilder setDate(LocalDateTime date){
        this.date = date;
        return this;
    }

    public LectureBuilder setTime(String time){
        this.time = time;
        return this;
    }

    public LectureBuilder setParticipants(int participants){
        this.participants = participants;
        return this;
    }

    public LectureBuilder setCustomerComment(String customerComment){
        this.customerComment = customerComment;
        return this;
    }

    public LectureBuilder setComment(String comment){
        this.comment = comment;
        return this;
    }

    public LectureBuilder setLectureRoom(LectureRoom lectureRoom){
        this.lectureRoom = lectureRoom;
        return this;
    }

    public LectureBuilder setLecturer(Lecturer lecturer){
        this.lecturer = lecturer;
        return this;
    }

    public LectureBuilder setChoiceOfTopic(ChoiceOfTopic choiceOfTopic){
        this.choiceOfTopic = choiceOfTopic;
        return this;
    }

    public LectureBuilder setNoOfTeams(int noOfTeams){
        this.noOfTeams = noOfTeams;
        return this;
    }

    public LectureBuilder setNoOfTeachers(int noOfTeachers){
        this.noOfTeachers = noOfTeachers;
        return this;
    }

    public LectureBuilder setGrade(String grade){
        this.grade = grade;
        return this;
    }

    public LectureBooking build(){
        return new LectureBooking(bookingType, bookingStatus, creationDate, date, participants,
                customerComment, comment, lectureRoom, lecturer, choiceOfTopic, noOfTeams, noOfTeachers, grade, customer);
    }
}
