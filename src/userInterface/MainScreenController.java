package userInterface;

import bookings.ArrangementBooking;
import bookings.Booking;
import bookings.BookingDataAccessor;
import bookings.LectureBooking;
import customers.LectureBookingCustomer;
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
import postToCalendars.PostToGoogle;

import java.io.IOException;
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
    @FXML
    public MenuBar Menubar;
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

        switch (typeOfBooking){
            case LECTUREBOOKING:{
                showStatisticInfo();
            }break;
            default:
        }

        //Opens notification window
        ArrayList<Booking> noficationBookings = getNotificationBookings(listOfAllBookings);
        notificationLabel.setText("(" + Integer.toString(noficationBookings.size()) + ")");
        notificationButton.setOnMouseClicked(e -> showUpcomingBookingsWindow(noficationBookings));

        TextFields.bindAutoCompletion(searchField, setCorrectTypeOfBookingsToSearchFor());
        setChosenBookingTypeIntoTableView();
    }

    @FXML
    private Label chosenBookingTypeLabel, notificationLabel;
    @FXML
    private ToggleButton overviewButton, pendingBookingsButton, activeBookingsButton,
            finishedBookingsButton, archivedBookingsButton, deletedBookingsButton;
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

    public void initialize() throws SQLException {

        fetchBookingsFromDatabase();

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

        bookingTableView.setOnMouseClicked(e -> displayInformationOfSelectedBooking());

        //Opens pop-up window corresponding to chosen menu item (method used from GeneralController)
        lectureBookingItem.setOnAction(e -> createNewLectureBooking());
        arrangementBookingItem.setOnAction(e -> createNewArrangementBooking());

        //Reloads the bookings from database into TableView
        refreshBookingsButton.setOnMouseClicked(e -> {
            try {
                refetchBookingsFromDataBase();
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
                    PostToGoogle newConfirmedArrangementBooking = new PostToGoogle((ArrangementBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                    newConfirmedArrangementBooking.postNewArrangementToCalendar();
                    if ((bookingTableView.getSelectionModel().getSelectedItem()).getBookingType() == (BookingType.LECTUREBOOKING)) {
                        PostToGoogle newConfirmedLectureBooking = new PostToGoogle((LectureBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                        newConfirmedLectureBooking.postNewLectureToCalendar();
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
    }

    /*
     *   METHODS
     */

    @FXML
    private void changeTypeOfBooking(ActionEvent event) throws SQLException {
        MenuItem chosenType = (MenuItem) event.getSource();
        String nameOfChosenBtn = chosenType.getText();

        if (nameOfChosenBtn.equals("Alle bookings")) {
            setTypeOfBooking(BookingType.ALL_BOOKING_TYPES);
        } else if (nameOfChosenBtn.equals("Børnefødselsdage")) {
            setTypeOfBooking(BookingType.ARRANGEMENTBOOKING);
        } else if (nameOfChosenBtn.equals("Skoletjenester")) {
            setTypeOfBooking(BookingType.LECTUREBOOKING);
        }
        setChosenBookingTypeIntoTableView();

    }

    private void fetchBookingsFromDatabase() throws SQLException {
        //Lecture bookings
        try {
            listOfLectureBookings.addAll(bda.fetchLecBooks());
            listOfAllBookings.addAll(listOfLectureBookings);
        } catch (NoBookingsInDatabaseException e) {
            System.out.println("No lecture bookings in database"); //Lav om til exception handling
        }
        //Arrangement bookings
        try {
            listOfArrangementBookings.addAll(bda.fetchArrBooks());
            listOfAllBookings.addAll(listOfArrangementBookings);
        } catch (NoBookingsInDatabaseException e) {
            System.out.println("No arrangement bookings in database"); //Lav om til exception handling
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

        bookingsToShow.clear();
        bookingsToShow.addAll(listOfChosenBookings);
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
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Displays information of the clicked booking in TableView
    void displayInformationOfSelectedBooking() {
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
            refetchBookingsFromDataBase();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    //TODO: Use getLastId to refresh TableView.
    private void refetchBookingsFromDataBase() throws SQLException {
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
            refetchBookingsFromDataBase();
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
            refetchBookingsFromDataBase();
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
            refetchBookingsFromDataBase();
        } catch (IOException | SQLException e) {
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

    private void showStatisticInfo(){
        Menu menu = new Menu("Statistic");
        menu.getItems().add(new MenuItem("New"));
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(new MenuItem("Exit"));
        Menubar.getMenus().add(menu);


    }


}