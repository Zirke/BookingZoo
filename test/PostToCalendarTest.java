package BookingZoo.test;

import Bookings.ArrangementBooking;
import Bookings.FoodOrder;
import Bookings.LectureBooking;
import Bookings.Lecturer;
import PostToCalendars.PostToGoogle;
import builders.ArrangementBuilder;
import builders.LectureBuilder;
import enums.*;
import facilities.LectureRoom;
import facilities.Restaurant;
import org.junit.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.Assert.*;


public class PostToCalendarTest {


    //This test assures that if the comment field is empty, the method will set a predefined comment for the calendar to post.
    @Test
    public void testEmptyComment() {
        ArrangementBuilder testBooking = new ArrangementBuilder();
        ArrangementBooking useCase;

        testBooking.setBookingType(BookingType.ARRANGEMENTBOOKING)
                    .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                    .setCustomer("contactperson", "1231341", "mail@mail.com")
                    .setCreationDate(LocalDate.now())
                    .setDate(LocalDateTime.now())
                    .setParticipants(58)
                    .setCustomerComment("We might be a few minutes late")
                    .setComment("")
                    .setMenuChosen(new FoodOrder("02/31-2018",ChoiceOfMenu.MENU_FOUR))
                    .setRestaurant(new Restaurant(FacilityState.UNOCCUPIED))
                    .setBirthdayChildName("testPerson")
                    .setBirthdayChildAge(10)
                    .setFormerParticipant("Ja")
                    .setGuide("Jonas");
        useCase = testBooking.build();

        PostToGoogle tempPost = new PostToGoogle(useCase);

         assertEquals("Ingen kommentar",  tempPost.commentBookingChecker(useCase));
    }

    // This test checks if the customer comment field is left null somewhere in the program
    @Test
    public void testEmptyCustomerComment() {
        ArrangementBuilder testBooking = new ArrangementBuilder();
        ArrangementBooking useCase;

        testBooking.setBookingType(BookingType.ARRANGEMENTBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCustomer("contactperson", "1231341", "mail@mail.com")
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.now())
                .setParticipants(58)
                .setCustomerComment(null)
                .setComment("They might be late")
                .setMenuChosen(new FoodOrder("02/31-2018", ChoiceOfMenu.MENU_FOUR))
                .setRestaurant(new Restaurant(FacilityState.UNOCCUPIED))
                .setBirthdayChildName("testPerson")
                .setBirthdayChildAge(10)
                .setFormerParticipant("Ja")
                .setGuide("Jonas");
        useCase = testBooking.build();

        PostToGoogle tempPost = new PostToGoogle(useCase);

        assertEquals("Ingen kommentar", tempPost.commentBookingCustomerChecker(useCase));
    }

    /*Tests when the given LocalDateTime is given to the method that it converts it to a string and adds a "0" if the int is less than 10.
    the reason for this is the google calendar only accepts a specific format on the string
     */
    @Test
    public void testIfMonthsAreLessThanTen(){
        ArrangementBuilder testBooking = new ArrangementBuilder();
        ArrangementBooking useCase;

        testBooking.setBookingType(BookingType.ARRANGEMENTBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCustomer("contactperson", "1231341", "mail@mail.com")
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.of(2018,9,16,14,0))
                .setParticipants(58)
                .setCustomerComment("Basic comment")
                .setComment("They might be late")
                .setMenuChosen(new FoodOrder("02/31-2018", ChoiceOfMenu.MENU_FOUR))
                .setRestaurant(new Restaurant(FacilityState.UNOCCUPIED))
                .setBirthdayChildName("testPerson")
                .setBirthdayChildAge(10)
                .setFormerParticipant("Ja")
                .setGuide("Jonas");
        useCase = testBooking.build();

        PostToGoogle tempPost = new PostToGoogle(useCase);

        assertEquals("09", tempPost.monthsLessThanTen(useCase));
    }

