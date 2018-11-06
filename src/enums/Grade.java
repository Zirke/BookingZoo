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
        switch (this) {
            case PRESCHOOL:
                returnSring = "Børnehaveklasse";
                break;
            case FIRST:
                returnSring = "1. klasse";
                break;
            case SECOND:
                returnSring = "2. klasse";
                break;
            case THIRD:
                returnSring = "3. klasse";
                break;
            case FOURTH:
                returnSring = "4. klasse";
                break;
            case FIFTH:
                returnSring = "5. klasse";
                break;
            case SIXTH:
                returnSring = "6. klasse";
                break;
            case SEVENTH:
                returnSring = "7. klasse";
                break;
            case EIGHTH:
                returnSring = "8. klasse";
                break;
            case NINTH:
                returnSring = "9. klasse";
                break;
            case TENTH:
                returnSring = "10. klasse";
                break;
            case ONEG:
                returnSring = "1.G";
                break;
            case SECONDG:
                returnSring = "2.G";
                break;
            case THIRDG:
                returnSring = "3.G";
                break;
        }
        return returnSring;
    }

    public static Grade gradeChosen(String input) {
        switch (input) {
            case "Børnehaveklasse":
                return PRESCHOOL;
            case "1. klasse":
                return FIRST;
            case "2. klasse":
                return SECOND;
            case "3. klasse":
                return THIRD;
            case "4. klasse":
                return FOURTH;
            case "5. klasse":
                return FIFTH;
            case "6. klasse":
                return SIXTH;
            case "7. klasse":
                return SEVENTH;
            case "8. klasse":
                return EIGHTH;
            case "9. klasse":
                return NINTH;
            case "10. klasse":
                return TENTH;
            case "1.G":
                return ONEG;
            case "2.G":
                return SECONDG;
            case "3.G":
                return THIRDG;
            default:
                throw new IllegalArgumentException();
        }
    }
}