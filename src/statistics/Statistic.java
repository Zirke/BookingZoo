package statistics;

import bookings.ArrangementBooking;
import bookings.Booking;
import bookings.LectureBooking;
import builders.LectureBuilder;
import customers.LectureBookingCustomer;
import enums.ChoiceOfTopic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import enums.Grade;
public class Statistic {

    //number of participants from schools from Aalborg Municipality
    public static int amountOFSchoolFromAalborgMunicipality(ArrayList<Booking> lectureBookings){
        int amount = 0;
        Iterator iter = lectureBookings.iterator();

        while(iter.hasNext()){
            LectureBooking booking =(LectureBooking)iter.next();
            if(((LectureBookingCustomer)booking.getCustomer()).getCommune().equals("Ja")){
                amount += booking.getNoOfTeachers() + booking.getParticipants();
            }
        }
        return amount;
    }

    //Amount of students from the lecture bookings.
    public static int amountOfStudentsFromSchools(ArrayList<Booking> lecturebookings){
        int amount = 0;

        for (Object lecturebooking : lecturebookings) {
            LectureBooking booking = (LectureBooking) lecturebooking;
            amount += booking.getParticipants();
        }
        return amount;
    }

    //Amount of teachers from the lecture bookings.
    public static int amountOFTeachers(ArrayList<Booking> lecturebookings){
        int amount = 0;

        for (Object lecturebooking : lecturebookings) {
            LectureBooking booking = (LectureBooking) lecturebooking;
            amount += booking.getNoOfTeachers();
        }
        return amount;
    }

    public static HashMap<Grade, Integer> amountStudentsInClass(ArrayList<Booking> lecturebookings){
        HashMap<Grade, Integer> amountInEachClass = new HashMap<>();
        initialiseGradeHashMap(amountInEachClass);

        for(Object x : lecturebookings){
            LectureBooking i = (LectureBooking) x;
            switch (i.getGrade()){
                case PRESCHOOL: {
                    amountInEachClass.put(Grade.PRESCHOOL, amountInEachClass.get(Grade.PRESCHOOL).intValue() + 1);
                }break;
                case FIRST:{
                    amountInEachClass.put(Grade.FIRST, amountInEachClass.get(Grade.FIRST).intValue() + 1);
                }
                    break;
                case SECOND:{
                    amountInEachClass.put(Grade.SECOND, amountInEachClass.get(Grade.SECOND).intValue() + 1);
                }
                    break;
                case THIRD:{
                    amountInEachClass.put(Grade.THIRD, amountInEachClass.get(Grade.THIRD).intValue() + 1);
                }
                    break;
                case FOURTH:{
                    amountInEachClass.put(Grade.FOURTH, amountInEachClass.get(Grade.FOURTH).intValue() + 1);
                }
                    break;
                case FIFTH:
                {
                    amountInEachClass.put(Grade.FIFTH, amountInEachClass.get(Grade.FIFTH).intValue() + 1);
                }
                    break;
                case SIXTH:{
                    amountInEachClass.put(Grade.SIXTH, amountInEachClass.get(Grade.SIXTH).intValue() + 1);
                }
                    break;
                case SEVENTH:{
                    amountInEachClass.put(Grade.SEVENTH, amountInEachClass.get(Grade.SEVENTH).intValue() + 1);
                }
                    break;
                case EIGHTH:{
                    amountInEachClass.put(Grade.EIGHTH, amountInEachClass.get(Grade.EIGHTH).intValue() + 1);
                }
                    break;
                case NINTH:{
                    amountInEachClass.put(Grade.NINTH, amountInEachClass.get(Grade.NINTH).intValue() + 1);
                }
                    break;
                case TENTH:{
                    amountInEachClass.put(Grade.TENTH, amountInEachClass.get(Grade.TENTH).intValue() + 1);
                }
                    break;
                case ONEG:{
                    amountInEachClass.put(Grade.ONEG, amountInEachClass.get(Grade.ONEG).intValue() + 1);
                }
                    break;
                case SECONDG:{
                    amountInEachClass.put(Grade.SECONDG, amountInEachClass.get(Grade.SECONDG).intValue() + 1);
                }
                    break;
                case THIRDG:{
                    amountInEachClass.put(Grade.THIRDG, amountInEachClass.get(Grade.THIRDG).intValue() + 1);
                }
                    break;
            }
        }
        return amountInEachClass;
    }

    private static void initialiseGradeHashMap(HashMap<Grade, Integer> amountInEachClass){

        initialiseGradeForHashMap(Grade.PRESCHOOL, amountInEachClass);
        initialiseGradeForHashMap(Grade.FIRST, amountInEachClass);
        initialiseGradeForHashMap(Grade.SECOND, amountInEachClass);
        initialiseGradeForHashMap(Grade.THIRD, amountInEachClass);
        initialiseGradeForHashMap(Grade.FOURTH, amountInEachClass);
        initialiseGradeForHashMap(Grade.FIFTH, amountInEachClass);
        initialiseGradeForHashMap(Grade.SIXTH, amountInEachClass);
        initialiseGradeForHashMap(Grade.SEVENTH, amountInEachClass);
        initialiseGradeForHashMap(Grade.EIGHTH, amountInEachClass);
        initialiseGradeForHashMap(Grade.NINTH, amountInEachClass);
        initialiseGradeForHashMap(Grade.TENTH, amountInEachClass);
        initialiseGradeForHashMap(Grade.ONEG, amountInEachClass);
        initialiseGradeForHashMap(Grade.SECONDG, amountInEachClass);
        initialiseGradeForHashMap(Grade.THIRDG, amountInEachClass);

    }

    private static void initialiseGradeForHashMap(Grade grade, HashMap<Grade, Integer> amountInEachClass){
        if(!amountInEachClass.containsKey(grade)){
            amountInEachClass.put(grade,0);
        }
    }



    public static int amountOfArrangementParticipants(ArrayList<Booking> arrangements){
        int amount = 0;

        for (Object arrangement : arrangements) {
            amount += ((ArrangementBooking) arrangement).getParticipants();
        }
        return amount;
    }

    //test om nested if virker
    public static int amountInGivenMonth(int month, int year, ArrayList<Booking> allBookings){
        int amount = 0;
        Iterator iter = allBookings.iterator();

        while(iter.hasNext()){
            Booking temp = (Booking)iter.next();
            if(temp.getDateTime().getMonthValue() == month && temp.getDateTime().getYear() == year){
                amount += temp.getParticipants();
                if(LectureBooking.class.isInstance(temp)){
                    amount += ((LectureBooking)temp).getNoOfTeachers();
                }
            }
        }

        return amount;
    }

    public static int amountOfChosenCategory(ChoiceOfTopic topic, ArrayList<Booking> allBookings){
        int amount = 0;
        Iterator iter = allBookings.iterator();

        while(iter.hasNext()){
            Booking temp = (Booking)iter.next();
            if(LectureBooking.class.isInstance(temp) && ((LectureBooking)temp).getChoiceOfTopic().equals(topic)){
                amount += ((LectureBooking) temp).getNoOfTeams(); // ask Soren
            }
        }

        return amount;
    }

}
