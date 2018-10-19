package facilities;

import enums.FacilityState;

public abstract class Facility {
    private FacilityState state;

    public Facility(FacilityState state) {
        this.state = state;
    }

    public FacilityState getState() {
        return state;
    }

    public void setState(FacilityState state) {
        this.state = state;
    }
}
