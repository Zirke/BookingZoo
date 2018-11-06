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

    private String tempMonth = "0";
    private String tempDay = "0";
    private String tempHour = "0";
    private String tempMinute = "0";
    private String idAddition = "aaaaaa"; // ID must be at least 5 characters

    private ArrangementBooking inputArrangementBooking;
    private LectureBooking inputLectureBooking;

    public PostToGoogle(ArrangementBooking calendarArrangementBooking) {
        this.inputArrangementBooking = calendarArrangementBooking;
    }

    public PostToGoogle(LectureBooking inputLectureBooking){
        this.inputLectureBooking = inputLectureBooking;
    }

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
            credential = new AuthorizationCodeInstalledApp(flow,lReceiver).authorize(USER_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return credential;
    }

    public void postNewArrangementToCalendar(){
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            Event arrangement_event = new Event()
                    .setSummary("Fødselsdagsbarn: " + inputArrangementBooking.getBirthdayChildName())
                    .setDescription(descriptionBuilderArrangement(commentBookingCustomerChecker(inputArrangementBooking), commentBookingChecker(inputArrangementBooking)))
                    .setTransparency("transparent")
                    .setColorId("5") // Yellow
                    .setSequence(1)
                    .setId(idChecker(inputArrangementBooking));

            //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
            tempMonth = monthsLessThanTen(inputArrangementBooking);
            tempDay = daysLessThanTen(inputArrangementBooking);
            tempHour = hoursLessThanTen(inputArrangementBooking);
            tempMinute = minutesLessThanTen(inputArrangementBooking);

            // initiate the start hour of the event
            DateTime startOfEvent = new DateTime(beginTimeStringBuilder(inputArrangementBooking,tempMonth,tempDay,tempHour,tempMinute));
            EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
            arrangement_event.setStart(begin);

            // when the event ends
            DateTime endDateTime = new DateTime(endTimeStringBuilderArrCalculator(tempMonth,tempDay,tempHour,tempMinute));
            EventDateTime end = new EventDateTime().setDateTime(endDateTime);
            arrangement_event.setEnd(end);

            service.events().insert(CALENDAR_ID, arrangement_event).execute();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public void postNewLectureToCalendar(){
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            LectureBookingCustomer temp = (LectureBookingCustomer) inputLectureBooking.getCustomer();

            Event lecture_event = new Event()
                    .setSummary("Skoletjeneste: " + temp.getSchoolName())
                    .setDescription(descriptionBuilderLecture(commentBookingCustomerChecker(inputLectureBooking), commentBookingChecker(inputLectureBooking)))
                    .setTransparency("transparent")
                    .setLocation(String.valueOf(inputLectureBooking.getLectureRoom()))
                    .setColorId("7") // Turquoise
                    .setSequence(1)
                    .setId(idChecker(inputLectureBooking));

            //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
            tempMonth = monthsLessThanTen(inputLectureBooking);
            tempDay = daysLessThanTen(inputLectureBooking);
            tempHour = hoursLessThanTen(inputLectureBooking);
            tempMinute = minutesLessThanTen(inputLectureBooking);

            DateTime startOfEvent = new DateTime(beginTimeStringBuilder(inputLectureBooking,tempMonth,tempDay,tempHour,tempMinute));
            EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
            lecture_event.setStart(begin);

            DateTime endDateTime = new DateTime(endTimeStringBuilderLecCalculator(tempMonth,tempDay,tempHour,tempMinute));
            EventDateTime end = new EventDateTime().setDateTime(endDateTime);
            lecture_event.setEnd(end);

            service.events().insert(CALENDAR_ID, lecture_event).execute();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    public void updateArrangementInCalendar(){
        try {
            String idModifier = idAddition + inputArrangementBooking.getId();
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            Event updatedArrangementEvent = service.events().get(CALENDAR_ID, idModifier).execute();

            updatedArrangementEvent.setSummary("Fødselsdagsbarn: " + inputArrangementBooking.getBirthdayChildName())
                                   .setDescription(descriptionBuilderArrangement(commentBookingCustomerChecker(inputArrangementBooking), commentBookingChecker(inputArrangementBooking)))
                                   .setSequence(service.events().get(CALENDAR_ID,idModifier).execute().getSequence());

            //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
            tempMonth = monthsLessThanTen(inputArrangementBooking);
            tempDay = daysLessThanTen(inputArrangementBooking);
            tempHour = hoursLessThanTen(inputArrangementBooking);
            tempMinute = minutesLessThanTen(inputArrangementBooking);

            DateTime startOfEvent = new DateTime(beginTimeStringBuilder(inputArrangementBooking,tempMonth,tempDay,tempHour,tempMinute));
            EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
            updatedArrangementEvent.setStart(begin);

            DateTime endDateTime = new DateTime(endTimeStringBuilderArrCalculator(tempMonth,tempDay,tempHour,tempMinute));
            EventDateTime end = new EventDateTime().setDateTime(endDateTime);
            updatedArrangementEvent.setEnd(end);

            service.events().update(CALENDAR_ID, (idModifier), updatedArrangementEvent).execute();

            if(inputArrangementBooking.getBookingStatus() == BookingStatus.STATUS_DELETED){
                deleteBookingInCalendar(inputArrangementBooking);
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    public void updateLectureInCalendar(){
        try {
            String idModifier = idAddition + inputLectureBooking.getId();

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            LectureBookingCustomer temp = (LectureBookingCustomer) inputLectureBooking.getCustomer();

            Event updatedLectureEvent = service.events().get(CALENDAR_ID, idModifier).execute();


            updatedLectureEvent.setSummary("Skoletjeneste: " + temp.getSchoolName())
                               .setDescription(descriptionBuilderLecture(commentBookingCustomerChecker(inputLectureBooking), commentBookingChecker(inputLectureBooking)))
                               .setSequence(service.events().get(CALENDAR_ID,idModifier).execute().getSequence());

            //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
            tempMonth = monthsLessThanTen(inputLectureBooking);
            tempDay = daysLessThanTen(inputLectureBooking);
            tempHour = hoursLessThanTen(inputLectureBooking);
            tempMinute = minutesLessThanTen(inputLectureBooking);

            DateTime startOfEvent = new DateTime(beginTimeStringBuilder(inputLectureBooking,tempMonth,tempDay,tempHour,tempMinute));
            EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
            updatedLectureEvent.setStart(begin);

            DateTime endDateTime = new DateTime(endTimeStringBuilderLecCalculator(tempMonth,tempDay,tempHour,tempMinute));
            EventDateTime end = new EventDateTime().setDateTime(endDateTime);
            updatedLectureEvent.setEnd(end);

            service.events().update(CALENDAR_ID, idModifier, updatedLectureEvent).execute();

            if(inputLectureBooking.getBookingStatus() == BookingStatus.STATUS_DELETED){
                deleteBookingInCalendar(inputLectureBooking);
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    public String commentBookingChecker(Booking temp){
        String tempComment = "Ingen kommentar";
        if((temp.getComment() != null) && !(temp.getComment().isEmpty())){
            tempComment = temp.getComment();
        }

        return tempComment;
    }
    public String commentBookingCustomerChecker(Booking temp){
        String tempComment = "Ingen kommentar";
        if((temp.getCustomerComment() != null) && !(temp.getCustomerComment().isEmpty())){
            tempComment = temp.getCustomerComment();
        }
        return tempComment;
    }

    public String idChecker(Booking temp){
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
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return idModifier;
    }
    public String monthsLessThanTen(Booking temp){
        if(temp.getDateTime().getMonthValue() < 10){
            tempMonth += String.valueOf(temp.getDateTime().getMonthValue());
        }
        else{
            tempMonth = String.valueOf(temp.getDateTime().getMonthValue());
        }
        return tempMonth;
    }
    public String daysLessThanTen(Booking temp){
        if(temp.getDateTime().getDayOfMonth() < 10){
            tempDay += String.valueOf(temp.getDateTime().getDayOfMonth());
        }
        else{
            tempDay = String.valueOf(temp.getDateTime().getDayOfMonth());
        }
        return tempDay;
    }
    public String hoursLessThanTen(Booking temp){
        if(temp.getDateTime().getHour() < 10){
            tempHour += String.valueOf(temp.getDateTime().getHour());
        }
        else{
            tempHour = String.valueOf(temp.getDateTime().getHour());
        }
        return tempHour;
    }
    public String minutesLessThanTen(Booking temp){
        if(temp.getDateTime().getMinute() < 10){
            tempMinute += String.valueOf(temp.getDateTime().getMinute());
        }
        else {
            tempMinute = String.valueOf(temp.getDateTime().getMinute());
        }
        return tempMinute;
    }
    public String beginTimeStringBuilder(Booking temp, String months, String days, String hours, String minutes){
        return String.valueOf(temp.getDateTime().getYear()) + "-" + months + "-" + days + "T" + hours + ":" + minutes + ":00+01:00";
    }
    private String endTimeStringBuilderArrCalculator(String months, String days, String hours, String minutes){
        return String.valueOf(inputArrangementBooking.getDateTime().getYear()) + "-" + months + "-" +
                days + "T" + (String.valueOf(Integer.valueOf(hours)+2)) + ":" + minutes + ":00+01:00";

    }
    private String endTimeStringBuilderLecCalculator(String months, String days, String hours, String minutes){
        return String.valueOf(inputLectureBooking.getDateTime().getYear()) + "-" + months + "-" +
                days + "T" + (String.valueOf(Integer.valueOf(hours)+1)) + ":" + minutes + ":00+01:00";

    }
    private String descriptionBuilderArrangement(String customercomment, String comment){
        return ("\n Fødselsdagsalder: " + String.valueOf(inputArrangementBooking.getBirthdayChildAge()) +
                "\n Antal deltagere: " + String.valueOf(inputArrangementBooking.getParticipants()) +
                "\n Rundviser: " + inputArrangementBooking.getGuide() +
                "\n Bestilling af mad: " + inputArrangementBooking.getMenuChosen().toString() +
                "\n Tidligere deltager: " + inputArrangementBooking.getFormerParticipant() +
                "\n\n Kunde kommentar: " + customercomment +
                "\n\n Egen kommentar: " + comment +
                "\n\n Kontaktperson: " + inputArrangementBooking.getCustomer().getContactPerson() +
                "\n Telefon: " + inputArrangementBooking.getCustomer().getPhoneNumber() +
                "\n E-mail: " + inputArrangementBooking.getCustomer().getEmail());
    }
    private String descriptionBuilderLecture(String customercomment, String comment){
        return ("\n Valg af emne: " + String.valueOf(inputLectureBooking.getChoiceOfTopic().toString()) +
                "\n Underviser: " + inputLectureBooking.getLecturer() +
                "\n Antal deltagere: " + String.valueOf(inputLectureBooking.getParticipants()) +
                "\n Antal hold: " + String.valueOf(inputLectureBooking.getNoOfTeams()) +
                "\n Antal lærer: " + String.valueOf(inputLectureBooking.getNoOfTeachers()) +
                "\n Klassetrin: " + inputLectureBooking.getGrade() +
                "\n\n Kunde kommentar: " + customercomment +
                "\n\n Egen kommentar: " + comment +
                "\n\n Kontaktperson: " + inputLectureBooking.getCustomer().getContactPerson() +
                "\n Telefon: " + inputLectureBooking.getCustomer().getPhoneNumber() +
                "\n E-mail: " + inputLectureBooking.getCustomer().getEmail());
    }
    public void deleteBookingInCalendar(Booking temp){
        try {
            String idModifier = idAddition + temp.getId();
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Deletes an event
            service.events().delete(CALENDAR_ID, idModifier).execute();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}