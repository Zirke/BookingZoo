package enums;

import customExceptions.InvalidEnumException;

public enum ChoiceOfTopic {

    DYR_DERHJEMME {
        @Override
        public String toString() {
            return "Dyr derhjemme";
        }
    },
    HVERDAG_ZOO {
        @Override
        public String toString() {
            return "Hverdagen i Zoo";
        }
    },
    KRYBDYR {
        @Override
        public String toString() {
            return "Krybdyr";
        }
    },
    GROENDLANDS_DYR {
        @Override
        public String toString() {
            return "Grønlands dyr";
        }
    },
    AFRIKAS_SAVANNER {
        @Override
        public String toString() {
            return "Afrikas savanner";
        }
    },
    AKTIVERINGSVAERKSTED {
        @Override
        public String toString() {
            return "Aktiveringsværksted";
        }
    },
    SANSEOPLEVELSER {
        @Override
        public String toString() {
            return "Sanseoplevelser";
        }
    },
    DYRS_TILPASNING {
        @Override
        public String toString() {
            return "Dyrs tilpasning og forskelligheder (Udskoling)";
        }
    },
    EVOLUTION {
        @Override
        public String toString() {
            return "Evolution/Klassifikation (Gymnasium)";
        }
    },
    ZOO_SOM_VIRKSOMHED {
        @Override
        public String toString() {
            return "Aalborg Zoo som virksomhed (Handelsskole)";
        }
    };

    public static ChoiceOfTopic topicChosen(String input) throws InvalidEnumException {

        switch (input) {
            case "Dyr derhjemme":
                return DYR_DERHJEMME;
            case "Hverdagen i Zoo":
                return HVERDAG_ZOO;
            case "Krybdyr":
                return KRYBDYR;
            case "Grønlands dyr":
                return GROENDLANDS_DYR;
            case "Afrikas savanner":
                return AFRIKAS_SAVANNER;
            case "Aktiveringsværksted":
                return AKTIVERINGSVAERKSTED;
            case "Sanseoplevelser":
                return SANSEOPLEVELSER;
            case "Dyrs tilpasning og forskelligheder (Udskoling)":
                return DYRS_TILPASNING;
            case "Evolution/Klassifikation (Gymnasium)":
                return EVOLUTION;
            case "Aalborg Zoo som virksomhed (Handelsskole)":
                return ZOO_SOM_VIRKSOMHED;
            default:
                throw new InvalidEnumException("Input string is not a valid topic: " + input);
        }
    }

}
