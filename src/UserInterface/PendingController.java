package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.Booking;
import Bookings.LectureBooking;
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
    private Button activeBookingsButton;
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
    public void initialize() {
        customerCommentLabel.setVisible(false);
        customerCommentArea.setVisible(false);
        acceptBookingButton.setVisible(false);
        cancelBookingButton.setVisible(false);

        LectureBooking booking1 = new LectureBooking(Booking.bookingType.Skoletjeneste, "12/10/2019",
                "12:45", "Jens Jensen", "30406010", "mail@mail.com",
                "Kommentaren her er lol", "5", "5", "5", "5",
                "Dyr Derhjemme", "Aalborg Skole", "9000", "Aalborg",
                "30124531", "324324", "1561561561");

        ArrangementBooking booking2 = new ArrangementBooking(Booking.bookingType.Boernefoedselsdag, "28/12/2018",
                "10:00", "Gitte Hansen", "97744210", "mail@mail.com",
                "Min datter har nøddeallergi", "17", "Laura Hansen",
                "9", "Nej", ArrangementBooking.choiceOfMenu.MENU_ONE);

        LectureBooking booking3 = new LectureBooking(Booking.bookingType.Skoletjeneste, "12/10/2019",
                "12:45", "Peter Petersen", "30406010", "mail@mail.com",
                "Kommentaren her er lol", "5", "5", "5", "5",
                "Dyr Derhjemme", "Aalborg Skole", "9000", "Aalborg",
                "30124531", "324324", "1561561561");

        ArrayList<Booking> listOfBookings = new ArrayList<>();
        listOfBookings.add(booking1);
        listOfBookings.add(booking2);
        listOfBookings.add(booking3);

        loadBookingsToListView(listOfBookings);

        /* Search field controlsfx */
        ArrayList<String> listOfContactPersonNames = new ArrayList<>();
        for (Booking temp : listOfBookings) {
            listOfContactPersonNames.add(temp.getContactPerson());
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
        //acceptBookingButton.setOnMouseClicked(e -> acceptSelectedBooking());
    }

    Connection connect() throws ClassNotFoundException, SQLException {

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
            if (temp.getContactPerson().equals(enteredBooking)) {
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

        bookingTypeLabel.setText(selectedLectureBooking.getType().toString());
        bookingStatusLabel.setText(selectedLectureBooking.getStatus().toString());
        dateLabel.setText("Dato: " + selectedLectureBooking.getDate());
        timeLabel.setText("Tidspunkt: " + selectedLectureBooking.getTime());
        pupilNoLabel.setText("Antal elever " + selectedLectureBooking.getNoOfPupils());
        teamNoLabel.setText("Antal hold: " + selectedLectureBooking.getNoOfTeams());
        teacherNoLabel.setText("Antal lærere: " + selectedLectureBooking.getNoOfTeachers());
        gradeLabel.setText("Klassetrin: " + selectedLectureBooking.getGrade());
        topicChoiceLabel.setText("Valg af emne: " + selectedLectureBooking.getChoiceOfTopic());
        communeLabel.setText("Aalborg Kommune (Ja/Nej): " + selectedLectureBooking.getCommune());
        schoolNameLabel.setText("Skolens navn: " + selectedLectureBooking.getSchoolName());
        schoolPhoneNumberLabel.setText("Skolens telefonnummer: " + selectedLectureBooking.getSchoolPhoneNumber());
        zipcodeLabel.setText("Postnummer: " + selectedLectureBooking.getZipCode());
        cityLabel.setText("By: " + selectedLectureBooking.getCity());
        contactPersonLabel.setText("Kontaktperson: " + selectedLectureBooking.getContactPerson());
        phoneNumberLabel.setText("Telefonnummer: " + selectedLectureBooking.getPhoneNumber());
        emailLabel.setText("E-mail: " + selectedLectureBooking.getEmail());
        eanLabel.setText("EAN nummer: " + selectedLectureBooking.getEanNumber());
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

        bookingTypeLabel.setText(selectedArrangementBooking.getType().toString());
        bookingStatusLabel.setText(selectedArrangementBooking.getStatus().toString());
        dateLabel.setText("Dato: " + selectedArrangementBooking.getDate());
        timeLabel.setText("Tidspunkt: " + selectedArrangementBooking.getTime());
        pupilNoLabel.setText("Antal børn " + selectedArrangementBooking.getNoOfChildren());
        teamNoLabel.setText("Fødselsdagsbarnets navn: " + selectedArrangementBooking.getBirthdayChildName());
        teacherNoLabel.setText("Barnets alder: " + selectedArrangementBooking.getBirthdayChildAge());
        gradeLabel.setText("Tidligere deltager (Ja/Nej): " + selectedArrangementBooking.getFormerParticipant());
        topicChoiceLabel.setText("Valg af menu: " + selectedArrangementBooking.getMenuChosen());
        schoolNameLabel.setText("Kontaktperson: " + selectedArrangementBooking.getContactPerson());
        schoolPhoneNumberLabel.setText("Telefonnummer: " + selectedArrangementBooking.getPhoneNumber());
        zipcodeLabel.setText("E-mail: " + selectedArrangementBooking.getEmail());

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

