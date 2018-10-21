package enums;

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
    }
}
