package exception;

import facilities.Facility;

public class IllegalFacilityException extends RuntimeException {
    Facility facility;

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public IllegalFacilityException(Facility facil){
        setFacility(facil);
    }
}
