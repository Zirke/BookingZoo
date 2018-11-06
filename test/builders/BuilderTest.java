package builders;

import Bookings.ArrangementBooking;
import Bookings.FoodOrder;
import Bookings.LectureBooking;
import Bookings.Lecturer;
import builders.ArrangementBuilder;
import builders.LectureBuilder;
import enums.*;
import facilities.LectureRoom;
import facilities.Restaurant;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class BuilderTest {

    @Test
    public void arrangementBuild(){
        ArrangementBuilder builder = new ArrangementBuilder();
        ArrangementBooking testCase;

        builder.setBookingType(BookingType.ARRANGEMENTBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCustomer("contactperson", "1231341", "simon@hotmail.dk")
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.now())
                .setParticipants(58)
                .setCustomerComment("hallo")
                .setComment("haallo2")
                .setMenuChosen(new FoodOrder("22/31-2018",ChoiceOfMenu.MENU_FOUR))
                .setRestaurant(new Restaurant(FacilityState.UNOCCUPIED))
                .setBirthdayChildName("testPerson")
                .setBirthdayChildAge(10)
                .setFormerParticipant("nej")
                .setGuide("Christoffer haha");
        testCase = builder.build();

        assertEquals("testPerson",testCase.getBirthdayChildName());

    }

    @Test
    public void lectureBuild(){
        LectureBuilder builder = new LectureBuilder();
        LectureBooking testCase;
        builder.setBookingType(BookingType.ARRANGEMENTBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCustomer("contactperson", "1231341", "simon@hotmail.dk", "skolenavn",
                        9000, "Aalborg", "Aalborg Kommune", "918924", 3237893)
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.now())
                .setParticipants(58)
                .setCustomerComment("hallo")
                .setComment("haallo2")
                .setLectureRoom(new LectureRoom(FacilityState.UNOCCUPIED, LectureRoomType.BIOLOGICAL_TYPE))
                .setLecturer(new Lecturer("jens"))
                .setChoiceOfTopic(ChoiceOfTopic.AFRIKAS_SAVANNER)
                .setNoOfTeams(3)
                .setNoOfTeachers(6)
                .setGrade("7");
        testCase = builder.build();
        assertEquals("hallo",testCase.getCustomerComment());
    }

}
