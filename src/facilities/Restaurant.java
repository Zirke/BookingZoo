package facilities;

import enums.FacilityState;
import enums.RestaurantType;

public class Restaurant extends Facility {

    private RestaurantType type;

    public Restaurant(FacilityState state) {
        super(state);
    }

    public Restaurant(FacilityState state, RestaurantType type) {
        super(state);
        this.type = type;
    }

    public RestaurantType getType() {
        return type;
    }

    public void setType(RestaurantType type) {
        this.type = type;
    }
}
