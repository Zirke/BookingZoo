package builders;

import Bookings.ArrangementBooking;
import Bookings.FoodOrder;
import Customers.Customer;
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


    public ArrangementBuilder setBookingType(BookingType bookingType){
        this.bookingType = bookingType;
        return this;
    }

    public ArrangementBuilder setBookingStatus(BookingStatus bookingStatus){
        this.bookingStatus = bookingStatus;
        return this;
    }

    public ArrangementBuilder setCustomer(String contactPerson, String phoneNumber, String email){
        this.customer = new Customer(contactPerson, phoneNumber, email);
        return this;
    }

    public ArrangementBuilder setCreationDate(LocalDate creationDate){
        this.creationDate = creationDate;
        return this;
    }

    public ArrangementBuilder setDate(LocalDateTime date){
        this.date = date;
        return this;
    }

    public ArrangementBuilder setParticipants(int participants){
        this.participants = participants;
        return this;
    }

    public ArrangementBuilder setCustomerComment(String customerComment){
        this.customerComment = customerComment;
        return this;
    }

    public ArrangementBuilder setComment(String comment){
        this.comment = comment;
        return this;
    }

    public ArrangementBuilder setMenuChosen(FoodOrder menuChosen){
        this.menuChosen = menuChosen;
        return this;
    }

    public ArrangementBuilder setRestaurant(Restaurant restaurant){
        this.restaurant = restaurant;
        return this;
    }

    public ArrangementBuilder setBirthdayChildName(String birthdayChildName){
        this.birthdayChildName = birthdayChildName;
        return this;
    }

    public ArrangementBuilder setBirthdayChildAge(int birthdayChildAge){
        this.birthdayChildAge = birthdayChildAge;
        return this;
    }

    public ArrangementBuilder setFormerParticipant(String formerParticipant){
        this.formerParticipant = formerParticipant;
        return this;
    }

    public ArrangementBuilder setGuide(String guide){
        this.guide = guide;
        return this;
    }

    public ArrangementBooking build(){
        return new ArrangementBooking(bookingType, bookingStatus, creationDate, date, participants,
                customerComment, comment, menuChosen, restaurant, birthdayChildName, birthdayChildAge, formerParticipant,
                guide, customer);
    }
}
