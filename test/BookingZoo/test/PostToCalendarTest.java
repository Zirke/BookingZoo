package BookingZoo.test;

import bookings.ArrangementBooking;
import bookings.FoodOrder;
import bookings.LectureBooking;
import bookings.Lecturer;
import builders.ArrangementBuilder;
import builders.LectureBuilder;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import enums.*;
import facilities.LectureRoom;
import facilities.Restaurant;
import org.junit.Test;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static postToCalendars.PostToGoogle.*;
import static org.junit.Assert.assertEquals;


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

        assertEquals("Ingen kommentar",  commentBookingChecker(useCase));
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

        assertEquals("Ingen kommentar", commentBookingCustomerChecker(useCase));
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


        assertEquals("09", monthsLessThanTen(useCase));
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
                .setGrade(Grade.TENTH);
        useCase = testBooking.build();

        assertEquals("07", daysLessThanTen(useCase));

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
                .setGrade(Grade.TENTH);
        useCase = testBooking.build();

        assertEquals("05", hoursLessThanTen(useCase));
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

        assertEquals("05", minutesLessThanTen(useCase));
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
                .setGrade(Grade.TENTH);
        useCase = testBooking.build();

        assertEquals("2018-11-04T05:30:00+01:00",beginTimeStringBuilder(useCase, monthsLessThanTen(useCase),daysLessThanTen(useCase),hoursLessThanTen(useCase),minutesLessThanTen(useCase)));
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
                .setGrade(Grade.TENTH)
                .setId(999999999);

        useCase = testBooking.build();

        assertEquals("aaaaaa999999999", idChecker(useCase));
    }
    @Test
    public void isIdValid01(){
        ArrangementBuilder arrangementBuilder = new ArrangementBuilder();
        ArrangementBooking useCase;

        arrangementBuilder.setId(351);
        useCase = arrangementBuilder.build();
        assertEquals(false, isIdValid(useCase.getId()));
    }

    @Test
    public void postToCalendarTest01() {
        ArrangementBuilder ArrangementBuilder = new ArrangementBuilder();
        ArrangementBooking useCase = null;
        int id = 8888888;

        String expected = null;
        if (!isIdValid(id)) {
            ++id;
        }

        boolean tryCatch = true;
        while(tryCatch) {
            ArrangementBuilder.setBookingType(BookingType.ARRANGEMENTBOOKING)
                    .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                    .setCustomer("contactperson", "1231341", "mail@mail.com")
                    .setCreationDate(LocalDate.now())
                    .setDate(LocalDateTime.of(2018, 11, 16, 14, 5))
                    .setParticipants(58)
                    .setCustomerComment("Basic comment")
                    .setComment("They might be late")
                    .setMenuChosen(new FoodOrder("02/31-2018", ChoiceOfMenu.MENU_FOUR))
                    .setRestaurant(new Restaurant(FacilityState.UNOCCUPIED))
                    .setBirthdayChildName("testPerson")
                    .setBirthdayChildAge(10)
                    .setFormerParticipant("Ja")
                    .setGuide("Jonas")
                    .setId(id);

            useCase = ArrangementBuilder.build();
            expected = "aaaaaa" + id;

            try {
                postToCalendar(useCase);
                tryCatch = false;
            } catch (GoogleJsonResponseException e) {
                id++;
                tryCatch = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Event testEvent = getEvent(useCase);

        assertEquals(expected, testEvent.getId());
        assertEquals("Fødselsdagsbarn: testPerson", testEvent.getSummary());
        assertEquals("5", testEvent.getColorId());
        assertNotNull(testEvent.getDescription());
        assertNull(testEvent.getLocation());

        try{
        deleteBookingInCalendar(useCase);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void newArrangementEvent01(){
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

        Event testEvent = newArrangementEvent(useCase);

        assertEquals(Long.valueOf(1), Long.valueOf(testEvent.getSequence()));
    }

    @Test
    public void newLectureEvent01(){
        LectureBuilder testBooking = new LectureBuilder();
        LectureBooking useCase;

        testBooking.setBookingType(BookingType.LECTUREBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCustomer("contactperson", "123324234", "mail@mail.com","generic school", 7500,
                        "Holstebro", "Nej", "45604965",456989486)
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.of(2018,11,4,10,30))
                .setParticipants(200)
                .setCustomerComment("Comment")
                .setComment("comment")
                .setLectureRoom(new LectureRoom(FacilityState.OCCUPIED, LectureRoomType.SAVANNAH_TYPE))
                .setLecturer(new Lecturer("Magnus"))
                .setChoiceOfTopic(ChoiceOfTopic.AFRIKAS_SAVANNER)
                .setNoOfTeachers(2)
                .setNoOfTeams(3)
                .setGrade(Grade.TENTH);
        useCase = testBooking.build();

        Event testEvent = newLectureEvent(useCase);

        assertEquals("Savannelokale", testEvent.getLocation());
    }

    @Test
    public void updateArrangement01(){

    }

    @Test
    public void updateLecture01(){

    }
}