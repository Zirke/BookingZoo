package BookingZoo.test;

import bookings.LectureBooking;
import builders.LectureBuilder;
import enums.BookingStatus;
import enums.BookingType;
import org.junit.Ignore;
import userInterface.MainScreenController;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MainScreenControllerTest {
    MainScreenController mainScreenController = new MainScreenController();

    public MainScreenControllerTest() throws SQLException, ClassNotFoundException {
    }

    @Ignore
    public void moveBookingToArchived01() {
        LectureBuilder builder = new LectureBuilder();
        LectureBooking testCase;

        builder.setBookingType(BookingType.LECTUREBOOKING)
                .setBookingStatus(BookingStatus.STATUS_DONE)
                .setCustomer("contactperson", "1231341", "simon@hotmail.dk",
                        "SchoolName", 9000, "Aalborg", "Yes", "40404040",
                        535435643)
                .setCreationDate(LocalDate.now())
                .setDate(LocalDateTime.now())
                .setParticipants(58)
                .setCustomerComment("hallo")
                .setComment("haallo2");
        testCase = builder.build();


    }
}
