package Bookings;

import java.util.Objects;

public class LectureBooking extends Booking {
    private String noOfPupils;
    private String noOfTeams;
    private String noOfTeachers;
    private String grade;
    private String choiceOfTopic;
    private String schoolName;
    private String zipCode;
    private String city;
    private String commune;
    private String schoolPhoneNumber;
    private String eanNumber;

    public LectureBooking(bookingType type, String date, String time, String contactPerson, String phoneNumber,
                          String email, String comment, String noOfPupils, String noOfTeams, String noOfTeachers,
                          String grade, String choiceOfTopic, String schoolName, String zipCode, String city,
                          String commune, String schoolPhoneNumber, String eanNumber) {
        super(type, date, time, contactPerson, phoneNumber, email, comment);
        this.noOfPupils = noOfPupils;
        this.noOfTeams = noOfTeams;
        this.noOfTeachers = noOfTeachers;
        this.grade = grade;
        this.choiceOfTopic = choiceOfTopic;
        this.schoolName = schoolName;
        this.zipCode = zipCode;
        this.city = city;
        this.commune = commune;
        this.schoolPhoneNumber = schoolPhoneNumber;
        this.eanNumber = eanNumber;
    }

    public bookingType getType() {
        return type;
    }

    public String getNoOfPupils() {
        return noOfPupils;
    }

    public void setNoOfPupils(String noOfPupils) {
        this.noOfPupils = noOfPupils;
    }

    public String getNoOfTeams() {
        return noOfTeams;
    }

    public void setNoOfTeams(String noOfTeams) {
        this.noOfTeams = noOfTeams;
    }

    public String getNoOfTeachers() {
        return noOfTeachers;
    }

    public void setNoOfTeachers(String noOfTeachers) {
        this.noOfTeachers = noOfTeachers;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getSchoolPhoneNumber() {
        return schoolPhoneNumber;
    }

    public void setSchoolPhoneNumber(String schoolPhoneNumber) {
        this.schoolPhoneNumber = schoolPhoneNumber;
    }

    public String getEanNumber() {
        return eanNumber;
    }

    public void setEanNumber(String eanNumber) {
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
                Objects.equals(choiceOfTopic, that.choiceOfTopic) &&
                Objects.equals(schoolName, that.schoolName) &&
                Objects.equals(zipCode, that.zipCode) &&
                Objects.equals(city, that.city) &&
                Objects.equals(commune, that.commune) &&
                Objects.equals(schoolPhoneNumber, that.schoolPhoneNumber) &&
                Objects.equals(eanNumber, that.eanNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noOfPupils, noOfTeams, noOfTeachers, grade, choiceOfTopic, schoolName, zipCode, city, commune, schoolPhoneNumber, eanNumber);
    }

    @Override
    public String toString() {
        return getType() + "\t" + "\t" + "\t" + "\t" +
                getContactPerson() + "\t" + "\t" + "\t" + "\t" + "\t" +
                getDate();
    }
}
