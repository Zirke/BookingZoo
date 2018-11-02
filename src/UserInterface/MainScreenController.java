package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.Booking;
import Bookings.BookingDataAccessor;
import Bookings.LectureBooking;
import Customers.LectureBookingCustomer;
import PostToCalendars.PostToGoogle;
import enums.BookingStatus;
import enums.BookingType;
import exception.NoBookingsInDatabaseException;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class MainScreenController extends GeneralController {
    private final BookingDataAccessor bda = new BookingDataAccessor(
            "org.postgresql.Driver",
            "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
            "jyjczxth",
            "nw51BNKhctporjIFT5Qhhm72jwGVJK95"
    );
    private ArrayList<Booking> listOfAllBookings = new ArrayList<>();
    private ArrayList<Booking> listOfBookings = new ArrayList<>(); //Without archived and deleted
    private ArrayList<Booking> listOfPendingBookings = new ArrayList<>();
    private ArrayList<Booking> listOfActiveBookings = new ArrayList<>();
    private ArrayList<Booking> listOfFinishedBookings = new ArrayList<>();
    private ArrayList<Booking> listOfArchivedBookings = new ArrayList<>();
    private ArrayList<Booking> listOfDeletedBookings = new ArrayList<>();
    private ArrayList<Booking> listOfLectureBookings = new ArrayList<>();
    private ArrayList<Booking> listOfArrangementBookings = new ArrayList<>();
    private BookingType typeOfBooking;

    public void setTypeOfBooking(BookingType typeOfBooking) {
        this.typeOfBooking = typeOfBooking;
    }

    @FXML
    public Button notificationButton;
    public Label notificationLabel;
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
    @FXML
    private MenuItem allBookingsMenuItem, arrangementBookingsMenuItem, lectureBookingsMenuItem;

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
        for (Booking temp : listOfAllBookings) {
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
                loadBookingTypeIntoTableView();
            } else showSearchedForBookingsInTableView(listOfAllBookings);
        });

        //Takes all "Booking" objects and loads them into bookingsTableView and sets up the proper columns

        //loadBookingTypeIntoTableView();

        bookingTableView.setOnMouseClicked(e -> showSelectedBookingInformation(bookingTableView));

        //Opens pop-up window corresponding to chosen menu item (method used from GeneralController)
        lectureBookingItem.setOnAction(e -> createNewLectureBooking());
        arrangementBookingItem.setOnAction(e -> createNewArrangementBooking());

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
                if ((bookingTableView.getSelectionModel().getSelectedItem()).getBookingType() == (BookingType.ARRANGEMENTBOOKING)) {
                    try {
                        PostToGoogle newConfirmedArrangementBooking = new PostToGoogle((ArrangementBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                        try {
                            newConfirmedArrangementBooking.postNewArrangementToCalendar();
                        } catch (IOException | GeneralSecurityException | SQLException excep) {
                            excep.printStackTrace();
                        }
                    } catch (SQLException | ClassNotFoundException excep1) {
                        excep1.printStackTrace();
                    }
                    if ((bookingTableView.getSelectionModel().getSelectedItem()).getBookingType() == (BookingType.LECTUREBOOKING)) {
                        try {
                            PostToGoogle newConfirmedLectureBooking = new PostToGoogle((LectureBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                            try {
                                newConfirmedLectureBooking.postNewLectureToCalendar();
                            } catch (IOException | GeneralSecurityException excep) {
                                excep.printStackTrace();
                            }
                        } catch (ClassNotFoundException | SQLException excep1) {
                            excep1.printStackTrace();
                        }

                    }
                }
            }
            acceptSelectedBooking();
            removeBookingFromTableView();
        });

        //Cancelling the selected booking when pressing cancelBookingButton
        cancelBookingButton.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Vil du slette bookingen?");
            alert.setContentText("Handlingen vil slette bookingen");

            Optional<ButtonType> alertChoice = alert.showAndWait();

            if (alertChoice.get() == ButtonType.OK) {
                try {
                    bda.changeBookingStatus(bookingTableView.getSelectionModel().getSelectedItem(), BookingStatus.STATUS_DELETED);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                removeBookingFromTableView();
            }
        });

        //Notification button
        ArrayList<Booking> noficationBookings = getNotificationBookings(listOfAllBookings);
        notificationLabel.setText("(" + Integer.toString(noficationBookings.size()) + ")");
        notificationButton.setOnMouseClicked(e -> showUpcomingBookings(noficationBookings));
    }

    /*
     *   METHODS
     */

    public void initialiseBookingsOfChosenType() {
        loadBookingTypeIntoTableView();
        //Shows searched for booking in TableView
        searchField.setOnAction(e -> {
            if (searchField.getText().isEmpty()) {
                loadBookingTypeIntoTableView();
            } else showSearchedForBookingsInTableView(listOfAllBookings);
        });
    }

    private void fetchBookingsFromDatabase() throws SQLException {
        try {
            listOfLectureBookings.addAll(bda.fetchLecBooks());
            listOfAllBookings.addAll(listOfLectureBookings);
        } catch (NoBookingsInDatabaseException e) {
            System.out.println("No lecture bookings in database"); //Lav om til exception handling
        }
        try {
            listOfArrangementBookings.addAll(bda.fetchArrBooks());
            listOfAllBookings.addAll(listOfArrangementBookings);
        } catch (NoBookingsInDatabaseException e) {
            System.out.println("No arrangement bookings in database"); //Lav om til exception handling
        }


        for (Booking tempBooking : listOfAllBookings) {
            if (tempBooking.getBookingStatus().equals(BookingStatus.STATUS_PENDING)) {
                listOfPendingBookings.add(tempBooking);
            }
            if (tempBooking.getBookingStatus().equals(BookingStatus.STATUS_ACTIVE)) {
                listOfActiveBookings.add(tempBooking);
            }
            if (tempBooking.getBookingStatus().equals(BookingStatus.STATUS_DONE)) {
                listOfFinishedBookings.add(tempBooking);
            }
            if (tempBooking.getBookingStatus().equals(BookingStatus.STATUS_ARCHIVED)) {
                listOfArchivedBookings.add(tempBooking);
            }
            if (tempBooking.getBookingStatus().equals(BookingStatus.STATUS_DELETED)) {
                listOfDeletedBookings.add(tempBooking);
            }
            if (!tempBooking.getBookingStatus().equals(BookingStatus.STATUS_ARCHIVED) &&
                    (!tempBooking.getBookingStatus().equals(BookingStatus.STATUS_DELETED))) {
                listOfBookings.add(tempBooking);
            }
        }
    }

    //Takes an ArrayList of bookings to load into TableView of bookings
    private void loadBookingsToTableView(ArrayList<Booking> listOfBookings) {
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
        bookingTypeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingType"));
        bookingContactPersonColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        bookingDateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDateTime().toLocalDate().toString()));

        ObservableList<Booking> bookingsToShow = FXCollections.observableArrayList();
        bookingsToShow.clear();
        bookingsToShow.addAll(listOfBookings);
        bookingTableView.setItems(bookingsToShow);
    }

    //TODO: Use getLastId to refresh TableView.
    private void refreshBookingTableView() throws SQLException {
        listOfAllBookings.clear();
        listOfBookings.clear();
        listOfPendingBookings.clear();
        listOfActiveBookings.clear();
        listOfFinishedBookings.clear();
        listOfDeletedBookings.clear();
        listOfArchivedBookings.clear();
        listOfArrangementBookings.clear();
        listOfLectureBookings.clear();

        fetchBookingsFromDatabase();
        loadBookingTypeIntoTableView();
    }

    private void removeBookingFromTableView() {
        Booking bookingToRemove = bookingTableView.getSelectionModel().getSelectedItem();
        bookingTableView.getItems().remove(bookingToRemove);
    }

    private void deleteSelectedBooking() {
        try {
            bda.deleteBooking(bookingTableView.getSelectionModel().getSelectedItem());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Displays information of the clicked booking in TableView
    void showSelectedBookingInformation(TableView bookingTableView) {
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
    private void showChosenCategoryBookings(ActionEvent event) {
        ToggleButton chosenCategoryBtn = (ToggleButton) event.getSource();
        String nameOfChosenBtn = chosenCategoryBtn.getText();

        ObservableList<Booking> categorisedBookings = FXCollections.observableArrayList();
        if (overviewButton.isSelected()) {
            categorisedBookings.clear();
            switch (typeOfBooking) {
                case ALL_BOOKING_TYPES:
                    categorisedBookings.addAll(listOfBookings);
                    break;
                case LECTUREBOOKING:
                    categorisedBookings.addAll(listOfLectureBookings);
                    break;
                case ARRANGEMENTBOOKING:
                    categorisedBookings.addAll(listOfArrangementBookings);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        } else if (pendingBookingsButton.isSelected() || activeBookingsButton.isSelected() || finishedBookingsButton.isSelected() ||
                archivedBookingsButton.isSelected() || deletedBookingsButton.isSelected()) {
            categorisedBookings.clear();
            switch (typeOfBooking) {
                case ALL_BOOKING_TYPES:
                    for (Booking temp : listOfBookings) {
                        BookingStatus chosenBookingStatus = BookingStatus.statusChosen(nameOfChosenBtn);
                        if (temp.getBookingStatus().equals(chosenBookingStatus)) {
                            categorisedBookings.add(temp);
                        }
                    }
                    break;
                case LECTUREBOOKING:
                    for (Booking temp : listOfLectureBookings) {
                        BookingStatus chosenBookingStatus = BookingStatus.statusChosen(nameOfChosenBtn);
                        if (temp.getBookingStatus().equals(chosenBookingStatus)) {
                            categorisedBookings.add(temp);
                        }
                    }
                    break;
                case ARRANGEMENTBOOKING:
                    for (Booking temp : listOfArrangementBookings) {
                        BookingStatus chosenBookingStatus = BookingStatus.statusChosen(nameOfChosenBtn);
                        if (temp.getBookingStatus().equals(chosenBookingStatus)) {
                            categorisedBookings.add(temp);
                        }
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
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

    private void createNewArrangementBooking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ArrangementBookingCreation.fxml"));
            Parent root = loader.load();

            ArrangementBookingCreationController controller = loader.getController();
            controller.setBda(bda);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
            refreshBookingTableView();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createNewLectureBooking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LectureBookingCreation.fxml"));
            Parent root = loader.load();

            LectureBookingCreationController controller = loader.getController();
            controller.setBda(bda);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
            refreshBookingTableView();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void editSelectedLectureBooking(LectureBooking selectedLectureBooking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditLectureBooking.fxml"));
            Parent root = loader.load();

            EditLectureBookingController controller = loader.getController();
            controller.setSelectedLectureBooking(selectedLectureBooking);
            controller.setBda(bda);
            controller.initData();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
            refreshBookingTableView();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void editSelectedArrangementBooking(ArrangementBooking selectedArrangementBooking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditArrangementBooking.fxml"));
            Parent root = loader.load();

            EditArrangementBookingController controller = loader.getController();
            controller.setSelectedArrangementBooking(selectedArrangementBooking);
            controller.setBda(bda);
            controller.initData();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
            refreshBookingTableView();
        } catch (IOException | SQLException e) {
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
        commentTextArea.setEditable(false);

        if (selectedLectureBooking.getBookingStatus().equals(BookingStatus.STATUS_PENDING)) {
            editBookingButton.setVisible(false);
        }

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
        commentTextArea.setEditable(false);

        if (selectedArrangementBooking.getBookingStatus().equals(BookingStatus.STATUS_PENDING)) {
            editBookingButton.setVisible(false);
        }

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

    private ArrayList<Booking> getNotificationBookings(ArrayList<Booking> allBookings) {
        Iterator iter = allBookings.iterator();
        ArrayList<Booking> notifiBookings = new ArrayList<>();
        while (iter.hasNext()) {
            Booking temp = (Booking) iter.next();
            if ((temp.getDateTime().minusDays(10).isBefore(LocalDateTime.now()) || temp.getDateTime().minusDays(10).isEqual(LocalDateTime.now()))
                    && (temp.getBookingStatus() == BookingStatus.STATUS_ACTIVE || temp.getBookingStatus() == BookingStatus.STATUS_DONE)) {
                notifiBookings.add(temp);
            }
        }
        return notifiBookings;
    }

    private void showUpcomingBookings(ArrayList<Booking> upcomingBookings) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookingNotification.fxml"));
            Parent root = loader.load();

            BookingNotificationController controller = loader.getController();
            controller.setUpcomingBookings(upcomingBookings);
            controller.setController(this);
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

    private void loadBookingTypeIntoTableView() {
        if (typeOfBooking.equals(BookingType.ALL_BOOKING_TYPES)) {
            loadBookingsToTableView(listOfBookings);
        } else if (typeOfBooking.equals(BookingType.LECTUREBOOKING)) {
            loadBookingsToTableView(listOfLectureBookings);
        } else if (typeOfBooking.equals(BookingType.ARRANGEMENTBOOKING)) {
            loadBookingsToTableView(listOfArrangementBookings);
        }
    }

    @FXML
    private void changeTypeOfBooking(ActionEvent event) {
        MenuItem chosenType = (MenuItem) event.getSource();
        String nameOfChosenBtn = chosenType.getText();

        if (nameOfChosenBtn.equals("Alle bookings")) {
            typeOfBooking = BookingType.ALL_BOOKING_TYPES;
        } else if (nameOfChosenBtn.equals("Børnefødselsdage")) {
            typeOfBooking = BookingType.ARRANGEMENTBOOKING;
        } else if (nameOfChosenBtn.equals("Skoletjenester")) {
            typeOfBooking = BookingType.LECTUREBOOKING;
        }
        loadBookingTypeIntoTableView();
    }
}