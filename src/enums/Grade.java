package enums;

public enum Grade {
    PRESCHOOL,
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    FIFTH,
    SIXTH,
    SEVENTH,
    EIGHTH,
    NINTH,
    TENTH,
    ONEG,
    SECONDG,
    THIRDG;


    @Override
    public String toString() {
        String returnSring = "somethingWentWrong";
        switch (this){
            case PRESCHOOL: returnSring = "børnehaveklasse";
                break;
            case FIRST: returnSring = "første klasse";
                break;
            case SECOND: returnSring = "anden klasse";
                break;
            case THIRD: returnSring = "tredje klasse";
                break;
            case FOURTH: returnSring = "fjerde klasse";
                break;
            case FIFTH: returnSring = "femte klasse";
                break;
            case SIXTH: returnSring = "sjette klasse";
                break;
            case SEVENTH: returnSring = "syvende klasse";
                break;
            case EIGHTH: returnSring = "ottende klasse";
                break;
            case NINTH: returnSring = "nine klasse";
                break;
            case TENTH: returnSring = "tiende klasse";
                break;
            case ONEG: returnSring = "første G";
                break;
            case SECONDG: returnSring = "anden G";
                break;
            case THIRDG: returnSring = "trejde G";
                break;
        }

        return returnSring;
    }
}
