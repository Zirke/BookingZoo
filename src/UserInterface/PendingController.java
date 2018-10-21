package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.Booking;
import Bookings.BookingDataAccessor;
import Bookings.LectureBooking;
import Customers.LectureBookingCustomer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.textfield.TextFields;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class PendingController extends GeneralController {
    private BookingDataAccessor bda;

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
    private Button acceptBookingButton, cancelBookingButton, editBookingButton;

    public void initialize() throws SQLException, ClassNotFoundException {
        customerCommentLabel.setVisible(false);
        customerCommentArea.setVisible(false);
        acceptBookingButton.setVisible(false);
        cancelBookingButton.setVisible(false);
        editBookingButton.setVisible(false);

        bda = new BookingDataAccessor(
                "org.postgresql.Driver",
                "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
                "jyjczxth",
                "nw51BNKhctporjIFT5Qhhm72jwGVJK95"
        );

        ArrayList<Booking> listOfBookings = new ArrayList<>();

        listOfBookings.addAll(bda.fetchArrBooks());
        listOfBookings.addAll(bda.fetchLecBooks());

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
        arrangementBookingItem.setOnAction(e -> {
            /*
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ArrangementBookingCreation.fxml"));
            try {
                VBox box = (VBox) loader.load();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ArrangementBookingCreationController controller = loader.<ArrangementBookingCreationController>getController();
            controller.setBda(bda);
            */
            openNewPopUpWindow("ArrangementBookingCreation.fxml");
        });

        //Opens edit pop-up window corresponding to chosen Booking in ListView
        editBookingButton.setOnMouseClicked(e -> editSelectedBooking(listOfBookings));

        //Accepting the selected booking when pressing acceptBookingButton
        acceptBookingButton.setOnMouseClicked(e -> acceptSelectedBooking(listOfBookings));

        //Cancelling the selected booking when pressing cancelBookingButton
        cancelBookingButton.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Vil du slette bookingen?");
            alert.setContentText("Handlingen vil slette bookingen");

            Optional<ButtonType> alertChoice = alert.showAndWait();

            if (alertChoice.get() == ButtonType.OK) {
                deleteSelectedBooking();
                removeBookingFromListView();
            }
        });
    }

    //Takes an ArrayList of bookings to load into ListView of bookings
    private void loadBookingsToListView(ArrayList<Booking> listOfBookings) {
        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        for (Booking booking : listOfBookings) {
            bookings.addAll(booking);
        }
        bookingListView.setItems(bookings);
    }

    private void removeBookingFromListView() {
        Booking bookingToRemove = (Booking) bookingListView.getSelectionModel().getSelectedItem();
        bookingListView.getItems().remove(bookingToRemove);
    }

    private void showSelectedBookingInformation() {
        if (bookingListView.getSelectionModel().getSelectedItem() instanceof LectureBooking) {
            showLectureBookingInformation((LectureBooking) bookingListView.getSelectionModel().getSelectedItem());
        } else if (bookingListView.getSelectionModel().getSelectedItem() instanceof ArrangementBooking) {
            showArrangementBookingInformation((ArrangementBooking) bookingListView.getSelectionModel().getSelectedItem());
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

    private void editSelectedBooking(ArrayList<Booking> listOfBookings) {
        for (Booking temp : listOfBookings) {
            if (bookingListView.getSelectionModel().getSelectedItem().equals(temp)) {
                // openEditLectureBooking("EditLectureBooking.fxml", (LectureBooking) temp);
            }
        }
    }

    private void acceptSelectedBooking(ArrayList<Booking> listOfBookings) {
        if (bookingListView.getSelectionModel().getSelectedItem() != null) {
            //System.out.println(bookingListView.getSelectionModel().getSelectedItem().toString());

        }
    }

    private void deleteSelectedBooking() {
        try {
            bda.deleteBooking((Booking) bookingListView.getSelectionModel().getSelectedItem());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void changeBookingStatus() {

    }

    //Changes text on all labels corresponding to the chosen booking in ListView
    private void showLectureBookingInformation(LectureBooking selectedLectureBooking) {
        customerCommentLabel.setVisible(true);
        customerCommentArea.setVisible(true);
        acceptBookingButton.setVisible(true);
        cancelBookingButton.setVisible(true);
        editBookingButton.setVisible(true);

        LectureBookingCustomer temp = (LectureBookingCustomer) selectedLectureBooking.getCustomer();

        bookingTypeLabel.setText(selectedLectureBooking.getBookingType().toString());
        bookingStatusLabel.setText(selectedLectureBooking.getBookingStatus().toString());
        //TODO Represent creation date
        dateLabel.setText("Dato: " + selectedLectureBooking.getDate());
        timeLabel.setText("Tidspunkt: " + selectedLectureBooking.getTime());
        pupilNoLabel.setText("Antal elever: " + selectedLectureBooking.getParticipants());
        teamNoLabel.setText("Antal hold: " + selectedLectureBooking.getNoOfTeams());
        teacherNoLabel.setText("Antal lærere: " + selectedLectureBooking.getNoOfTeachers());
        gradeLabel.setText("Klassetrin: " + selectedLectureBooking.getGrade());
        topicChoiceLabel.setText("Valg af emne: " + selectedLectureBooking.getChoiceOfTopic());
        communeLabel.setText("Aalborg Kommune (Ja/Nej): " + temp.getCommune());
        schoolNameLabel.setText("Skolens navn: " + temp.getSchoolName());
        schoolPhoneNumberLabel.setText("Skolens telefonnummer: " + temp.getSchoolPhoneNumber());
        zipcodeLabel.setText("Postnummer: " + temp.getZipCode());
        cityLabel.setText("By: " + temp.getCity());
        contactPersonLabel.setText("Kontaktperson: " + selectedLectureBooking.getCustomer().getContactPerson());
        phoneNumberLabel.setText("Telefonnummer: " + selectedLectureBooking.getCustomer().getPhoneNumber());
        emailLabel.setText("E-mail: " + selectedLectureBooking.getCustomer().getEmail());
        eanLabel.setText("EAN nummer: " + temp.getEanNumber());
        customerCommentArea.setText(selectedLectureBooking.getComment());
        customerCommentArea.setEditable(false);

        communeLabel.setVisible(true);
        cityLabel.setVisible(true);
        contactPersonLabel.setVisible(true);
        phoneNumberLabel.setVisible(true);
        emailLabel.setVisible(true);
        eanLabel.setVisible(true);
    }

    private void showArrangementBookingInformation(ArrangementBooking selectedArrangementBooking) {
        customerCommentLabel.setVisible(true);
        customerCommentArea.setVisible(true);
        acceptBookingButton.setVisible(true);
        cancelBookingButton.setVisible(true);
        editBookingButton.setVisible(true);

        bookingTypeLabel.setText(selectedArrangementBooking.getBookingType().toString());
        bookingStatusLabel.setText(selectedArrangementBooking.getBookingStatus().toString());
        dateLabel.setText("Dato: " + selectedArrangementBooking.getDate());
        timeLabel.setText("Tidspunkt: " + selectedArrangementBooking.getTime());
        pupilNoLabel.setText("Antal børn: " + selectedArrangementBooking.getParticipants());
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
        customerCommentArea.setText(selectedArrangementBooking.getCustomerComment());
        customerCommentArea.setEditable(false);
    }
}

