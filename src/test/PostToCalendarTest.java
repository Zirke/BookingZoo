package test;

import Bookings.ArrangementBooking;
import Bookings.FoodOrder;
import PostToCalendars.PostToGoogle;
import enums.BookingStatus;
import enums.ChoiceOfMenu;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class PostToCalendarTest {

    @Test
    public void testBirthday() throws IOException, GeneralSecurityException {
        //ArrangementBooking testBirthdayBooking = new ArrangementBooking(BookingStatus.STATUS_ACTIVE, 15,
        //  new FoodOrder(ChoiceOfMenu.MENU_FOUR), "Thomas", 11, "Jonas",
          //      "Karsten", "29125645", "Kasrten@Email.com");
        //PostToGoogle newBirthday = new PostToGoogle(testBirthdayBooking);

        //newBirthday.postNewBIRTHDAYToCalendar();
    }
}