    /* This test is done with a lecture booking instead of an arrangement booking like the previous examples to show both types of bookings work.
    the method tests if the input day is less than 10, it then converts it to a proper format with a "0" in front of the number
     */
    @Test
    public void testIfDaysAreLessThanTen(){
        LectureBuilder testBooking = new LectureBuilder();
        LectureBooking useCase;

        testBooking.setBookingType(BookingType.LECTUREBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCustomer("contactperson", "123324234", "mail@mail.com","generic school", 7500,
                        "Holstebro", "Nej", "45604965",456989486)
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.of(2018,11,7,12,30))
                .setParticipants(200)
                .setCustomerComment("Comment")
                .setComment("comment")
                .setLectureRoom(new LectureRoom(FacilityState.OCCUPIED, LectureRoomType.SAVANNAH_TYPE))
                .setLecturer(new Lecturer("Magnus"))
                .setChoiceOfTopic(ChoiceOfTopic.AFRIKAS_SAVANNER)
                .setNoOfTeachers(2)
                .setNoOfTeams(3)
                .setGrade("7");
        useCase = testBooking.build();

        PostToGoogle tempPost = new PostToGoogle(useCase);

        assertEquals("07", tempPost.daysLessThanTen(useCase));

    }
    /* This test checks if the hours input are less than 10 on this specific lecture booking.
    * */
    @Test
    public void testIfHoursLessThanTen(){
        LectureBuilder testBooking = new LectureBuilder();
        LectureBooking useCase;

        testBooking.setBookingType(BookingType.LECTUREBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCustomer("contactperson", "123324234", "mail@mail.com","generic school", 7500,
                        "Holstebro", "Nej", "45604965",456989486)
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.of(2018,11,10,5,30))
                .setParticipants(200)
                .setCustomerComment("Comment")
                .setComment("comment")
                .setLectureRoom(new LectureRoom(FacilityState.OCCUPIED, LectureRoomType.SAVANNAH_TYPE))
                .setLecturer(new Lecturer("Magnus"))
                .setChoiceOfTopic(ChoiceOfTopic.AFRIKAS_SAVANNER)
                .setNoOfTeachers(2)
                .setNoOfTeams(3)
                .setGrade("7");
        useCase = testBooking.build();

        PostToGoogle tempPost = new PostToGoogle(useCase);

        assertEquals("05", tempPost.hoursLessThanTen(useCase));
    }

    /*
    This is the last test that checks whether the minutes input are less than 10 this is tested on the arrangement booking
     */
    @Test
    public void testIfMinutesLessThanTen(){
        ArrangementBuilder testBooking = new ArrangementBuilder();
        ArrangementBooking useCase;

        testBooking.setBookingType(BookingType.ARRANGEMENTBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCustomer("contactperson", "1231341", "mail@mail.com")
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.of(2018,11,16,14,5))
                .setParticipants(58)
                .setCustomerComment("Basic comment")
                .setComment("They might be late")
                .setMenuChosen(new FoodOrder("02/31-2018", ChoiceOfMenu.MENU_FOUR))
                .setRestaurant(new Restaurant(FacilityState.UNOCCUPIED))
                .setBirthdayChildName("testPerson")
                .setBirthdayChildAge(10)
                .setFormerParticipant("Ja")
                .setGuide("Jonas");
        useCase = testBooking.build();

        PostToGoogle tempPost = new PostToGoogle(useCase);

        assertEquals("05", tempPost.minutesLessThanTen(useCase));
    }
    /* This test will check if the string is put together correctly when
    assembling the years,months,days,hours,minutes, and giving google calendar the correct string */
    @Test
    public void testOfBuilderString(){
        LectureBuilder testBooking = new LectureBuilder();
        LectureBooking useCase;

        testBooking.setBookingType(BookingType.LECTUREBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCustomer("contactperson", "123324234", "mail@mail.com","generic school", 7500,
                        "Holstebro", "Nej", "45604965",456989486)
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.of(2018,11,4,5,30))
                .setParticipants(200)
                .setCustomerComment("Comment")
                .setComment("comment")
                .setLectureRoom(new LectureRoom(FacilityState.OCCUPIED, LectureRoomType.SAVANNAH_TYPE))
                .setLecturer(new Lecturer("Magnus"))
                .setChoiceOfTopic(ChoiceOfTopic.AFRIKAS_SAVANNER)
                .setNoOfTeachers(2)
                .setNoOfTeams(3)
                .setGrade("7");
        useCase = testBooking.build();

        PostToGoogle tempPost = new PostToGoogle(useCase);

        assertEquals("2018-11-04T05:30:00+01:00",tempPost.beginTimeStringBuilder(useCase, tempPost.monthsLessThanTen(useCase),tempPost.daysLessThanTen(useCase),tempPost.hoursLessThanTen(useCase),tempPost.minutesLessThanTen(useCase)));
    }

    @Test
    public void testOfIdChecker(){
        LectureBuilder testBooking = new LectureBuilder();
        LectureBooking useCase;

        testBooking.setBookingType(BookingType.LECTUREBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCustomer("contactperson", "123324234", "mail@mail.com","generic school", 7500,
                        "Holstebro", "Nej", "45604965",456989486)
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.now())
                .setParticipants(200)
                .setCustomerComment("Comment")
                .setComment("comment")
                .setLectureRoom(new LectureRoom(FacilityState.OCCUPIED, LectureRoomType.SAVANNAH_TYPE))
                .setLecturer(new Lecturer("Magnus"))
                .setChoiceOfTopic(ChoiceOfTopic.AFRIKAS_SAVANNER)
                .setNoOfTeachers(2)
                .setNoOfTeams(3)
                .setGrade("7")
                .setId(999999999);
        useCase = testBooking.build();

        PostToGoogle tempPost = new PostToGoogle(useCase);

        assertEquals("aaaaaa999999999", tempPost.idChecker(useCase));
    }
}