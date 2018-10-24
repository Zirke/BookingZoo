package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.Booking;
import Bookings.BookingDataAccessor;
import Bookings.LectureBooking;
import Customers.LectureBookingCustomer;
import enums.BookingStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class MainScreenController extends GeneralController {
    private final BookingDataAccessor bda = new BookingDataAccessor(
            "org.postgresql.Driver",
            "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
            "jyjczxth",
            "nw51BNKhctporjIFT5Qhhm72jwGVJK95"
    );
    //private ArrayList<Booking> listOfBookings = new ArrayList<>();

    @FXML
    private Button refreshBookingsButton, overviewButton, pendingBookingsButton, activeBookingsButton,
            finishedBookingsButton, archivedBookingsButton;
    @FXML
    private MenuItem lectureBookingItem, arrangementBookingItem;
    @FXML
    private TextField searchField;
    @FXML
    private ListView bookingListView;

    //Nodes for booking information display area
    @FXML
    private Label customerCommentLabel, bookingTypeLabel, bookingStatusLabel, creationDateLabel, dateLabel,
            timeLabel, pupilNoLabel, teamNoLabel, teacherNoLabel, gradeLabel, topicChoiceLabel, schoolNameLabel,
            schoolPhoneNumberLabel, zipcodeLabel, cityLabel, communeLabel, phoneNumberLabel, contactPersonLabel,
            emailLabel, eanLabel;
    @FXML
    private TextArea customerCommentArea;
    @FXML
    private Button acceptBookingButton, cancelBookingButton, editBookingButton;

    public MainScreenController() throws SQLException, ClassNotFoundException {
    }

    public void initialize() throws SQLException {
        customerCommentLabel.setVisible(false);
        customerCommentArea.setVisible(false);
        acceptBookingButton.setVisible(false);
        cancelBookingButton.setVisible(false);
        editBookingButton.setVisible(false);

        //Takes all Booking objects from listOfBookings and load them into bookingListView
        loadBookingsToListView();

        /* Search field controlsfx */
        ArrayList<String> listOfContactPersonNames = new ArrayList<>();
        for (Booking temp : fetchBookingsFromDatabase()) {
            listOfContactPersonNames.add(temp.getCustomer().getContactPerson());
        }
        String[] options = listOfContactPersonNames.toArray(new String[0]);
        TextFields.bindAutoCompletion(searchField, options);

        /*
         *   Event handlers
         */

        //Shows searched for booking in ListView
        searchField.setOnKeyTyped(e -> {
            try {
                if (searchField.getText().isEmpty()) {
                    loadBookingsToListView();
                } else showSearchedForBookingsInListView(fetchBookingsFromDatabase());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        bookingListView.setOnMouseClicked(e -> showSelectedBookingInformation());

        //Opens pop-up window corresponding to chosen menu item (method used from GeneralController)
        lectureBookingItem.setOnAction(e -> openNewPopUpWindow("LectureBookingCreation.fxml"));
        arrangementBookingItem.setOnAction(e -> openNewPopUpWindow("ArrangementBookingCreation.fxml"));

        //Reloads the bookings from database into ListView
        refreshBookingsButton.setOnMouseClicked(e -> refreshBookingListView());

        //Opens edit pop-up window corresponding to chosen Booking in ListView
        editBookingButton.setOnMouseClicked(e -> {
            if (bookingListView.getSelectionModel().getSelectedItem() instanceof LectureBooking) {
                editSelectedLectureBooking((LectureBooking) (bookingListView.getSelectionModel().getSelectedItem()));
            } else if (bookingListView.getSelectionModel().getSelectedItem() instanceof ArrangementBooking) {
                editSelectedArrangementBooking((Bookings.ArrangementBooking) (bookingListView.getSelectionModel().getSelectedItem()));
            }
        });

        acceptBookingButton.setOnMouseClicked(e -> {
            try {
                acceptSelectedBooking(fetchBookingsFromDatabase());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

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

    private ArrayList<Booking> fetchBookingsFromDatabase() throws SQLException {
        ArrayList<Booking> listOfBookings = new ArrayList<>();
        listOfBookings.addAll(bda.fetchLecBooks());
        listOfBookings.addAll(bda.fetchArrBooks());

        return listOfBookings;
    }

    //Takes an ArrayList of bookings to load into ListView of bookings
    private void loadBookingsToListView() throws SQLException {
        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        for (Booking booking : fetchBookingsFromDatabase()) {
            bookings.addAll(booking);
        }
        bookingListView.setItems(bookings);
    }

    private void refreshBookingListView() {
        try {
            loadBookingsToListView();
            bda.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeBookingFromListView() {
        Booking bookingToRemove = (Booking) bookingListView.getSelectionModel().getSelectedItem();
        bookingListView.getItems().remove(bookingToRemove);
    }

    private void deleteSelectedBooking() {
        try {
            bda.deleteBooking((Booking) bookingListView.getSelectionModel().getSelectedItem());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Displays information of the clicked booking in ListView
    private void showSelectedBookingInformation() {
        if (bookingListView.getSelectionModel().getSelectedItem() instanceof LectureBooking) {
            showLectureBookingInformation((LectureBooking) bookingListView.getSelectionModel().getSelectedItem());
        } else if (bookingListView.getSelectionModel().getSelectedItem() instanceof ArrangementBooking) {
            showArrangementBookingInformation((ArrangementBooking) (bookingListView.getSelectionModel().getSelectedItem()));
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

    @FXML
    private void showChosenCategoryBookings(ActionEvent event) throws SQLException {
        //bookingListView.getSelectionModel().clearSelection();

        Button chosenCategoryBtn = (Button) event.getSource();
        String nameOfChosenBtn = chosenCategoryBtn.getText();

        ObservableList<Booking> categorisedBookings = FXCollections.observableArrayList();

        for (Booking temp : fetchBookingsFromDatabase()) {
            if (!nameOfChosenBtn.equals("Oversigt")) {
                BookingStatus chosenBookingStatus = BookingStatus.statusChosen(nameOfChosenBtn);
                if (temp.getBookingStatus().equals(chosenBookingStatus)) {
                    categorisedBookings.add(temp);
                }
            }
        }
        bookingListView.setItems(categorisedBookings);
    }

    //TODO
    //Accepting the selected booking when pressing acceptBookingButton
    private void acceptSelectedBooking(ArrayList<Booking> listOfBookings) {
        if (bookingListView.getSelectionModel().getSelectedItem() != null) {
            //System.out.println(bookingListView.getSelectionModel().getSelectedItem().toString());
        }
    }

    private void editSelectedLectureBooking(LectureBooking selectedLectureBooking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditLectureBooking.fxml"));
            Parent root = loader.load();

            EditLectureBookingController controller = loader.getController();
            controller.setSelectedLectureBooking(selectedLectureBooking);
            controller.initData();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editSelectedArrangementBooking(ArrangementBooking selectedArrangementBooking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditArrangementBooking.fxml"));
            Parent root = loader.load();

            EditArrangementBookingController controller = loader.getController();
            controller.setSelectedArrangementBooking(selectedArrangementBooking);
            controller.initData();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //Changes text on all labels corresponding to the chosen booking in ListView
    private void showLectureBookingInformation(LectureBooking selectedLectureBooking) {
        customerCommentLabel.setVisible(true);
        customerCommentArea.setVisible(true);
        acceptBookingButton.setVisible(true);
        cancelBookingButton.setVisible(true);
        editBookingButton.setVisible(true);

        communeLabel.setVisible(true);
        cityLabel.setVisible(true);
        contactPersonLabel.setVisible(true);
        phoneNumberLabel.setVisible(true);
        emailLabel.setVisible(true);
        eanLabel.setVisible(true);

        LectureBookingCustomer temp = (LectureBookingCustomer) selectedLectureBooking.getCustomer();
        bookingTypeLabel.setText(selectedLectureBooking.getBookingType().toString());
        bookingStatusLabel.setText(selectedLectureBooking.getBookingStatus().toString());
        creationDateLabel.setText("Oprettet: " + selectedLectureBooking.getCreationDate().toString());
        dateLabel.setText("Dato: " + selectedLectureBooking.getDateTime().toLocalDate().toString());
        timeLabel.setText("Tidspunkt: " + selectedLectureBooking.getDateTime().toLocalTime().toString());
        pupilNoLabel.setText("Antal elever: " + selectedLectureBooking.getParticipants());
        teamNoLabel.setText("Antal hold: " + selectedLectureBooking.getNoOfTeams());
        teacherNoLabel.setText("Antal lærere: " + selectedLectureBooking.getNoOfTeachers());
        gradeLabel.setText("Klassetrin: " + selectedLectureBooking.getGrade());
        topicChoiceLabel.setText("Valg af emne: " + selectedLectureBooking.getChoiceOfTopic().toString());
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
    }

    private void showArrangementBookingInformation(ArrangementBooking selectedArrangementBooking) {
        customerCommentLabel.setVisible(true);
        customerCommentArea.setVisible(true);
        acceptBookingButton.setVisible(true);
        cancelBookingButton.setVisible(true);
        editBookingButton.setVisible(true);

        communeLabel.setVisible(false);
        cityLabel.setVisible(false);
        contactPersonLabel.setVisible(false);
        phoneNumberLabel.setVisible(false);
        emailLabel.setVisible(false);
        eanLabel.setVisible(false);
        customerCommentArea.setEditable(false);

        bookingTypeLabel.setText(selectedArrangementBooking.getBookingType().toString());
        bookingStatusLabel.setText(selectedArrangementBooking.getBookingStatus().toString());
        dateLabel.setText("Dato: " + selectedArrangementBooking.getDateTime().toLocalDate().toString());
        creationDateLabel.setText("Oprettet: " + selectedArrangementBooking.getCreationDate().toString());
        timeLabel.setText("Tidspunkt: " + selectedArrangementBooking.getDateTime().toLocalTime().toString());
        pupilNoLabel.setText("Antal børn: " + selectedArrangementBooking.getParticipants());
        teamNoLabel.setText("Fødselsdagsbarnets navn: " + selectedArrangementBooking.getBirthdayChildName());
        teacherNoLabel.setText("Barnets alder: " + selectedArrangementBooking.getBirthdayChildAge());
        gradeLabel.setText("Tidligere deltager (Ja/Nej): " + selectedArrangementBooking.getFormerParticipant());
        topicChoiceLabel.setText("Valg af menu: " + selectedArrangementBooking.getMenuChosen());
        schoolNameLabel.setText("Kontaktperson: " + selectedArrangementBooking.getCustomer().getContactPerson());
        schoolPhoneNumberLabel.setText("Telefonnummer: " + selectedArrangementBooking.getCustomer().getPhoneNumber());
        zipcodeLabel.setText("E-mail: " + selectedArrangementBooking.getCustomer().getEmail());
        customerCommentArea.setText(selectedArrangementBooking.getCustomerComment());


    }
}