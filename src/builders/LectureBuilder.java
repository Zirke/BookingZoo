package builders;

import bookings.LectureBooking;
import bookings.Lecturer;
import customers.LectureBookingCustomer;
import enums.BookingStatus;
import enums.BookingType;
import enums.ChoiceOfTopic;
import enums.Grade;
import facilities.LectureRoom;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LectureBuilder {
    private enums.BookingType bookingType;
    private enums.BookingStatus bookingStatus;
    private LectureBookingCustomer customer;
    private LocalDate creationDate;
    private LocalDateTime date;
    private int participants;
    private String customerComment;
    private String comment;
    private int id;

    private LectureRoom lectureRoom;
    private Lecturer lecturer;
    private ChoiceOfTopic choiceOfTopic;
    private int noOfTeams;
    private int noOfTeachers;
    private Grade grade;

    LectureBooking n = new LectureBooking();

    public LectureBuilder setBookingType(BookingType bookingType){
        this.bookingType = bookingType;
        n.setBookingType(bookingType);
        return this;
    }

    public LectureBuilder setBookingStatus(BookingStatus bookingStatus){
        this.bookingStatus = bookingStatus;
        n.setBookingStatus(bookingStatus);
        return this;
    }

    public LectureBuilder setCustomer(String contactPerson, String phoneNumber, String email, String schoolName, int zipCode,
                                      String city, String commune, String schoolPhoneNumber, long eanNumber){
        this.customer = new LectureBookingCustomer(contactPerson, phoneNumber, email, schoolName, zipCode, city, commune,
                schoolPhoneNumber, eanNumber);
        n.setCustomer(new LectureBookingCustomer(contactPerson, phoneNumber, email, schoolName, zipCode, city, commune,
                schoolPhoneNumber, eanNumber));
        return this;
    }

    public LectureBuilder setCreationDate(LocalDate creationDate){
        this.creationDate = creationDate;
        n.setCreationDate(creationDate);
        return this;
    }

    public LectureBuilder setDate(LocalDateTime date){
        this.date = date;
        n.setDateTime(date);
        return this;
    }

    public LectureBuilder setParticipants(int participants){
        this.participants = participants;
        n.setParticipants(participants);
        return this;
    }

    public LectureBuilder setCustomerComment(String customerComment){
        this.customerComment = customerComment;
        n.setCustomerComment(customerComment);
        return this;
    }

    public LectureBuilder setComment(String comment){
        this.comment = comment;
        n.setComment(comment);
        return this;
    }

    public LectureBuilder setLectureRoom(LectureRoom lectureRoom){
        this.lectureRoom = lectureRoom;
        n.setLectureRoom(lectureRoom);
        return this;
    }

    public LectureBuilder setLecturer(Lecturer lecturer){
        this.lecturer = lecturer;
        n.setLecturer(lecturer);
        return this;
    }

    public LectureBuilder setChoiceOfTopic(ChoiceOfTopic choiceOfTopic){
        this.choiceOfTopic = choiceOfTopic;
        n.setChoiceOfTopic(choiceOfTopic);
        return this;
    }

    public LectureBuilder setNoOfTeams(int noOfTeams){
        this.noOfTeams = noOfTeams;
        n.setNoOfTeams(noOfTeams);
        return this;
    }

    public LectureBuilder setNoOfTeachers(int noOfTeachers){
        this.noOfTeachers = noOfTeachers;
        n.setNoOfTeachers(noOfTeachers);
        return this;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
        n.setGrade(grade);
    }

    public void setId(int id) {
        this.id = id;
        n.setId(id);
    }

    public LectureBooking build(){
        //return new LectureBooking(id, bookingType, bookingStatus, creationDate, date, participants,
          //      customerComment, comment, lectureRoom, lecturer, choiceOfTopic, noOfTeams, noOfTeachers, grade, customer);
        return n;
    }
}
