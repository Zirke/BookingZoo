package Bookings;

public abstract class Booking {
    public enum bookingType {Skoletjeneste, Boernefoedselsdag}

    ;

    private bookingType type;
    private String date;
    private String time;
    private String contactPerson;
    private int phoneNumber;
    private String email;
    private String comment;

    public Booking(bookingType type, String date, String time, String contactPerson, int phoneNumber, String email, String comment) {
        this.type = type;
        this.date = date;
        this.time = time;
        this.contactPerson = contactPerson;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.comment = comment;
    }

    public Booking(bookingType type) {
        this.type = type;
    }

    public bookingType getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getComment() {
        return comment;
    }

    public void setType(bookingType type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
