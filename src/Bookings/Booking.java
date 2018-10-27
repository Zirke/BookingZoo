package Bookings;

import Customers.Customer;
import enums.BookingStatus;
import enums.BookingType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Booking {

    private int id;
    private enums.BookingType bookingType;
    private enums.BookingStatus bookingStatus;
    private Customer customer;
    private LocalDate creationDate;
    private LocalDateTime dateTime;
    private int participants;
    private String customerComment;
    private String comment;

    Booking(int id, BookingType bookingType, BookingStatus bookingStatus, Customer customer, LocalDate creationDate, LocalDateTime dateTime, int participants, String customerComment, String comment) {
        this.id = id;
        this.bookingType = bookingType;
        this.bookingStatus = bookingStatus;
        this.customer = customer;
        this.creationDate = creationDate;
        this.dateTime = dateTime;
        this.participants = participants;
        this.customerComment = customerComment;
        this.comment = comment;
    }

    Booking(BookingType bookingType, BookingStatus bookingStatus, Customer customer, LocalDate creationDate, LocalDateTime dateTime, int participants, String customerComment, String comment) {
        this.bookingType = bookingType;
        this.bookingStatus = bookingStatus;
        this.customer = customer;
        this.creationDate = creationDate;
        this.dateTime = dateTime;
        this.participants = participants;
        this.customerComment = customerComment;
        this.comment = comment;
    }

    public Booking() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public String getCustomerComment() {
        return customerComment;
    }

    public void setCustomerComment(String customerComment) {
        this.customerComment = customerComment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
