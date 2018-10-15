package Bookings;

import java.util.Objects;

public class LectureBooking {
    private String type;
    private String contactPerson;
    private String date;

    public LectureBooking(String type, String contactPerson, String date) {
        this.type = type;
        this.contactPerson = contactPerson;
        this.date = date;
    }

    public LectureBooking() {
    }

    public String getType() {
        return type;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getDate() {
        return date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LectureBooking)) return false;
        LectureBooking that = (LectureBooking) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(contactPerson, that.contactPerson) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, contactPerson, date);
    }

    @Override
    public String toString() {
        return  type + '\t' +
                contactPerson + '\t' +
                date;
    }
}
