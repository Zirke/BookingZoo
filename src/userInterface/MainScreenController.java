package userInterface;

import bookings.ArrangementBooking;
import bookings.Booking;
import bookings.BookingDataAccessor;
import bookings.LectureBooking;
import customComparators.CustomBookingComparator;
import customers.LectureBookingCustomer;
import email.SendEmail;
import enums.BookingStatus;
import enums.BookingType;
import enums.StatisticType;
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
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.textfield.TextFields;
import postToCalendars.PostToGoogle;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
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
    private ArrayList<Booking> listOfNonArchivedOrDeletedLectureBookings = new ArrayList<>();
    private ArrayList<Booking> listOfNonArchivedOrDeletedArrangementBookings = new ArrayList<>();
    private BookingType typeOfBooking;

    public MainScreenController() throws SQLException, ClassNotFoundException {
    }

    void setTypeOfBooking(BookingType typeOfBooking) {
        this.typeOfBooking = typeOfBooking;

        chosenBookingTypeLabel.setText(typeOfBooking.toString());

        //Opens notification window
        ArrayList<Booking> noficationBookings = getNotificationBookings(listOfAllBookings);
        notificationLabel.setText("(" + Integer.toString(noficationBookings.size()) + ")");
        notificationButton.setOnMouseClicked(e -> showUpcomingBookingsWindow(noficationBookings));

        TextFields.bindAutoCompletion(searchField, setCorrectTypeOfBookingsToSearchFor());
        setChosenBookingTypeIntoTableView();
    }

    @FXML
    private MenuBar menuBar;
    @FXML
    private Label chosenBookingTypeLabel, notificationLabel;
    @FXML
    private ToggleButton overviewButton, pendingBookingsButton, activeBookingsButton,
            finishedBookingsButton, archivedBookingsButton, deletedBookingsButton;
    @FXML
    private ToggleGroup categoryButtonsToggleGroup;
    @FXML
    private Button refreshBookingsButton, notificationButton;
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
    private RadioMenuItem allBookingsMenuItem, arrangementBookingsMenuItem, lectureBooingsMenuItem;

    //Nodes for booking information display area
    @FXML
    private Label commentLabel, customerCommentLabel, bookingTypeLabel, bookingStatusLabel, creationDateLabel, dateLabel,
            timeLabel, pupilNoLabel, teamNoLabel, teacherNoLabel, gradeLabel, topicChoiceLabel, schoolNameLabel,
            schoolPhoneNumberLabel, zipcodeLabel, cityLabel, communeLabel, phoneNumberLabel, contactPersonLabel,
            emailLabel, eanLabel, guide_lecturerLabel, roomLabel;
    @FXML
    private TextArea customerCommentTextArea, commentTextArea;
    @FXML
    private Button acceptBookingButton, cancelBookingButton, editBookingButton, deleteButton;

    public void initialize() throws SQLException {

        fetchBookingsFromDatabase();
        moveConductedBookingToArchived();

        //Makes sure that no ToggleButton can be unselected
        GeneralController.get().addAlwaysOneSelectedSupport(categoryButtonsToggleGroup);

        bookingTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                //hideBookingInformation();
            }
        });

        /*
         *   Event handlers
         */

        deleteButton.setOnMouseClicked(e -> {
            deleteSelectedBookingFromDatabase();
            removeBookingFromTableView();
        });

        //Shows searched for booking in TableView
        searchField.setOnAction(e -> {
            if (searchField.getText().isEmpty()) {
                setChosenBookingTypeIntoTableView();
            } else showSearchedForBookingsInTableView(listOfAllBookings);
        });

        bookingTableView.setOnMouseClicked(e -> displayInformationOfSelectedBooking(bookingTableView));

        //Opens pop-up window corresponding to chosen menu item (method used from GeneralController)
        lectureBookingItem.setOnAction(e -> createNewLectureBooking());
        arrangementBookingItem.setOnAction(e -> createNewArrangementBooking());

        //Reloads the bookings from database into TableView
        refreshBookingsButton.setOnMouseClicked(e -> {
            try {
                refetchBookingsFromDataBase();
                moveConductedBookingToArchived();
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
        acceptBookingButton.setOnMouseClicked(e -> acceptBookingDialog());

        //Cancelling the selected booking when pressing cancelBookingButton
        cancelBookingButton.setOnMouseClicked(e -> cancelBookingDialog());
    }

    /*
     *   METHODS
     */

    @FXML
    private void changeTypeOfBooking(ActionEvent event) {
        MenuItem chosenType = (MenuItem) event.getSource();
        String nameOfChosenBtn = chosenType.getText();

        switch (nameOfChosenBtn) {
            case "Alle bookings":
                setTypeOfBooking(BookingType.ALL_BOOKING_TYPES);
                showPendingBookingPopUp();
                removeStatisticMenu(BookingType.ALL_BOOKING_TYPES);
                break;
            case "Børnefødselsdage":
                setTypeOfBooking(BookingType.ARRANGEMENTBOOKING);
                removeStatisticMenu(BookingType.ARRANGEMENTBOOKING);
                showPendingBookingPopUp();
                break;
            case "Skoletjenester":
                setTypeOfBooking(BookingType.LECTUREBOOKING);

                showPendingBookingPopUp();
                showStatisticInfo();
                break;
        }
        //setChosenBookingTypeIntoTableView() //virker fint uden
    }

    private void fetchBookingsFromDatabase() throws SQLException {
        //Lecture bookings
        try {
            listOfLectureBookings.addAll(bda.fetchLecBooks());
            listOfAllBookings.addAll(listOfLectureBookings);
        } catch (NoBookingsInDatabaseException e) {
            System.out.println("No lecture bookings in database"); //TODO Lav om til exception handling
        }
        //Arrangement bookings
        try {
            listOfArrangementBookings.addAll(bda.fetchArrBooks());
            listOfAllBookings.addAll(listOfArrangementBookings);
        } catch (NoBookingsInDatabaseException e) {
            System.out.println("No arrangement bookings in database"); //TODO Lav om til exception handling
        }
        //Categorised bookings
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
            //No Archived or Deleted bookings (both types)
            if (!tempBooking.getBookingStatus().equals(BookingStatus.STATUS_ARCHIVED) &&
                    (!tempBooking.getBookingStatus().equals(BookingStatus.STATUS_DELETED))) {
                listOfBookings.add(tempBooking);
                //No Archived or Deleted bookings (sorted types)
                if (tempBooking.getBookingType().equals(BookingType.LECTUREBOOKING)) {
                    listOfNonArchivedOrDeletedLectureBookings.add(tempBooking);
                }
                if (tempBooking.getBookingType().equals(BookingType.ARRANGEMENTBOOKING)) {
                    listOfNonArchivedOrDeletedArrangementBookings.add(tempBooking);
                }
            }
        }
    }

    private void setChosenBookingTypeIntoTableView() {
        if (typeOfBooking.equals(BookingType.ALL_BOOKING_TYPES)) {
            loadBookingsToTableView(listOfBookings);
        } else if (typeOfBooking.equals(BookingType.LECTUREBOOKING)) {
            loadBookingsToTableView(listOfNonArchivedOrDeletedLectureBookings);
        } else if (typeOfBooking.equals(BookingType.ARRANGEMENTBOOKING)) {
            loadBookingsToTableView(listOfNonArchivedOrDeletedArrangementBookings);
        }
    }

    //Takes an ArrayList of bookings to load into TableView of bookings
    private void loadBookingsToTableView(ArrayList<Booking> listOfChosenBookings) {
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
        bookingTypeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingType"));
        bookingContactPersonColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        bookingDateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDateTime().toLocalDate().toString()));

        ObservableList<Booking> bookingsToShow = FXCollections.observableArrayList();
        //bookingsToShow.clear();
        bookingsToShow.addAll(listOfChosenBookings);
        bookingsToShow.sort(new CustomBookingComparator());
        bookingTableView.setItems(bookingsToShow);
    }


    private void acceptSelectedBooking() {
        try {
            bda.changeBookingStatus(bookingTableView.getSelectionModel().getSelectedItem(), BookingStatus.STATUS_ACTIVE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeBookingFromTableView() {
        Booking bookingToRemove = bookingTableView.getSelectionModel().getSelectedItem();
        bookingTableView.getItems().remove(bookingToRemove);
    }

    private void deleteSelectedBookingFromDatabase() {
        try {
            bda.deleteBooking(bookingTableView.getSelectionModel().getSelectedItem());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Displays information of the clicked booking in TableView. (The method will be used on both the MainScreen and BookingNotification)
    void displayInformationOfSelectedBooking(TableView<Booking> bookingTableView) {
        if (bookingTableView.getSelectionModel().getSelectedItem() instanceof LectureBooking) {
            showLectureBookingInformation((LectureBooking) bookingTableView.getSelectionModel().getSelectedItem());
        } else if (bookingTableView.getSelectionModel().getSelectedItem() instanceof ArrangementBooking) {
            showArrangementBookingInformation((ArrangementBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
        }
    }

    //TODO fix to typeOfBooking
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
                    categorisedBookings.addAll(listOfNonArchivedOrDeletedLectureBookings);
                    break;
                case ARRANGEMENTBOOKING:
                    categorisedBookings.addAll(listOfNonArchivedOrDeletedArrangementBookings);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        } else if (pendingBookingsButton.isSelected() || activeBookingsButton.isSelected() || finishedBookingsButton.isSelected() ||
                archivedBookingsButton.isSelected() || deletedBookingsButton.isSelected()) {
            categorisedBookings.clear();

            switch (typeOfBooking) {
                case ALL_BOOKING_TYPES:
                    for (Booking temp : listOfAllBookings) {
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
        categorisedBookings.sort(new CustomBookingComparator());
        bookingTableView.setItems(categorisedBookings);
    }


    //TODO: Use getLastId to refresh TableView.
    public void refetchBookingsFromDataBase() throws SQLException {
        listOfAllBookings.clear();
        listOfBookings.clear();
        listOfPendingBookings.clear();
        listOfActiveBookings.clear();
        listOfFinishedBookings.clear();
        listOfDeletedBookings.clear();
        listOfArchivedBookings.clear();
        listOfArrangementBookings.clear();
        listOfLectureBookings.clear();
        listOfNonArchivedOrDeletedLectureBookings.clear();
        listOfNonArchivedOrDeletedArrangementBookings.clear();

        fetchBookingsFromDatabase();
        setChosenBookingTypeIntoTableView();
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
        roomLabel.setVisible(true); //new

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
        roomLabel.setText("Klasseværelse: " + selectedLectureBooking.getLectureRoom().getType().toString());
        customerCommentTextArea.setText(selectedLectureBooking.getComment());
        commentTextArea.setText(selectedLectureBooking.getComment());

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

    private ArrayList<Booking> getNotificationBookings(ArrayList<Booking> allBookings) {
        Iterator iter = allBookings.iterator();
        ArrayList<Booking> notifiBookings = new ArrayList<>();

        while (iter.hasNext()) {
            Booking temp = (Booking) iter.next();
            switch (typeOfBooking) {
                case ALL_BOOKING_TYPES: {
                    if (isNotificationBooking(temp)) {
                        notifiBookings.add(temp);
                    }
                }
                break;
                case ARRANGEMENTBOOKING: {
                    boolean isArrangementBooking = temp.getClass().equals(ArrangementBooking.class);
                    if (isArrangementBooking && isNotificationBooking(temp)) {
                        notifiBookings.add(temp);
                    }
                }
                break;
                case LECTUREBOOKING: {
                    boolean isLectureBooking = temp.getClass().equals(LectureBooking.class);
                    if (isLectureBooking && isNotificationBooking(temp)) {
                        notifiBookings.add(temp);
                    }
                }
                break;
            }
        }
        return notifiBookings;
    }

    private Boolean isNotificationBooking(Booking temp) {
        return ((temp.getDateTime().minusDays(10).isBefore(LocalDateTime.now()) ||
                temp.getDateTime().minusDays(10).isEqual(LocalDateTime.now()))
                && (temp.getBookingStatus() == BookingStatus.STATUS_ACTIVE || temp.getBookingStatus() == BookingStatus.STATUS_DONE)) &&
                !temp.getDateTime().isBefore(LocalDateTime.now());
    }


    private ArrayList<String> setCorrectTypeOfBookingsToSearchFor() {
        ArrayList<String> listOfContactPersonNames = new ArrayList<>();
        listOfContactPersonNames.clear();

        if (typeOfBooking.equals(BookingType.ALL_BOOKING_TYPES)) {
            listOfContactPersonNames.clear();
            for (Booking temp : listOfAllBookings) {
                listOfContactPersonNames.add(temp.getCustomer().getContactPerson());
            }
        } else if (typeOfBooking.equals(BookingType.ARRANGEMENTBOOKING)) {
            listOfContactPersonNames.clear();
            for (Booking temp : listOfArrangementBookings) {
                listOfContactPersonNames.add(temp.getCustomer().getContactPerson());
            }
        } else if (typeOfBooking.equals(BookingType.LECTUREBOOKING)) {
            listOfContactPersonNames.clear();
            for (Booking temp : listOfLectureBookings) {
                listOfContactPersonNames.add(temp.getCustomer().getContactPerson());
            }
        }
        return listOfContactPersonNames;
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
            //refetchBookingsFromDataBase();
        } catch (IOException e) {
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
            //refetchBookingsFromDataBase();
        } catch (IOException e) {
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
            //refetchBookingsFromDataBase();
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
            controller.setBda(bda);
            controller.initData();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
            //refetchBookingsFromDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showUpcomingBookingsWindow(ArrayList<Booking> upcomingBookings) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookingNotification.fxml"));
            Parent root = loader.load();

            BookingNotificationController controller = loader.getController();
            controller.setUpcomingBookings(upcomingBookings);
            controller.setController(this);
            controller.setTypeOfBooking(typeOfBooking);
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

    private void showStatisticWindow(StatisticType type) {
        ArrayList<Booking> listOfBooking = new ArrayList<>();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Statistic.fxml"));
            Parent root = loader.load();

            StatisticController control = loader.getController();
            listOfBooking.addAll(listOfLectureBookings);
            control.setLectureBookings(listOfBooking);
            control.setSetting(type);
            control.initialise();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        //roomLabel.setVisible(true);

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

        if(selectedArrangementBooking.getRestaurant().getType() != null){
            roomLabel.setVisible(true);
            roomLabel.setText("Den valgte restaurant er: " + selectedArrangementBooking.getRestaurant().getType().toString());
        }

    }

    private void acceptBookingDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Vil du acceptere bookingen?");
        alert.setContentText("Bookingen flyttes til 'Aktive'");

        Optional<ButtonType> alertChoice = alert.showAndWait();

        if (alertChoice.get() == ButtonType.OK) {
            if ((bookingTableView.getSelectionModel().getSelectedItem()).getBookingType() == (BookingType.ARRANGEMENTBOOKING)) {
                SendEmail.sendConfirmationEmail((ArrangementBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                PostToGoogle newConfirmedArrangementBooking = new PostToGoogle((ArrangementBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                newConfirmedArrangementBooking.postNewArrangementToCalendar();
            } else if ((bookingTableView.getSelectionModel().getSelectedItem()).getBookingType() == (BookingType.LECTUREBOOKING)) {
                SendEmail.sendConfirmationEmail((LectureBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                PostToGoogle newConfirmedLectureBooking = new PostToGoogle((LectureBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                newConfirmedLectureBooking.postNewLectureToCalendar();
            }
        }
        acceptSelectedBooking();

        removeBookingFromTableView();
    }

    private void cancelBookingDialog() {
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
    }

    void showPendingBookingPopUp() {
        int numberOfPendingBookings = 0;
        ArrayList<Booking> tempArray = new ArrayList<>();

        if (typeOfBooking.equals(BookingType.ALL_BOOKING_TYPES)) {
            numberOfPendingBookings = listOfPendingBookings.size();
            tempArray.addAll(listOfPendingBookings);
        } else if (typeOfBooking.equals(BookingType.LECTUREBOOKING)) {
            for (Booking temp : listOfPendingBookings) {
                if (temp.getBookingType().equals(BookingType.LECTUREBOOKING)) {
                    tempArray.add(temp);
                }
            }
            numberOfPendingBookings = tempArray.size();
        } else if (typeOfBooking.equals(BookingType.ARRANGEMENTBOOKING)) {
            for (Booking temp : listOfPendingBookings) {
                if (temp.getBookingType().equals(BookingType.ARRANGEMENTBOOKING)) {
                    tempArray.add(temp);
                }
            }
            numberOfPendingBookings = tempArray.size();
        }
        if (numberOfPendingBookings > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Der er " + numberOfPendingBookings + " afventende booking(s)");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("imageView/zooicon.png"));

            ButtonType buttonTypeOne = new ButtonType("Vis afventende bookings");
            ButtonType buttonTypeTwo = new ButtonType("Luk", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

            Optional<ButtonType> alertChoice = alert.showAndWait();

            if (alertChoice.get() == buttonTypeOne) {
                pendingBookingsButton.setSelected(true);
                loadBookingsToTableView(tempArray);
            } else overviewButton.setSelected(true);
        }
    }

    void showStatisticInfo() {
        Menu menu = new Menu("Statistik");
        menu.getItems().add(new MenuItem("Over elever og lærer"));
        menu.getItems().add(new MenuItem("Over emnevalg"));
        menu.getItems().add(new MenuItem("Over klassetrin"));
        menu.getItems().add(new MenuItem("Over Aalborg kommune"));
        menuBar.getMenus().add(menu);

        statestikPressed();
    }

    private void statestikPressed() {
        ArrayList<MenuItem> items = new ArrayList<>();
        ObservableList list = menuBar.getMenus();

        for (Object i : list) {
            if (((Menu) i).getText().equals("Statistik")) {
                items.addAll(((Menu) i).getItems());
            }
        }

        listenerForStatisticMenuBar(items);
        //items.get(0).setOnAction(e -> {
        //showStatisticWindow(StatisticType.STUDENTS_AND_TEACHER);
        //});

    }

    private void listenerForStatisticMenuBar(ArrayList<MenuItem> menuItems) {
        for (int i = 0; i < menuItems.size(); i++) {
            int finalI = i;
            menuItems.get(i).setOnAction(e -> {
                showStatisticWindow(StatisticType.toStatisticType(menuItems.get(finalI).getText()));
            });
        }
    }

    private void moveConductedBookingToArchived() {
        for (Booking temp : listOfBookings) {
            int time = (int) (Duration.between(LocalDateTime.now(), temp.getDateTime()).toDays());
            if (time <= 0 && (temp.getBookingType().equals(BookingType.LECTUREBOOKING))) {
                try {
                    temp.setBookingStatus(BookingStatus.STATUS_ARCHIVED);
                    bda.editLecBook((LectureBooking) temp);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (time <= 0 && (temp.getBookingType().equals(BookingType.ARRANGEMENTBOOKING))) {
                try {
                    temp.setBookingStatus(BookingStatus.STATUS_ARCHIVED);
                    bda.editArrBook((ArrangementBooking) temp);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void hideBookingInformation() {
        commentLabel.setVisible(false);
        customerCommentLabel.setVisible(false);
        bookingTypeLabel.setVisible(false);
        bookingStatusLabel.setVisible(false);
        creationDateLabel.setVisible(false);
        dateLabel.setVisible(false);
        timeLabel.setVisible(false);
        pupilNoLabel.setVisible(false);
        teamNoLabel.setVisible(false);
        teacherNoLabel.setVisible(false);
        gradeLabel.setVisible(false);
        topicChoiceLabel.setVisible(false);
        schoolNameLabel.setVisible(false);
        schoolPhoneNumberLabel.setVisible(false);
        zipcodeLabel.setVisible(false);
        cityLabel.setVisible(false);
        communeLabel.setVisible(false);
        phoneNumberLabel.setVisible(false);
        contactPersonLabel.setVisible(false);
        emailLabel.setVisible(false);
        eanLabel.setVisible(false);
        guide_lecturerLabel.setVisible(false);
        customerCommentTextArea.setVisible(false);
        commentTextArea.setVisible(false);
        acceptBookingButton.setVisible(false);
        cancelBookingButton.setVisible(false);
        editBookingButton.setVisible(false);
        deleteButton.setVisible(false);
    }

    private void removeStatisticMenu(BookingType type) {
        ArrayList<Integer> removeIndexed = new ArrayList<>();
        for (int i = 0; i < menuBar.getMenus().size(); i++) {
            if (menuBar.getMenus().get(i).getText().equals("Statistik")) {
                removeIndexed.add(i);
            }
        }
        for (int i = removeIndexed.size() - 1; i >= 0; i--) {
            int delete = removeIndexed.get(i);
            menuBar.getMenus().remove(delete);
        }
    }
}