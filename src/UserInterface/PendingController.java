package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.Booking;
import Bookings.LectureBooking;
import Bookings.Lecturer;
import Customers.LectureBookingCustomer;
import enums.BookingStatus;
import enums.BookingType;
import enums.FacilityState;
import enums.LectureRoomType;
import facilities.LectureRoom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.textfield.TextFields;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class PendingController extends GeneralController {

    @FXML
    private Button activeBookingsButton, editBookingButton;
    @FXML
    private MenuItem lectureBookingItem, arrangementBookingItem;
    @FXML
    private TextField searchField;
    @FXML
    private ListView bookingListView;

    //Nodes for booking information display area
    @FXML
    private Label customerCommentLabel, bookingTypeLabel, bookingStatusLabel, dateLabel, timeLabel, pupilNoLabel, teamNoLabel, teacherNoLabel,
            gradeLabel, topicChoiceLabel, schoolNameLabel, schoolPhoneNumberLabel, zipcodeLabel, cityLabel, communeLabel,
            phoneNumberLabel, contactPersonLabel, emailLabel, eanLabel;
    @FXML
    private TextArea customerCommentArea;
    @FXML
    private Button acceptBookingButton, cancelBookingButton;

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {
        customerCommentLabel.setVisible(false);
        customerCommentArea.setVisible(false);
        acceptBookingButton.setVisible(false);
        cancelBookingButton.setVisible(false);

        //LectureBooking booking1 = new LectureBooking(1, BookingType.LectureBooking, BookingStatus.STATUS_PENDING, "12/12/2018", "12/12/2018", "10:00", "22", "Hej med dig", "Ingenting", , "97744210", "mail@mail.com");
        LectureBooking booking1 = new LectureBooking(223, BookingType.LECTUREBOOKING, BookingStatus.STATUS_ACTIVE,
                "12/10-2018", "12/10-2018", "10:00", "38", "kommento",
                "Bailando", new LectureRoom(FacilityState.occupied, LectureRoomType.biologicalType), new Lecturer(), "aber kan flyve",
                "25", "1", "1", "3", "Simon kærgaard",
                "123243131", "skarga@hotmail.dk", "Gl. Lindholm skole", "123123", "Aalborg",
                "Aalborg kommune", "2342312222", "123456");

        /*
        ArrangementBooking booking2 = new ArrangementBooking(Booking.bookingType.Boernefoedselsdag, "28/12/2018",
                "10:00", "Gitte Hansen", "97744210", "mail@mail.com",
                "Min datter har nøddeallergi", "17", "Laura Hansen",
                "9", "Nej", ArrangementBooking.choiceOfMenu.MENU_ONE, "");

        LectureBooking booking3 = new LectureBooking(Booking.bookingType.Skoletjeneste, "12/10/2019",
                "12:45", "Peter Petersen", "30406010", "mail@mail.com",
                "Kommentaren her er lol", "5", "5", "5", "5",
                "Dyr Derhjemme", "Aalborg Skole", "9000", "Aalborg",
                "30124531", "324324", "1561561561");
        */

        ArrayList<Booking> listOfBookings = new ArrayList<>();

        listOfBookings.add(booking1);

        //listOfBookings.addAll(ArrangementBooking.fetchArrBooks(connect()));
        //listOfBookings.addAll(LectureBooking.fetchSchoolBookings(connect()));

        loadBookingsToListView(listOfBookings);

        /* Search field controlsfx */
        ArrayList<String> listOfContactPersonNames = new ArrayList<>();
        for (Booking temp : listOfBookings) {
            listOfContactPersonNames.add(temp.getCustomer().getContactPerson());
        }
        String[] options = listOfContactPersonNames.toArray(new String[0]);
        TextFields.bindAutoCompletion(searchField, options);

        /* Event handlers */
        //Displays information of the clicked booking in ListView
        bookingListView.setOnMouseClicked(e -> showSelectedBookingInformation());

        //Shows searched for booking in ListView
        searchField.setOnKeyTyped(e -> {
            if (searchField.getText().isEmpty()) {
                loadBookingsToListView(listOfBookings);
            } else showSearchedForBookingsInListView(listOfBookings);
        });

        //Opens pop-up window corresponding to chosen menu item (method used from GeneralController)
        lectureBookingItem.setOnAction(e -> openNewPopUpWindow("LectureBookingCreation.fxml"));
        arrangementBookingItem.setOnAction(e -> openNewPopUpWindow("ArrangementBookingCreation.fxml"));

        //Accepting the selected booking when pressing acceptBookingButton
        acceptBookingButton.setOnMouseClicked(e -> acceptSelectedBooking(listOfBookings));
    }

    private Connection connect() throws ClassNotFoundException, SQLException {

        Class.forName("org.postgresql.Driver");

        String host = "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth";
        String user = "jyjczxth";
        String pass = "nw51BNKhctporjIFT5Qhhm72jwGVJK95";

        Connection con = DriverManager.getConnection(host, user, pass);

        return con;
    }

    //Takes an ArrayList of bookings to load into ListView of bookings
    private void loadBookingsToListView(ArrayList<Booking> listOfBookings) {
        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        for (Booking booking : listOfBookings) {
            bookings.addAll(booking);
        }
        bookingListView.setItems(bookings);
    }

    private void showSelectedBookingInformation() {
        if (bookingListView.getSelectionModel().getSelectedItem() instanceof LectureBooking) {
            lectureBookingInformation((LectureBooking) bookingListView.getSelectionModel().getSelectedItem());
        } else if (bookingListView.getSelectionModel().getSelectedItem() instanceof ArrangementBooking) {
            arrangementBookingInformation((ArrangementBooking) bookingListView.getSelectionModel().getSelectedItem());
        }
    }

    private void showSearchedForBookingsInListView(ArrayList<Booking> listOfBookings) {
        for (Booking temp : listOfBookings) {
            String enteredBooking = searchField.getText();
            if (temp.getCustomer().getContactPerson().equals(enteredBooking)) {
                bookingListView.getSelectionModel().clearSelection();
                ObservableList<Booking> bookings = FXCollections.observableArrayList();
                bookings.add(temp);
                bookingListView.setItems(bookings);
            }
        }
    }

    private void acceptSelectedBooking(ArrayList<Booking> listOfBookings) {

        if (bookingListView.getSelectionModel().getSelectedItem() != null) {
            //System.out.println(bookingListView.getSelectionModel().getSelectedItem().toString());

        }
    }

    //Changes text on all labels corresponding to the chosen booking in ListView
    private void lectureBookingInformation(LectureBooking selectedLectureBooking) {
        customerCommentLabel.setVisible(true);
        customerCommentArea.setVisible(true);
        acceptBookingButton.setVisible(true);
        cancelBookingButton.setVisible(true);

        bookingTypeLabel.setText(selectedLectureBooking.getBookingType().toString());
        bookingStatusLabel.setText(selectedLectureBooking.getBookingStatus().toString());
        //TODO Represent creation date
        dateLabel.setText("Dato: " + selectedLectureBooking.getDate());
        timeLabel.setText("Tidspunkt: " + selectedLectureBooking.getTime());
        pupilNoLabel.setText("Antal elever " + selectedLectureBooking.getNoOfPupils());
        teamNoLabel.setText("Antal hold: " + selectedLectureBooking.getNoOfTeams());
        teacherNoLabel.setText("Antal lærere: " + selectedLectureBooking.getNoOfTeachers());
        gradeLabel.setText("Klassetrin: " + selectedLectureBooking.getGrade());
        topicChoiceLabel.setText("Valg af emne: " + selectedLectureBooking.getChoiceOfTopic());

        LectureBookingCustomer temp = (LectureBookingCustomer) selectedLectureBooking.getCustomer();
        communeLabel.setText("Aalborg Kommune (Ja/Nej): " + temp.getCommune());
        //communeLabel.setText("Aalborg Kommune (Ja/Nej): " + (LectureBookingCustomer)(selectedLectureBooking.getCustomer().getCommune()));
        schoolNameLabel.setText("Skolens navn: " + temp.getSchoolName());
        schoolPhoneNumberLabel.setText("Skolens telefonnummer: " + temp.getSchoolPhoneNumber());
        zipcodeLabel.setText("Postnummer: " + temp.getZipCode());
        cityLabel.setText("By: " + temp.getCity());
        contactPersonLabel.setText("Kontaktperson: " + selectedLectureBooking.getCustomer().getContactPerson());
        phoneNumberLabel.setText("Telefonnummer: " + selectedLectureBooking.getCustomer().getPhoneNumber());
        emailLabel.setText("E-mail: " + selectedLectureBooking.getCustomer().getEmail());
        //eanLabel.setText("EAN nummer: " + selectedLectureBooking.getCustomer().getEanNumber());
        customerCommentArea.setText(selectedLectureBooking.getComment());
        customerCommentArea.setEditable(false);

        communeLabel.setVisible(true);
        cityLabel.setVisible(true);
        contactPersonLabel.setVisible(true);
        phoneNumberLabel.setVisible(true);
        emailLabel.setVisible(true);
        eanLabel.setVisible(true);
    }

    private void arrangementBookingInformation(ArrangementBooking selectedArrangementBooking) {
        customerCommentLabel.setVisible(true);
        customerCommentArea.setVisible(true);
        acceptBookingButton.setVisible(true);
        cancelBookingButton.setVisible(true);

        bookingTypeLabel.setText(selectedArrangementBooking.getBookingType().toString());
        bookingStatusLabel.setText(selectedArrangementBooking.getBookingStatus().toString());
        dateLabel.setText("Dato: " + selectedArrangementBooking.getDate());
        timeLabel.setText("Tidspunkt: " + selectedArrangementBooking.getTime());
        pupilNoLabel.setText("Antal børn " + selectedArrangementBooking.getNoOfChildren());
        teamNoLabel.setText("Fødselsdagsbarnets navn: " + selectedArrangementBooking.getBirthdayChildName());
        teacherNoLabel.setText("Barnets alder: " + selectedArrangementBooking.getBirthdayChildAge());
        gradeLabel.setText("Tidligere deltager (Ja/Nej): " + selectedArrangementBooking.getFormerParticipant());
        topicChoiceLabel.setText("Valg af menu: " + selectedArrangementBooking.getMenuChosen());
        schoolNameLabel.setText("Kontaktperson: " + selectedArrangementBooking.getCustomer().getContactPerson());
        schoolPhoneNumberLabel.setText("Telefonnummer: " + selectedArrangementBooking.getCustomer().getPhoneNumber());
        zipcodeLabel.setText("E-mail: " + selectedArrangementBooking.getCustomer().getEmail());

        communeLabel.setVisible(false);
        cityLabel.setVisible(false);
        contactPersonLabel.setVisible(false);
        phoneNumberLabel.setVisible(false);
        emailLabel.setVisible(false);
        eanLabel.setVisible(false);
        customerCommentArea.setText(selectedArrangementBooking.getComment());
        customerCommentArea.setEditable(false);
    }


}

