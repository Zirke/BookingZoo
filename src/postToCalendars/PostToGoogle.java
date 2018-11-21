package postToCalendars;

import bookings.ArrangementBooking;
import bookings.Booking;
import bookings.BookingDataAccessor;
import bookings.LectureBooking;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import customers.LectureBookingCustomer;
import enums.BookingStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PostToGoogle {

    private static String idAddition = "aaaaaa"; // ID must be at least 5 characters

    private static final String APPLICATION_NAME = "Aalborg Zoo Semester Project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "storedTokens";

    /* If you change scope, delete all tokens */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/postToCalendars/client_secret_1087141990564-5fvbiisgl771m51nij44vjpngfm0j0vt.apps.googleusercontent.com.json";

    /* Calendar id for the google calendar and the user id for posting on the calendar
     *  Only approved users can post to the calendar - must be added manually*/
    private static final String CALENDAR_ID = "aalborgzoo305@gmail.com";
    private static final String USER_ID = "1087141990564-5fvbiisgl771m51nij44vjpngfm0j0vt.apps.googleusercontent.com";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) {
        Credential credential = null;
        try {
            // Load client secrets.
            InputStream in = PostToGoogle.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver lReceiver = new LocalServerReceiver.Builder().setPort(8888).build();

            /* logged in as your own google account (has the authorization to post on the calendar) */
            credential = new AuthorizationCodeInstalledApp(flow, lReceiver).authorize(USER_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return credential;
    }

    public static void postToCalendar(Booking inputBooking) throws IOException {
        Calendar calendar = connectToGoogleCalendar();

        if (inputBooking instanceof ArrangementBooking) {
            calendar.events().insert(CALENDAR_ID, newArrangementEvent(inputBooking)).execute();
        } else {
            calendar.events().insert(CALENDAR_ID, newLectureEvent(inputBooking)).execute();
        }
    }

    public static void updateCalendar(Booking inputBooking) throws IOException {
        String idModifier = idAddition + inputBooking.getId();
        Calendar calendar = connectToGoogleCalendar();

        Event updateEvent = calendar.events().get(CALENDAR_ID, idModifier).execute();
        if (inputBooking instanceof ArrangementBooking) {
            calendar.events().update(CALENDAR_ID, idModifier, updateArrangementInCalendar(updateEvent, inputBooking, idModifier, calendar)).execute();
        } else {
            calendar.events().update(CALENDAR_ID, idModifier, updateLectureInCalendar(updateEvent, inputBooking, idModifier, calendar)).execute();
        }
    }

    public static Event newArrangementEvent(Booking inputArrangementBooking) {
        Event arrangement_event = new Event()
                .setSummary("Fødselsdagsbarn: " + ((ArrangementBooking) inputArrangementBooking).getBirthdayChildName())
                .setDescription(descriptionBuilderArrangement(inputArrangementBooking, commentBookingCustomerChecker(inputArrangementBooking), commentBookingChecker(inputArrangementBooking)))
                .setTransparency("transparent")
                .setColorId("5") // Yellow
                .setSequence(1)
                .setId(idChecker(inputArrangementBooking));

        //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
        String tempMonth = monthsLessThanTen(inputArrangementBooking);
        String tempDay = daysLessThanTen(inputArrangementBooking);
        String tempHour = hoursLessThanTen(inputArrangementBooking);
        String tempMinute = minutesLessThanTen(inputArrangementBooking);

        // initiate the start hour of the event
        DateTime startOfEvent = new DateTime(beginTimeStringBuilder(inputArrangementBooking, tempMonth, tempDay, tempHour, tempMinute));
        EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
        arrangement_event.setStart(begin);

        // when the event ends
        DateTime endDateTime = new DateTime(endTimeStringBuilderArrCalculator(inputArrangementBooking, tempMonth, tempDay, tempHour, tempMinute));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        arrangement_event.setEnd(end);

        return arrangement_event;
    }

    public static Event newLectureEvent(Booking inputLectureBooking) {
        LectureBookingCustomer temp = (LectureBookingCustomer) inputLectureBooking.getCustomer();

        Event lecture_event = new Event()
                .setSummary("Skoletjeneste: " + temp.getSchoolName())
                .setDescription(descriptionBuilderLecture(inputLectureBooking, commentBookingCustomerChecker(inputLectureBooking), commentBookingChecker(inputLectureBooking)))
                .setTransparency("transparent")
                .setLocation(String.valueOf(((LectureBooking) inputLectureBooking).getLectureRoom()))
                .setColorId("7") // Turquoise
                .setSequence(1)
                .setId(idChecker(inputLectureBooking));

        //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
        String tempMonth = monthsLessThanTen(inputLectureBooking);
        String tempDay = daysLessThanTen(inputLectureBooking);
        String tempHour = hoursLessThanTen(inputLectureBooking);
        String tempMinute = minutesLessThanTen(inputLectureBooking);

        DateTime startOfEvent = new DateTime(beginTimeStringBuilder(inputLectureBooking, tempMonth, tempDay, tempHour, tempMinute));
        EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
        lecture_event.setStart(begin);

        DateTime endDateTime = new DateTime(endTimeStringBuilderLecCalculator(inputLectureBooking, tempMonth, tempDay, tempHour, tempMinute));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        lecture_event.setEnd(end);

        return lecture_event;
    }

    private static Event updateArrangementInCalendar(Event updatedArrangementEvent, Booking
            inputArrangementBooking, String idModifier, Calendar calendar) throws IOException {

        updatedArrangementEvent.setSummary("Fødselsdagsbarn: " + ((ArrangementBooking) inputArrangementBooking).getBirthdayChildName())
                .setDescription(descriptionBuilderArrangement(inputArrangementBooking, commentBookingCustomerChecker(inputArrangementBooking), commentBookingChecker(inputArrangementBooking)))
                .setSequence(calendar.events().get(CALENDAR_ID, idModifier).execute().getSequence());

        //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
        String tempMonth = monthsLessThanTen(inputArrangementBooking);
        String tempDay = daysLessThanTen(inputArrangementBooking);
        String tempHour = hoursLessThanTen(inputArrangementBooking);
        String tempMinute = minutesLessThanTen(inputArrangementBooking);

        DateTime startOfEvent = new DateTime(beginTimeStringBuilder(inputArrangementBooking, tempMonth, tempDay, tempHour, tempMinute));
        EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
        updatedArrangementEvent.setStart(begin);

        DateTime endDateTime = new DateTime(endTimeStringBuilderArrCalculator(inputArrangementBooking, tempMonth, tempDay, tempHour, tempMinute));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        updatedArrangementEvent.setEnd(end);


        if (inputArrangementBooking.getBookingStatus() == BookingStatus.STATUS_DELETED) {
            deleteBookingInCalendar(inputArrangementBooking);
        }
        return updatedArrangementEvent;
    }

    private static Event updateLectureInCalendar(Event updatedLectureEvent, Booking inputLectureBooking, String idModifier, Calendar calendar) throws IOException {
            LectureBookingCustomer temp = (LectureBookingCustomer) inputLectureBooking.getCustomer();

            updatedLectureEvent.setSummary("Skoletjeneste: " + temp.getSchoolName())
                    .setDescription(descriptionBuilderLecture(inputLectureBooking, commentBookingCustomerChecker(inputLectureBooking), commentBookingChecker(inputLectureBooking)))
                    .setSequence(calendar.events().get(CALENDAR_ID, idModifier).execute().getSequence());


        //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
        String tempMonth = monthsLessThanTen(inputLectureBooking);
        String tempDay = daysLessThanTen(inputLectureBooking);
        String tempHour = hoursLessThanTen(inputLectureBooking);
        String tempMinute = minutesLessThanTen(inputLectureBooking);

        DateTime startOfEvent = new DateTime(beginTimeStringBuilder(inputLectureBooking, tempMonth, tempDay, tempHour, tempMinute));
        EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
        updatedLectureEvent.setStart(begin);

        DateTime endDateTime = new DateTime(endTimeStringBuilderLecCalculator(inputLectureBooking, tempMonth, tempDay, tempHour, tempMinute));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        updatedLectureEvent.setEnd(end);

        if (inputLectureBooking.getBookingStatus() == BookingStatus.STATUS_DELETED) {
            deleteBookingInCalendar(inputLectureBooking);
        }
        return updatedLectureEvent;
    }

    public static String commentBookingChecker(Booking temp) {
        String tempComment = "Ingen kommentar";
        if ((temp.getComment() != null) && !(temp.getComment().isEmpty())) {
            tempComment = temp.getComment();
        }

        return tempComment;
    }

    public static String commentBookingCustomerChecker(Booking temp) {
        String tempComment = "Ingen kommentar";
        if ((temp.getCustomerComment() != null) && !(temp.getCustomerComment().isEmpty())) {
            tempComment = temp.getCustomerComment();
        }
        return tempComment;
    }

    public static String idChecker(Booking temp) {
        String idModifier = idAddition + temp.getId();

        try {
            final BookingDataAccessor bda = new BookingDataAccessor(
                    "org.postgresql.Driver",
                    "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
                    "jyjczxth",
                    "nw51BNKhctporjIFT5Qhhm72jwGVJK95"
            );

            if (temp.getId() == 0) {
                idModifier = idAddition + bda.getLastID();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return idModifier;
    }

    public static String monthsLessThanTen(Booking temp) {
        String tempMonth = "0";

        if (temp.getDateTime().getMonthValue() < 10) {
            tempMonth += String.valueOf(temp.getDateTime().getMonthValue());
        } else {
            tempMonth = String.valueOf(temp.getDateTime().getMonthValue());
        }
        return tempMonth;
    }

    public static String daysLessThanTen(Booking temp) {
        String tempDay = "0";

        if (temp.getDateTime().getDayOfMonth() < 10) {
            tempDay += String.valueOf(temp.getDateTime().getDayOfMonth());
        } else {
            tempDay = String.valueOf(temp.getDateTime().getDayOfMonth());
        }
        return tempDay;
    }

    public static String hoursLessThanTen(Booking temp) {
        String tempHour = "0";

        if (temp.getDateTime().getHour() < 10) {
            tempHour += String.valueOf(temp.getDateTime().getHour());
        } else {
            tempHour = String.valueOf(temp.getDateTime().getHour());
        }
        return tempHour;
    }

    public static String minutesLessThanTen(Booking temp) {
        String tempMinute = "0";

        if (temp.getDateTime().getMinute() < 10) {
            tempMinute += String.valueOf(temp.getDateTime().getMinute());
        } else {
            tempMinute = String.valueOf(temp.getDateTime().getMinute());
        }
        return tempMinute;
    }

    public static String beginTimeStringBuilder(Booking temp, String months, String days, String hours, String
            minutes) {
        return String.valueOf(temp.getDateTime().getYear()) + "-" + months + "-" + days + "T" + hours + ":" + minutes + ":00+01:00";
    }

    private static String endTimeStringBuilderArrCalculator(Booking inputArrangementBooking, String months, String
            days, String hours, String minutes) {
        return String.valueOf(inputArrangementBooking.getDateTime().getYear()) + "-" + months + "-" +
                days + "T" + (String.valueOf(Integer.valueOf(hours) + 2)) + ":" + minutes + ":00+01:00";

    }

    private static String endTimeStringBuilderLecCalculator(Booking inputLectureBooking, String months, String days, String hours, String minutes) {
        return String.valueOf(inputLectureBooking.getDateTime().getYear()) + "-" + months + "-" +
                days + "T" + (String.valueOf(Integer.valueOf(hours) + 1)) + ":" + minutes + ":00+01:00";

    }

    private static String descriptionBuilderArrangement(Booking inputArrangementBooking, String
            customercomment, String comment) {
        return ("\n Fødselsdagsalder: " + String.valueOf(((ArrangementBooking) inputArrangementBooking).getBirthdayChildAge()) +
                "\n Antal deltagere: " + String.valueOf(inputArrangementBooking.getParticipants()) +
                "\n Rundviser: " + ((ArrangementBooking) inputArrangementBooking).getGuide()) +
                "\n Bestilling af mad: " + ((ArrangementBooking) inputArrangementBooking).getMenuChosen().toString() +
                "\n Tidligere deltager: " + ((ArrangementBooking) inputArrangementBooking).getFormerParticipant() +
                "\n\n Kunde kommentar: " + customercomment +
                "\n\n Egen kommentar: " + comment +
                "\n\n Kontaktperson: " + inputArrangementBooking.getCustomer().getContactPerson() +
                "\n Telefon: " + inputArrangementBooking.getCustomer().getPhoneNumber() +
                "\n E-mail: " + inputArrangementBooking.getCustomer().getEmail();
    }

    private static String descriptionBuilderLecture(Booking inputLectureBooking, String customercomment, String
            comment) {
        return ("\n Valg af emne: " + String.valueOf(((LectureBooking) inputLectureBooking).getChoiceOfTopic().toString()) +
                "\n Underviser: " + ((LectureBooking) inputLectureBooking).getLecturer() +
                "\n Antal deltagere: " + String.valueOf(inputLectureBooking.getParticipants()) +
                "\n Antal hold: " + String.valueOf(((LectureBooking) inputLectureBooking).getNoOfTeams()) +
                "\n Antal lærer: " + String.valueOf(((LectureBooking) inputLectureBooking).getNoOfTeachers()) +
                "\n Klassetrin: " + ((LectureBooking) inputLectureBooking).getGrade() +
                "\n\n Kunde kommentar: " + customercomment +
                "\n\n Egen kommentar: " + comment +
                "\n\n Kontaktperson: " + inputLectureBooking.getCustomer().getContactPerson() +
                "\n Telefon: " + inputLectureBooking.getCustomer().getPhoneNumber() +
                "\n E-mail: " + inputLectureBooking.getCustomer().getEmail());
    }

    public static void deleteBookingInCalendar(Booking temp) throws IOException {

        String idModifier = idAddition + temp.getId();
        Calendar calendar = connectToGoogleCalendar();

        // Deletes an event
        calendar.events().delete(CALENDAR_ID, idModifier).execute();
    }

    private static Calendar connectToGoogleCalendar() {
        Calendar calendar = null;
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            calendar = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static Event getEvent(Booking temp) {

        String idModifier = idAddition + temp.getId();
        Calendar calendar = connectToGoogleCalendar();
        Event getEvent = null;
        try {
            getEvent = calendar.events().get(CALENDAR_ID, idModifier).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getEvent;
    }

    public static Boolean isIdValid(int id) {
        String idModifier = idAddition + id;
        Calendar calendar = connectToGoogleCalendar();
        try {
            Events events = calendar.events().list(CALENDAR_ID).execute();
            List<Event> items = events.getItems();
            for (Event event : items) {
                if (idModifier.equals(event.getId())) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}