package Bookings;

//
public abstract class Booking {
    public enum bookingType {
        Skoletjeneste {
            @Override
            public String toString() {
                return "Skoletjeneste";
            }
        },
        Boernefoedselsdag {
            @Override
            public String toString() {
                return "Børnefødselsdag";
            }
        }
    }

    public enum bookingStatus {Afventende, Aktiv, Faerdig, Arkiveret}

    int id;
    bookingType type;
    private bookingStatus status = bookingStatus.Afventende;
    private String date;
    private String time;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String comment;

    public Booking(int id, bookingType type, String date, String time, String contactPerson, String phoneNumber, String email, String comment) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.time = time;
        this.contactPerson = contactPerson;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public bookingType getType() {
        return type;
    }

    public bookingStatus getStatus() {
        return status;
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

    public String getPhoneNumber() {
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
