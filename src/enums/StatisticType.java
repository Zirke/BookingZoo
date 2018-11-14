package enums;

public enum StatisticType {
    STUDENTS_AND_TEACHER{
        @Override
        public String toString() {
            return "Statistik over elever og lærere";
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
            return "Statistik over Aalborg Kommune";
        }
    },
    FOOD{
        @Override
        public String toString(){return "Statistik over valgte menuer";}
    };

    public static StatisticType toStatisticType(String input){
        switch(input){
            case "Over elever og lærere": return STUDENTS_AND_TEACHER;
            case "Over emnevalg": return TOPIC;
            case "Over klassetrin": return GRADE;
            case "Over Aalborg Kommune": return MUNICIPALITY;
            case "Over valgte menuer": return FOOD;
            default: throw new IllegalArgumentException();
        }
    }


}
