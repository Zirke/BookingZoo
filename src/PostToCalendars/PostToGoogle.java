package PostToCalendars;

import Bookings.ArrangementBooking;
import Bookings.BookingDataAccessor;
import Bookings.LectureBooking;
import Customers.LectureBookingCustomer;
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
import enums.BookingStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PostToGoogle {
    private final BookingDataAccessor bda = new BookingDataAccessor(
            "org.postgresql.Driver",
            "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
            "jyjczxth",
            "nw51BNKhctporjIFT5Qhhm72jwGVJK95"
    );

    private String tempMonth = "0";
    private String tempDay = "0";
    private String tempHour = "0";
    private String tempMinute = "0";
    private String tempComment = "Ingen kommentar";
    private String tempCustomerComment = "Ingen kommentar";
    private String idAddition = "aaaaaa"; // ID must be at least 5 characters

    private ArrangementBooking inputArrangementBooking;
    private LectureBooking inputLectureBooking;

    public PostToGoogle(ArrangementBooking calendarArrangementBooking) throws ClassNotFoundException, SQLException{
        this.inputArrangementBooking = calendarArrangementBooking;
    }

    public PostToGoogle(LectureBooking inputLectureBooking)throws ClassNotFoundException, SQLException {
        this.inputLectureBooking = inputLectureBooking;
    }

    private static final String APPLICATION_NAME = "Aalborg Zoo Semester Project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "storedTokens";

    /* If you change scope, delete all tokens */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/PostToCalendars/client_secret_1087141990564-5fvbiisgl771m51nij44vjpngfm0j0vt.apps.googleusercontent.com.json";

    /* Calendar id for the google calendar and the user id for posting on the calendar
    *  Only approved users can post to the calendar - must be added manually*/
    private static final String CALENDAR_ID = "aalborgzoo305@gmail.com";
    private static final String USER_ID = "541371792575-b6ten96k8mvqddhruugj7gfdhv7e0l9s.apps.googleusercontent.com";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
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
        return new AuthorizationCodeInstalledApp(flow, lReceiver).authorize(USER_ID);
    }

    public void postNewArrangementToCalendar() throws SQLException, IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        //checks if either of the comments are empty.
        tempCustomerComment = commentArrangementCustomerChecker();
        tempComment = commentArrangementChecker();

        Event arrangement_event = new Event()
                .setSummary("Fødselsdagsbarn: " + inputArrangementBooking.getBirthdayChildName())
                .setDescription(descriptionBuilderArrangement(tempCustomerComment, tempComment))
                .setTransparency("transparent")
                .setColorId("5") // Yellow
                .setSequence(1)
                .setId(idArrangementChecker());

        //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
        tempMonth = monthsLessThanTenArr();
        tempDay = daysLessThanTenArr();
        tempHour = hoursLessThanTenArr();
        tempMinute = minutesLessThanTenArr();

        // initiate the start hour of the event
        DateTime startOfEvent = new DateTime(timeStringBuilderArrangement(tempMonth,tempDay,tempHour,tempMinute));
        EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
        arrangement_event.setStart(begin);

        // when the event ends
        DateTime endDateTime = new DateTime(timeStringBuilderArrCalculator(tempMonth,tempDay,tempHour,tempMinute));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        arrangement_event.setEnd(end);

        service.events().insert(CALENDAR_ID, arrangement_event).execute();
    }

    public void postNewLectureToCalendar() throws IOException, GeneralSecurityException, SQLException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        LectureBookingCustomer temp = (LectureBookingCustomer) inputLectureBooking.getCustomer();

        tempCustomerComment = commentLectureCustomerChecker();
        tempComment = commentLectureChecker();

        Event lecture_event = new Event()
                .setSummary("Skoletjeneste: " + temp.getSchoolName())
                .setDescription(descriptionBuilderLecture(tempCustomerComment,tempComment))
                .setTransparency("transparent")
                .setLocation(String.valueOf(inputLectureBooking.getLectureRoom()))
                .setColorId("7") // Turquoise
                .setId(idLectureChecker());

        //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
        tempMonth = monthsLessThanTenLec();
        tempDay = daysLessThanTenLec();
        tempHour = hoursLessThanTenLec();
        tempMinute = minutesLessThanTenLec();

        DateTime startOfEvent = new DateTime(timeStringBuilderLecture(tempMonth,tempDay,tempHour,tempMinute));
        EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
        lecture_event.setStart(begin);

        DateTime endDateTime = new DateTime(timeStringBuilderLecCalculator(tempMonth,tempDay,tempHour,tempMinute));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        lecture_event.setEnd(end);

        service.events().insert(CALENDAR_ID, lecture_event).execute();
    }
    public void updateArrangementInCalendar() throws IOException, GeneralSecurityException{
        String idModifier = idAddition + inputArrangementBooking.getId();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        Event updatedArrangementEvent = service.events().get(CALENDAR_ID, idModifier).execute();

        tempCustomerComment = commentArrangementCustomerChecker();
        tempComment = commentArrangementChecker();

        updatedArrangementEvent.setSummary("Fødselsdagsbarn: " + inputArrangementBooking.getBirthdayChildName())
                               .setDescription(descriptionBuilderArrangement(tempCustomerComment, tempComment))
                               .setSequence(2);

        //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
        tempMonth = monthsLessThanTenArr();
        tempDay = daysLessThanTenArr();
        tempHour = hoursLessThanTenArr();
        tempMinute = minutesLessThanTenArr();

        DateTime startOfEvent = new DateTime(timeStringBuilderArrangement(tempMonth,tempDay,tempHour,tempMinute));
        EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
        updatedArrangementEvent.setStart(begin);

        DateTime endDateTime = new DateTime(timeStringBuilderArrCalculator(tempMonth,tempDay,tempHour,tempMinute));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        updatedArrangementEvent.setEnd(end);

        service.events().update(CALENDAR_ID, (idModifier), updatedArrangementEvent).execute();

        if(inputArrangementBooking.getBookingStatus() == BookingStatus.STATUS_DELETED){
            deleteArrangementInCalendar();
        }

    }
    public void updateLectureInCalendar() throws IOException, GeneralSecurityException{
        String idModifier = idAddition + inputLectureBooking.getId();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        LectureBookingCustomer temp = (LectureBookingCustomer) inputLectureBooking.getCustomer();

        Event updatedLectureEvent = service.events().get(CALENDAR_ID, idModifier).execute();

        tempCustomerComment = commentLectureCustomerChecker();
        tempComment = commentLectureChecker();

        updatedLectureEvent.setSummary("Skoletjeneste: " + temp.getSchoolName())
                           .setDescription(descriptionBuilderLecture(tempCustomerComment, tempComment))
                           .setSequence(2);

        //these statement checks whether some information is below 10, if it is "0" will be added infront of the integer
        tempMonth = monthsLessThanTenLec();
        tempDay = daysLessThanTenLec();
        tempHour = hoursLessThanTenLec();
        tempMinute = minutesLessThanTenLec();

        DateTime startOfEvent = new DateTime(timeStringBuilderLecture(tempMonth,tempDay,tempHour,tempMinute));
        EventDateTime begin = new EventDateTime().setDateTime(startOfEvent);
        updatedLectureEvent.setStart(begin);

        DateTime endDateTime = new DateTime(timeStringBuilderLecCalculator(tempMonth,tempDay,tempHour,tempMinute));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        updatedLectureEvent.setEnd(end);

        service.events().update(CALENDAR_ID, idModifier, updatedLectureEvent).execute();

        if(inputLectureBooking.getBookingStatus() == BookingStatus.STATUS_DELETED){
            deleteLectureInCalendar();
        }
    }
    private String commentArrangementChecker(){
        if(!(inputArrangementBooking.getComment().isEmpty())){
            tempComment = inputArrangementBooking.getComment();
        }
        return tempComment;
    }
    private String commentArrangementCustomerChecker(){
        if (!(inputArrangementBooking.getCustomerComment().isEmpty())){
            tempCustomerComment = inputArrangementBooking.getCustomerComment();
        }
        return  tempCustomerComment;
    }
    private String commentLectureChecker(){
        if(!(inputLectureBooking.getComment().isEmpty())){
            tempComment = inputLectureBooking.getComment();
        }
        return tempComment;
    }
    private String commentLectureCustomerChecker(){
        if (!(inputLectureBooking.getCustomerComment().isEmpty())){
            tempCustomerComment = inputLectureBooking.getCustomerComment();
        }
        return  tempCustomerComment;
    }
    private String idArrangementChecker() throws SQLException{
        String idModifier;
        if(inputArrangementBooking.getId() == 0){
            idModifier = idAddition + bda.getLastID();
        }
        else {
            idModifier = idAddition + inputArrangementBooking.getId();
        }
        return idModifier;
    }
    private String idLectureChecker() throws SQLException{
        String idModifier;
        if(inputLectureBooking.getId() == 0){
            idModifier = idAddition + bda.getLastID();
        }
        else {
            idModifier = idAddition + inputLectureBooking.getId();
        }
        return idModifier;
    }
    private String monthsLessThanTenArr(){
        if(inputArrangementBooking.getDateTime().getMonthValue() < 10) {
            tempMonth += String.valueOf(inputArrangementBooking.getDateTime().getMonthValue());
        }
        else{
            tempMonth = String.valueOf(inputArrangementBooking.getDateTime().getMonthValue());
        }
        return tempMonth;
    }
    private String daysLessThanTenArr(){
        if(inputArrangementBooking.getDateTime().getDayOfMonth() < 10) {
            tempDay += String.valueOf(inputArrangementBooking.getDateTime().getDayOfMonth());
        }
        else{
            tempDay = String.valueOf(inputArrangementBooking.getDateTime().getDayOfMonth());
        }
        return tempDay;
    }
    private String hoursLessThanTenArr(){
        if(inputArrangementBooking.getDateTime().getHour() < 10) {
            tempHour += String.valueOf(inputArrangementBooking.getDateTime().getHour());
        }
        else{
            tempHour = String.valueOf(inputArrangementBooking.getDateTime().getHour());
        }
        return tempHour;
    }
    private String minutesLessThanTenArr(){
        if(inputArrangementBooking.getDateTime().getMinute() < 10) {
            tempMinute += String.valueOf(inputArrangementBooking.getDateTime().getMinute());
        }
        else{
            tempMinute = String.valueOf(inputArrangementBooking.getDateTime().getMinute());
        }
        return tempMinute;
    }
    private String monthsLessThanTenLec(){
        if(inputLectureBooking.getDateTime().getMonthValue() < 10) {
            tempMonth += String.valueOf(inputLectureBooking.getDateTime().getMonthValue());
        }
        else{
            tempMonth = String.valueOf(inputLectureBooking.getDateTime().getMonthValue());
        }
        return tempMonth;
    }
    private String daysLessThanTenLec(){
        if(inputLectureBooking.getDateTime().getDayOfMonth() < 10) {
            tempDay += String.valueOf(inputLectureBooking.getDateTime().getDayOfMonth());
        }
        else{
            tempDay = String.valueOf(inputLectureBooking.getDateTime().getDayOfMonth());
        }
        return tempDay;
    }
    private String hoursLessThanTenLec(){
        if(inputLectureBooking.getDateTime().getHour() < 10) {
            tempHour += String.valueOf(inputLectureBooking.getDateTime().getHour());
        }
        else{
            tempHour = String.valueOf(inputLectureBooking.getDateTime().getHour());
        }
        return tempHour;
    }
    private String minutesLessThanTenLec(){
        if(inputLectureBooking.getDateTime().getMinute() < 10) {
            tempMinute += String.valueOf(inputLectureBooking.getDateTime().getMinute());
        }
        else{
            tempMinute = String.valueOf(inputLectureBooking.getDateTime().getMinute());
        }
        return tempMinute;
    }
    private String timeStringBuilderArrangement(String months, String days, String hours, String minutes){
        return String.valueOf(inputArrangementBooking.getDateTime().getYear()) + "-" + months + "-" +
                days + "T" + hours + ":" + minutes + ":00+01:00";
    }
    private String timeStringBuilderArrCalculator(String months, String days, String hours, String minutes){
        return String.valueOf(inputArrangementBooking.getDateTime().getYear()) + "-" + months + "-" +
                days + "T" + (String.valueOf(Integer.valueOf(hours)+2)) + ":" + minutes + ":00+01:00";

    }
    private String timeStringBuilderLecture(String months, String days, String hours, String minutes){
        return String.valueOf(inputLectureBooking.getDateTime().getYear()) + "-" + months + "-" +
                days + "T" + hours + ":" + minutes + ":00+01:00";
    }
    private String timeStringBuilderLecCalculator(String months, String days, String hours, String minutes){
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
                "\n Klassetrin: " + String.valueOf(inputLectureBooking.getGrade()) +
                "\n\n Kunde kommentar: " + customercomment +
                "\n\n Egen kommentar: " + comment +
                "\n\n Kontaktperson: " + inputLectureBooking.getCustomer().getContactPerson() +
                "\n Telefon: " + inputLectureBooking.getCustomer().getPhoneNumber() +
                "\n E-mail: " + inputLectureBooking.getCustomer().getEmail());
    }
    public void deleteArrangementInCalendar() throws IOException, GeneralSecurityException{
        String idModifier = idAddition + inputArrangementBooking.getId();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Deletes an event
        service.events().delete(CALENDAR_ID, idModifier).execute();
    }
    public void deleteLectureInCalendar() throws IOException, GeneralSecurityException{
        String idModifier = idAddition + inputLectureBooking.getId();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Deletes an event
        service.events().delete(CALENDAR_ID, idModifier).execute();
    }
}