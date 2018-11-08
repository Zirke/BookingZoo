package userInterface;

import bookings.LectureBooking;
import builders.LectureBuilder;
import enums.BookingStatus;
import enums.BookingType;
import org.junit.Ignore;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class MainScreenControllerTest {
    MainScreenController mainScreenController = new MainScreenController();

    public MainScreenControllerTest() throws SQLException, ClassNotFoundException {
    }

    @Ignore
    public void moveBookingToArchived01() {
        LectureBuilder builder = new LectureBuilder();
        LectureBooking testCase;

        builder.setBookingType(BookingType.LECTUREBOOKING)
                .setBookingStatus(BookingStatus.STATUS_ACTIVE)
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.of(2018, 11, 5, 12, 12));
        testCase = builder.build();

//        mainScreenController.moveBookingToArchived(testCase);

        assertEquals(BookingStatus.STATUS_ARCHIVED, testCase.getBookingStatus());
    }
}
