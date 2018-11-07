package enums;

public enum StatisticType {
    STUDENTS_AND_TEACHER{
        @Override
        public String toString() {
            return "Statistik over elever og lærer";
        }
    },
    TOPIC{
        @Override
        public String toString() {
            return "Statistik over emnevalg";
        }
    },
    GRADE{
        @Override
        public String toString() {
            return "Statistik over klassetrin";
        }
    },
    MUNICIPALITY{
        @Override
        public String toString() {
            return "Statistik over Aalborg kommune";
        }
    };

    public static StatisticType toStatisticType(String input){
        switch(input){
            case "Over elever og lærer": return STUDENTS_AND_TEACHER;
            case "Over emnevalg": return TOPIC;
            case "Over klassetrin": return GRADE;
            case "Over Aalborg kommune": return MUNICIPALITY;
            default: throw new IllegalArgumentException();
        }
    }


}
