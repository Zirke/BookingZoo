package statistics;

import bookings.ArrangementBooking;
import bookings.Booking;
import bookings.LectureBooking;
import customers.LectureBookingCustomer;
import enums.ChoiceOfTopic;

import java.util.ArrayList;
import java.util.Iterator;

public class Statistic {

    //number of participants from schools from Aalborg commune
    public int amountOFSchoolFromAalborgCommune(ArrayList<Booking> lectureBookings){
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

    public int amountOfParticipantsFromSchools(ArrayList<Booking> lecturebookings){
        int amount = 0;

        for (Object lecturebooking : lecturebookings) {
            LectureBooking booking = (LectureBooking) lecturebooking;
            amount += booking.getNoOfTeachers() + booking.getParticipants();
        }
        return amount;
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
