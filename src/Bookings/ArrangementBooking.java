package Bookings;

import java.util.Objects;

public class ArrangementBooking extends Booking {
    public enum choiceOfMenu {
        MENU_ONE {
            @Override
            public String toString() {
                return "Klassisk menu med kakao, boller og kage 98,-";
            }
        },
        MENU_TWO {
            @Override
            public String toString() {
                return "Menu med frikadeller og dessert 75,-";
            }
        },
        MENU_THREE {
            @Override
            public String toString() {
                return "Menu med pasta og kødsovs og dessert 75,-";
            }
        },
        MENU_FOUR {
            @Override
            public String toString() {
                return "Menu med nuggets og pommes frites 50,-";
            }
        },
        NO_FOOD {
            @Override
            public String toString() {
                return "Mad fra Skovbakken ikke ønsket";
            }
        },
    }

    private String noOfChildren;
    private String birthdayChildName;
    private String birthdayChildAge;
    private String formerParticipant;
    private choiceOfMenu menuChosen;

    public ArrangementBooking(bookingType type, String date, String time, String contactPerson, String phoneNumber,
                              String email, String comment, String noOfChildren, String birthdayChildName,
                              String birthdayChildAge, String formerParticipant, choiceOfMenu menuChosen) {
        super(type, date, time, contactPerson, phoneNumber, email, comment);
        this.noOfChildren = noOfChildren;
        this.birthdayChildName = birthdayChildName;
        this.birthdayChildAge = birthdayChildAge;
        this.formerParticipant = formerParticipant;
        this.menuChosen = menuChosen;
    }

    public bookingType getType() {
        return type;
    }

    public String getNoOfChildren() {
        return noOfChildren;
    }

    public void setNoOfChildren(String noOfChildren) {
        this.noOfChildren = noOfChildren;
    }

    public String getBirthdayChildName() {
        return birthdayChildName;
    }

    public void setBirthdayChildName(String birthdayChildName) {
        this.birthdayChildName = birthdayChildName;
    }

    public String getBirthdayChildAge() {
        return birthdayChildAge;
    }

    public void setBirthdayChildAge(String birthdayChildAge) {
        this.birthdayChildAge = birthdayChildAge;
    }

    public String getFormerParticipant() {
        return formerParticipant;
    }

    public void setFormerParticipant(String formerParticipant) {
        this.formerParticipant = formerParticipant;
    }

    public choiceOfMenu getMenuChosen() {
        return menuChosen;
    }

    public void setMenuChosen(choiceOfMenu menuChosen) {
        this.menuChosen = menuChosen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrangementBooking)) return false;
        ArrangementBooking that = (ArrangementBooking) o;
        return Objects.equals(noOfChildren, that.noOfChildren) &&
                Objects.equals(birthdayChildName, that.birthdayChildName) &&
                Objects.equals(birthdayChildAge, that.birthdayChildAge) &&
                Objects.equals(formerParticipant, that.formerParticipant) &&
                menuChosen == that.menuChosen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(noOfChildren, birthdayChildName, birthdayChildAge, formerParticipant, menuChosen);
    }

    @Override
    public String toString() {
        return getType() + "\t" +
                getContactPerson() + "\t" +
                getDate() + "\t" +
                getStatus();
    }
}
