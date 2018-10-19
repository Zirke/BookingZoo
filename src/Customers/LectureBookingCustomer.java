package Customers;

public class LectureBookingCustomer extends Customer {
    private String schoolName;
    private String zipCode;
    private String city;
    private String commune;
    private String schoolPhoneNumber;
    private String eanNumber;

    public LectureBookingCustomer(String contactPerson, String phoneNumber, String email, String schoolName,
                                  String zipCode, String city, String commune, String schoolPhoneNumber, String eanNumber) {
        super(contactPerson, phoneNumber, email);
        this.schoolName = schoolName;
        this.zipCode = zipCode;
        this.city = city;
        this.commune = commune;
        this.schoolPhoneNumber = schoolPhoneNumber;
        this.eanNumber = eanNumber;
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
}

