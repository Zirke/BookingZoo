package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.Booking;
import Bookings.BookingDataAccessor;
import Bookings.LectureBooking;
import Customers.LectureBookingCustomer;
import PostToCalendars.PostToGoogle;
import enums.BookingStatus;
import javafx.beans.property.ReadOnlyStringWrapper;
import enums.BookingType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
    private ArrayList<Booking> listOfBookings = new ArrayList<>();

    @FXML
    private ToggleButton overviewButton, pendingBookingsButton, activeBookingsButton,
            finishedBookingsButton, archivedBookingsButton, deletedBookingsButton;
    @FXML
    private Button refreshBookingsButton;
    @FXML
    private MenuItem lectureBookingItem, arrangementBookingItem;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Booking> bookingTableView;
    @FXML
    private TableColumn<Booking, String> bookingStatusColumn, bookingTypeColumn, bookingContactPersonColumn;
    @FXML
    private TableColumn<Booking, String> bookingDateColumn;

    //Nodes for booking information display area
    @FXML
    private Label commentLabel, customerCommentLabel, bookingTypeLabel, bookingStatusLabel, creationDateLabel, dateLabel,
            timeLabel, pupilNoLabel, teamNoLabel, teacherNoLabel, gradeLabel, topicChoiceLabel, schoolNameLabel,
            schoolPhoneNumberLabel, zipcodeLabel, cityLabel, communeLabel, phoneNumberLabel, contactPersonLabel,
            emailLabel, eanLabel, guide_lecturerLabel;
    @FXML
    private TextArea customerCommentTextArea, commentTextArea;
    @FXML
    private Button acceptBookingButton, cancelBookingButton, editBookingButton, deleteButton;

    public MainScreenController() throws SQLException, ClassNotFoundException {
    }

    public void initialize() throws SQLException {
        fetchBookingsFromDatabase();

        customerCommentLabel.setVisible(false);
        customerCommentTextArea.setVisible(false);
        commentLabel.setVisible(false);
        commentTextArea.setVisible(false);
        acceptBookingButton.setVisible(false);
        cancelBookingButton.setVisible(false);
        editBookingButton.setVisible(false);

        deleteButton.setOnMouseClicked(e -> {
            deleteSelectedBooking();
            removeBookingFromTableView();
        });

        /* Search field controlsfx */
        ArrayList<String> listOfContactPersonNames = new ArrayList<>();
        for (Booking temp : listOfBookings) {
            listOfContactPersonNames.add(temp.getCustomer().getContactPerson());
        }
        String[] options = listOfContactPersonNames.toArray(new String[0]);
        TextFields.bindAutoCompletion(searchField, options);

        /*
         *   Event handlers
         */

        //Shows searched for booking in TableView
        searchField.setOnAction(e -> {
            if (searchField.getText().isEmpty()) {
                loadBookingsToTableView();
            } else showSearchedForBookingsInTableView(listOfBookings);
        });

        //Takes all "Booking" objects and loads them into bookingsTableView and sets up the proper columns
        loadBookingsToTableView();

        bookingTableView.setOnMouseClicked(e -> showSelectedBookingInformation());

        //Opens pop-up window corresponding to chosen menu item (method used from GeneralController)
        lectureBookingItem.setOnAction(e -> openNewPopUpWindow("LectureBookingCreation.fxml"));
        arrangementBookingItem.setOnAction(e -> openNewPopUpWindow("ArrangementBookingCreation.fxml"));

        //Reloads the bookings from database into TableView
        refreshBookingsButton.setOnMouseClicked(e -> {
            try {
                refreshBookingTableView();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        //Opens edit pop-up window corresponding to chosen Booking in TableView
        editBookingButton.setOnMouseClicked(e -> {
            if (bookingTableView.getSelectionModel().getSelectedItem() instanceof LectureBooking) {
                editSelectedLectureBooking((LectureBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
            } else if (bookingTableView.getSelectionModel().getSelectedItem() instanceof ArrangementBooking) {
                editSelectedArrangementBooking((ArrangementBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
            }
        });

        //Changes the "BookingStatus" of the selected booking in TableView
        acceptBookingButton.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Vil du acceptere bookingen?");
            alert.setContentText("Bookingen flyttes til 'Aktive'");

            Optional<ButtonType> alertChoice = alert.showAndWait();

            if (alertChoice.get() == ButtonType.OK) {
                if((bookingTableView.getSelectionModel().getSelectedItem()).getBookingType() == (BookingType.ARRANGEMENTBOOKING)) {
                    PostToGoogle newConfirmedArrangementBooking = new PostToGoogle((ArrangementBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                    try {
                        newConfirmedArrangementBooking.postNewArrangementToCalendar();
                    } catch (IOException | GeneralSecurityException execp) {
                        execp.printStackTrace();}
                    }
                if((bookingTableView.getSelectionModel().getSelectedItem()).getBookingType() == (BookingType.LECTUREBOOKING)) {
                    PostToGoogle newConfirmedLectureBooking = new PostToGoogle((LectureBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                    try {
                        newConfirmedLectureBooking.postNewLectureToCalendar();
                    } catch (IOException | GeneralSecurityException execp) {
                        execp.printStackTrace();
                    }
                }
                acceptSelectedBooking();
                removeBookingFromTableView();
            }
        });

        //Cancelling the selected booking when pressing cancelBookingButton
        cancelBookingButton.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Vil du slette bookingen?");
            alert.setContentText("Handlingen vil slette bookingen");

            Optional<ButtonType> alertChoice = alert.showAndWait();

            if (alertChoice.get() == ButtonType.OK) {
                if(bookingTableView.getSelectionModel().getSelectedItem().getBookingType() == (BookingType.ARRANGEMENTBOOKING)){
                    PostToGoogle cancelArrangementBooking = new PostToGoogle((ArrangementBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                    try {
                      cancelArrangementBooking.deleteArrangementInCalendar();
                        bda.changeBookingStatus(bookingTableView.getSelectionModel().getSelectedItem(), BookingStatus.STATUS_DELETED);
                    } catch (SQLException | IOException | GeneralSecurityException e1) {
                        e1.printStackTrace();
                }
                removeBookingFromTableView();
            }}
        });
    }

    /*
     *   METHODS
     */

    private void fetchBookingsFromDatabase() throws SQLException {
        listOfBookings.addAll(bda.fetchLecBooks());
        listOfBookings.addAll(bda.fetchArrBooks());
    }

    //Takes an ArrayList of bookings to load into TableView of bookings
    private void loadBookingsToTableView() {
        //bookingStatusColumn.setSortType(TableColumn.SortType.DESCENDING);

        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
        bookingTypeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingType"));
        bookingContactPersonColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        bookingDateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDateTime().toLocalDate().toString()));
        bookingDateColumn.setSortType(TableColumn.SortType.ASCENDING);

        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        for (Booking booking : listOfBookings) {
            bookings.addAll(booking);
        }
        bookingTableView.setItems(bookings);
    }

    private void refreshBookingTableView() throws SQLException {
        listOfBookings.clear();
        fetchBookingsFromDatabase();
        loadBookingsToTableView();
    }

    private void removeBookingFromTableView() {
        Booking bookingToRemove = bookingTableView.getSelectionModel().getSelectedItem();
        bookingTableView.getItems().remove(bookingToRemove);
    }

    private void deleteSelectedBooking() {
        try {
            bda.deleteBooking(bookingTableView.getSelectionModel().getSelectedItem());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Displays information of the clicked booking in TableView
    private void showSelectedBookingInformation() {
        if (bookingTableView.getSelectionModel().getSelectedItem() instanceof LectureBooking) {
            showLectureBookingInformation((LectureBooking) bookingTableView.getSelectionModel().getSelectedItem());
        } else if (bookingTableView.getSelectionModel().getSelectedItem() instanceof ArrangementBooking) {
            showArrangementBookingInformation((ArrangementBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
        }
    }

    private void showSearchedForBookingsInTableView(ArrayList<Booking> listOfBookings) {
        for (Booking temp : listOfBookings) {
            String enteredBooking = searchField.getText();
            if (temp.getCustomer().getContactPerson().equals(enteredBooking)) {
                bookingTableView.getSelectionModel().clearSelection();
                ObservableList<Booking> bookings = FXCollections.observableArrayList();
                bookings.add(temp);
                bookingTableView.setItems(bookings);
            }
        }
    }

    @FXML
    private void showChosenCategoryBookings(ActionEvent event) throws SQLException {
        ToggleButton chosenCategoryBtn = (ToggleButton) event.getSource();
        String nameOfChosenBtn = chosenCategoryBtn.getText();

        ObservableList<Booking> categorisedBookings = FXCollections.observableArrayList();

        for (Booking temp : listOfBookings) {
            if (!nameOfChosenBtn.equals("Oversigt")) {
                BookingStatus chosenBookingStatus = BookingStatus.statusChosen(nameOfChosenBtn);
                if (temp.getBookingStatus().equals(chosenBookingStatus)) {
                    categorisedBookings.add(temp);
                }
            }
        }
        bookingTableView.setItems(categorisedBookings);
    }

    private void acceptSelectedBooking() {
        try {
            bda.changeBookingStatus(bookingTableView.getSelectionModel().getSelectedItem(), BookingStatus.STATUS_ACTIVE);
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void showPendingButtons(BookingStatus bookingStatus) {
        if (bookingStatus.equals(BookingStatus.STATUS_PENDING)) {
            acceptBookingButton.setVisible(true);
            cancelBookingButton.setVisible(true);
            editBookingButton.setVisible(true);
        } else {
            acceptBookingButton.setVisible(false);
            cancelBookingButton.setVisible(false);
            editBookingButton.setVisible(true);
        }
    }

    //Changes text on all labels corresponding to the chosen booking in ListView
    private void showLectureBookingInformation(LectureBooking selectedLectureBooking) {

        showPendingButtons(selectedLectureBooking.getBookingStatus());

        communeLabel.setVisible(true);
        cityLabel.setVisible(true);
        contactPersonLabel.setVisible(true);
        phoneNumberLabel.setVisible(true);
        emailLabel.setVisible(true);
        eanLabel.setVisible(true);
        guide_lecturerLabel.setVisible(true);
        customerCommentLabel.setVisible(true);
        customerCommentTextArea.setVisible(true);
        customerCommentTextArea.setEditable(false);
        commentLabel.setVisible(true);
        commentTextArea.setVisible(true);

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
        if (selectedLectureBooking.getLecturer() == null) {
            guide_lecturerLabel.setText("Underviser: Ingen underviser tilføjet");
        } else guide_lecturerLabel.setText("Underviser: " + selectedLectureBooking.getLecturer().toString());
        customerCommentTextArea.setText(selectedLectureBooking.getComment());
        commentTextArea.setText(selectedLectureBooking.getComment());
    }

    private void showArrangementBookingInformation(ArrangementBooking selectedArrangementBooking) {

        showPendingButtons(selectedArrangementBooking.getBookingStatus());

        cityLabel.setVisible(false);
        contactPersonLabel.setVisible(false);
        phoneNumberLabel.setVisible(false);
        emailLabel.setVisible(false);
        eanLabel.setVisible(false);
        guide_lecturerLabel.setVisible(false);
        customerCommentLabel.setVisible(true);
        customerCommentTextArea.setVisible(true);
        customerCommentTextArea.setEditable(false);
        commentLabel.setVisible(true);
        commentTextArea.setVisible(true);

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
        if (selectedArrangementBooking.getGuide() == null) {
            communeLabel.setText("Guide: Ingen guide tilføjet");
        } else communeLabel.setText("Guide: " + selectedArrangementBooking.getGuide());
        customerCommentTextArea.setText(selectedArrangementBooking.getCustomerComment());
        commentTextArea.setText(selectedArrangementBooking.getComment());
    }
}