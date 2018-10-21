package Customers;

public class LectureBookingCustomer extends Customer {
    private String schoolName;
    private int zipCode;
    private String city;
    private String commune;
    private String schoolPhoneNumber;
    private long eanNumber;

    public LectureBookingCustomer(String contactPerson, String phoneNumber, String email, String schoolName,
                                  int zipCode, String city, String commune, String schoolPhoneNumber, long eanNumber) {
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

    public long getEanNumber() {
        return eanNumber;
    }

    public void setEanNumber(long eanNumber) {
        this.eanNumber = eanNumber;
    }
}

