package PostToCalendars;

import Bookings.ArrangementBooking;
import Bookings.LectureBooking;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class PostToGoogle {

    private ArrangementBooking inputArrangementBooking;
    private LectureBooking inputLectureBooking;

    public PostToGoogle(ArrangementBooking calendarArrangementBooking) {
        this.inputArrangementBooking = calendarArrangementBooking;
    }

    private static final String APPLICATION_NAME = "Aalborg Zoo Semester Project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "storedTokens";

    /* If you change scope, delete all tokens */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/PostToCalendars/client_secret_541371792575-89r2prpe9k5k35mue48s47eaqa7i8jrt.apps.googleusercontent.com.json";

    /* Calendar id for the google calendar and the user id for posting on the calendar
    *  Only approved users can post to the calendar - must be added manually*/
    private static final String CALENDAR_ID = "s8phc5eq9eohle7ps9c4ctsa04@group.calendar.google.com";
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

        /* logged in as christoffer hansen (has the authorization to post on the calendar) */
        return new AuthorizationCodeInstalledApp(flow, lReceiver).authorize(USER_ID);
    }

    public void postNewBIRTHDAYToCalendar() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        Event birthday_event = new Event()
                .setSummary("Fødselsdagsbarn: " + inputArrangementBooking.getBirthdayChildName())
                .setDescription(" Status: " + inputArrangementBooking.getBookingStatus().toString() +
                        "\n Fødselsdagsalder: " + String.valueOf(inputArrangementBooking.getBirthdayChildAge()) +
                        "\n Antal deltagere: " + String.valueOf(inputArrangementBooking.getParticipants()) +
                        "\n Guide: " + inputArrangementBooking.getGuide() +
                        "\n Bestilling af mad: " + inputArrangementBooking.getMenuChosen().toString() +
                        "\n\n Kommentar: " + inputArrangementBooking.getComment() +
                        "\n\n Kontaktperson: " + inputArrangementBooking.getCustomer().getContactPerson() +
                        "\n Telefon: " + inputArrangementBooking.getCustomer().getPhoneNumber() +
                        "\n E-mail: " + inputArrangementBooking.getCustomer().getEmail())
                .setTransparency("transparent");
        /*DateTime startOfEvent = new DateTime(inputArrangementBooking.getDateTime().getYear() + "-" +
                                                    inputArrangementBooking.getDateTime().getMonth() + "-" +
                                                    inputArrangementBooking.getDateTime().getDayOfMonth() + "T" +
                                                    inputArrangementBooking.getDateTime().getHour() + ":" +
                                                    inputArrangementBooking.getDateTime().getMinute() + ":00"); */
        DateTime startOfEvent = new DateTime("2018-11-01T15:30:00");
        EventDateTime begin = new EventDateTime()
                .setDateTime(startOfEvent)
                .setTimeZone("Europe/Copenhagen");
        birthday_event.setStart(begin);

        DateTime endDateTime = new DateTime("2018-11-01T16:30:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Copenhagen");
        birthday_event.setEnd(end);

        service.events().insert(CALENDAR_ID, birthday_event).execute();
    }

    public void postNewLECTUREToCalendar() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        Event lecture_event = new Event()
                .setSummary("Undervisning i: ")
                .setDescription("Lokale: ");
        DateTime startOfEvent = new DateTime("2018-10-29T10:00:00");
        EventDateTime begin = new EventDateTime()
                .setDateTime(startOfEvent)
                .setTimeZone("Europe/Copenhagen");
        lecture_event.setStart(begin);

        DateTime endOfEvent = new DateTime("2018-10-29T19:00:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endOfEvent)
                .setTimeZone("Europe/Copenhagen");
        lecture_event.setEnd(end);

        service.events().insert(CALENDAR_ID, lecture_event).execute();
    }
}