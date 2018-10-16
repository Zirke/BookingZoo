package Bookings;

import java.util.Objects;

public class LectureBooking extends Booking {
    private int noOfPupils;
    private int noOfTeams;
    private int noOfTeachers;
    private int grade;
    private String choiceOfTopic;
    private String schoolName;
    private int zipCode;
    private String city;
    private String commune;
    private int schoolPhoneNumber;
    private int eanNumber;

    public LectureBooking(bookingType type, String date, String time, String contactPerson, int phoneNumber, String email, String comment, int noOfPupils, int noOfTeams, int noOfTeachers, int grade, String choiceOfTopic, String schoolName, int zipCode, String city, int schoolPhoneNumber, int eanNumber) {
        super(type, date, time, contactPerson, phoneNumber, email, comment);
        this.noOfPupils = noOfPupils;
        this.noOfTeams = noOfTeams;
        this.noOfTeachers = noOfTeachers;
        this.grade = grade;
        this.choiceOfTopic = choiceOfTopic;
        this.schoolName = schoolName;
        this.zipCode = zipCode;
        this.city = city;
        this.schoolPhoneNumber = schoolPhoneNumber;
        this.eanNumber = eanNumber;
    }

    public int getNoOfPupils() {
        return noOfPupils;
    }

    public void setNoOfPupils(int noOfPupils) {
        this.noOfPupils = noOfPupils;
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

    public String getChoiceOfTopic() {
        return choiceOfTopic;
    }

    public void setChoiceOfTopic(String choiceOfTopic) {
        this.choiceOfTopic = choiceOfTopic;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getSchoolPhoneNumber() {
        return schoolPhoneNumber;
    }

    public void setSchoolPhoneNumber(int schoolPhoneNumber) {
        this.schoolPhoneNumber = schoolPhoneNumber;
    }

    public int getEanNumber() {
        return eanNumber;
    }

    public void setEanNumber(int eanNumber) {
        this.eanNumber = eanNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LectureBooking)) return false;
        LectureBooking that = (LectureBooking) o;
        return Objects.equals(noOfPupils, that.noOfPupils) &&
                Objects.equals(noOfTeams, that.noOfTeams) &&
                Objects.equals(noOfTeachers, that.noOfTeachers) &&
                Objects.equals(grade, that.grade) &&
                Objects.equals(schoolName, that.schoolName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noOfPupils, noOfTeams, noOfTeachers, grade, schoolName);
    }

    @Override
    public String toString() {
        return getType() + "\t" + "\t" + "\t" + "\t" +
                getContactPerson() + "\t" + "\t" + "\t" + "\t" + "\t" +
                getDate();
    }
}
