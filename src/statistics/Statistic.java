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
    public int amountOFSchoolFromAalborgMunicipality(ArrayList<Booking> lectureBookings){
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
    public int amountOfStudentsFromSchools(ArrayList<Booking> lecturebookings){
        int amount = 0;

        for (Object lecturebooking : lecturebookings) {
            LectureBooking booking = (LectureBooking) lecturebooking;
            amount += booking.getParticipants();
        }
        return amount;
    }

    //Amount of teachers from the lecture bookings.
    public int amountOFTeachers(ArrayList<Booking> lecturebookings){
        int amount = 0;

        for (Object lecturebooking : lecturebookings) {
            LectureBooking booking = (LectureBooking) lecturebooking;
            amount += booking.getNoOfTeachers();
        }
        return amount;
    }

    public HashMap<Grade, Integer> amountStudentsInClass(ArrayList<Booking> lecturebookings){
        HashMap<Grade, Integer> amountInEachClass = new HashMap<>();

        for(Object x : lecturebookings){
            LectureBooking i = (LectureBooking) x;
            switch (i.getGrade()){
                case PRESCHOOL: break;
            }
        }
        return amountInEachClass;
    }






    public int amountOfArrangementParticipants(ArrayList<Booking> arrangements){
        int amount = 0;

        for (Object arrangement : arrangements) {
            amount += ((ArrangementBooking) arrangement).getParticipants();
        }
        return amount;
    }

    //test om nested if virker
    public int amountInGivenMonth(int month, int year, ArrayList<Booking> allBookings){
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

    public int amountOfChosenCategory(ChoiceOfTopic topic, ArrayList<Booking> allBookings){
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
