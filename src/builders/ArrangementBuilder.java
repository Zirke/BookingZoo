package builders;

import bookings.ArrangementBooking;
import bookings.FoodOrder;
import customers.Customer;
import enums.BookingStatus;
import enums.BookingType;
import facilities.Restaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ArrangementBuilder {
    private enums.BookingType bookingType;
    private enums.BookingStatus bookingStatus;
    private Customer customer;
    private LocalDate creationDate;
    private LocalDateTime date;
    private int participants;
    private String customerComment;
    private String comment;

    private FoodOrder menuChosen;
    private Restaurant restaurant;
    private String birthdayChildName;
    private int birthdayChildAge;
    private String formerParticipant;
    private String guide;
    private int id;

    ArrangementBooking n = new ArrangementBooking();


    public ArrangementBuilder setBookingType(BookingType bookingType){
        this.bookingType = bookingType;
        n.setBookingType(this.bookingType);
        return this;
    }

    public ArrangementBuilder setBookingStatus(BookingStatus bookingStatus){
        this.bookingStatus = bookingStatus;
        n.setBookingStatus(bookingStatus);
        return this;
    }

    public ArrangementBuilder setCustomer(String contactPerson, String phoneNumber, String email){
        this.customer = new Customer(contactPerson, phoneNumber, email);
        n.setCustomer(new Customer(contactPerson, phoneNumber, email));
        return this;
    }

    public ArrangementBuilder setCreationDate(LocalDate creationDate){
        this.creationDate = creationDate;
        n.setCreationDate(creationDate);
        return this;
    }

    public ArrangementBuilder setDate(LocalDateTime date){
        this.date = date;
        n.setDateTime(date);
        return this;
    }

    public ArrangementBuilder setParticipants(int participants){
        this.participants = participants;
        n.setParticipants(participants);
        return this;
    }

    public ArrangementBuilder setCustomerComment(String customerComment){
        this.customerComment = customerComment;
        n.setCustomerComment(customerComment);
        return this;
    }

    public ArrangementBuilder setComment(String comment){
        this.comment = comment;
        n.setComment(comment);
        return this;
    }

    public ArrangementBuilder setMenuChosen(FoodOrder menuChosen){
        this.menuChosen = menuChosen;
        n.setMenuChosen(menuChosen);
        return this;
    }

    public ArrangementBuilder setRestaurant(Restaurant restaurant){
        this.restaurant = restaurant;
        n.setRestaurant(restaurant);
        return this;
    }

    public ArrangementBuilder setBirthdayChildName(String birthdayChildName){
        this.birthdayChildName = birthdayChildName;
        n.setBirthdayChildName(birthdayChildName);
        return this;
    }

    public ArrangementBuilder setBirthdayChildAge(int birthdayChildAge){
        this.birthdayChildAge = birthdayChildAge;
        n.setBirthdayChildAge(birthdayChildAge);
        return this;
    }

    public ArrangementBuilder setFormerParticipant(String formerParticipant){
        this.formerParticipant = formerParticipant;
        n.setFormerParticipant(formerParticipant);
        return this;
    }

    public ArrangementBuilder setGuide(String guide){
        this.guide = guide;
        n.setGuide(guide);
        return this;
    }

    public ArrangementBuilder setId(int id) {
        this.id = id;
        n.setId(id);
        return this;
    }

    public ArrangementBooking build(){
        //return new ArrangementBooking(id, bookingType, bookingStatus, creationDate, date, participants,
           //     customerComment, comment, menuChosen, restaurant, birthdayChildName, birthdayChildAge, formerParticipant,
             //   guide, customer);
        return n;
    }
}
