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
import enums.FacilityState;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

import static enums.BookingStatus.*;
import static postToCalendars.PostToGoogle.postToCalendar;

public class MainScreenController extends GeneralController {
    private BookingDataAccessor bda = BookingDataAccessor.connect();

    private ArrayList<Booking> listOfAllBookings = new ArrayList<>();
    private ArrayList<Booking> listOfNonArchivedOrDeletedBookings = new ArrayList<>();
    private ArrayList<Booking> listOfPendingBookings = new ArrayList<>();
    private ArrayList<Booking> listOfActiveBookings = new ArrayList<>();
    private ArrayList<Booking> listOfFinishedBookings = new ArrayList<>();
    private ArrayList<Booking> listOfArchivedBookings = new ArrayList<>();
    private ArrayList<Booking> listOfDeletedBookings = new ArrayList<>();
    private ArrayList<Booking> listOfLectureBookings = new ArrayList<>();
    private ArrayList<Booking> listOfArrangementBookings = new ArrayList<>();
    private ArrayList<Booking> listOfNonArchivedOrDeletedLectureBookings = new ArrayList<>();
    private ArrayList<Booking> listOfNonArchivedOrDeletedArrangementBookings = new ArrayList<>();
    private HashMap<LocalDateTime, LectureBooking> LecRoomHashMap = new HashMap<>();
    private HashMap<LocalDateTime, ArrangementBooking> ArrTimeHashMap = new HashMap<>();
    private BookingType typeOfBooking;
    private AutoCompletionBinding<String> autoCompletionBinding;

    public MainScreenController() throws SQLException, ClassNotFoundException {
    }

    void setTypeOfBooking(BookingType typeOfBooking) {
        this.typeOfBooking = typeOfBooking;

        chosenBookingTypeLabel.setText(typeOfBooking.toString());

        //Opens notification window
        ArrayList<Booking> notificationBookings = getNotificationBookings(listOfAllBookings);
        notificationLabel.setText("(" + Integer.toString(notificationBookings.size()) + ")");
        notificationButton.setOnMouseClicked(e -> showUpcomingBookingsWindow(notificationBookings));

        ArrayList<String> temp = setCorrectTypeOfBookingsToSearchFor();
        if (autoCompletionBinding != null) {
            autoCompletionBinding.dispose();
        }
        autoCompletionBinding = TextFields.bindAutoCompletion(searchField, temp);
        setChosenBookingTypeIntoTableView();
    }

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
    private MenuItem municipalityMenuItem, gradeMenuItem, choiceOfTopicMenuItem, pupilsAndTeachersMenuItem, chosenMenuesMenuItem;

    //Nodes for booking information display area
    @FXML
    private VBox informationDisplayVBox;
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

        //Hides booking information, when no booking is selected
        bookingTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                informationDisplayVBox.setVisible(true);
            } else informationDisplayVBox.setVisible(false);
        });

        /*
         *   Event handlers
         */



        //Shows searched for booking in TableView
        searchField.setOnAction(e -> {
            if (searchField.getText().isEmpty()) {
                setChosenBookingTypeIntoTableView();
            } else if (typeOfBooking.equals(BookingType.ALL_BOOKING_TYPES)) {
                showSearchedForBookingsInTableView(listOfAllBookings);
            } else if (typeOfBooking.equals(BookingType.LECTUREBOOKING)) {
                showSearchedForBookingsInTableView(listOfLectureBookings);
            } else showSearchedForBookingsInTableView(listOfArrangementBookings);
        });

        //Displays information contained in selected booking
        bookingTableView.setOnMouseClicked(e -> displayInformationOfSelectedBooking(bookingTableView));

        //Opens pop-up window corresponding to chosen menu item (method used from GeneralController)
        lectureBookingItem.setOnAction(e -> createNewLectureBooking());
        arrangementBookingItem.setOnAction(e -> createNewArrangementBooking());

        //Reloads the bookings from database into TableView
        refreshBookingsButton.setOnMouseClicked(e -> {

                fetchOnlyNewBookingsFromDataBase();


            moveConductedBookingToArchived();
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

        deleteButton.setOnMouseClicked(e -> {
            deleteSelectedBookingFromDatabase();
            removeBookingFromTableView();
        });
    }

    /*
     *   METHODS
     */

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
            e.printStackTrace();
            System.out.println("No arrangement bookings in database"); //TODO Lav om til exception handling
        }
        updateAllBookingLists(listOfAllBookings);
    }

    private void updateAllBookingLists(ArrayList<Booking> inputList) {
        //Categorised bookings
        for (Booking tempBooking : inputList) {
            if (tempBooking.getBookingStatus().equals(STATUS_PENDING)) {
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
                listOfNonArchivedOrDeletedBookings.add(tempBooking);
                //No Archived or Deleted bookings (sorted types)
                if (tempBooking.getBookingType().equals(BookingType.LECTUREBOOKING)) {
                    listOfNonArchivedOrDeletedLectureBookings.add(tempBooking);
                }
                if (tempBooking.getBookingType().equals(BookingType.ARRANGEMENTBOOKING)) {
                    listOfNonArchivedOrDeletedArrangementBookings.add(tempBooking);
                }
            }

            if (tempBooking instanceof LectureBooking) {
                if (!LecRoomHashMap.containsKey(tempBooking.getDateTime())) {
                    LecRoomHashMap.put(tempBooking.getDateTime(), (LectureBooking) tempBooking);
                    ((LectureBooking) tempBooking).getLectureRoom().setState(FacilityState.OCCUPIED);
                } else {
                    LecRoomHashMap.put(tempBooking.getDateTime().plusMinutes(1), (LectureBooking) tempBooking);
                    ((LectureBooking) tempBooking).getLectureRoom().setState(FacilityState.OCCUPIED);
                }
            }else if(tempBooking instanceof ArrangementBooking){
                if (!ArrTimeHashMap.containsKey(tempBooking.getDateTime())) {
                    ArrTimeHashMap.put(tempBooking.getDateTime(), (ArrangementBooking) tempBooking);
                } else {
                    ArrTimeHashMap.put(tempBooking.getDateTime().plusMinutes(1), (ArrangementBooking) tempBooking);
                }
            }
        }
    }

    @FXML
    private void changeTypeOfBooking(ActionEvent event) {
        MenuItem chosenType = (MenuItem) event.getSource();
        String nameOfChosenBtn = chosenType.getText();

        switch (nameOfChosenBtn) {
            case "Alle bookings":
                setTypeOfBooking(BookingType.ALL_BOOKING_TYPES);
                showPendingBookingPopUp();
                break;
            case "Børnefødselsdage":
                setTypeOfBooking(BookingType.ARRANGEMENTBOOKING);
                showPendingBookingPopUp();
                break;
            case "Skoletjenester":
                setTypeOfBooking(BookingType.LECTUREBOOKING);
                showPendingBookingPopUp();
                break;
        }
    }

    private void setChosenBookingTypeIntoTableView() {
        loadBookingsToTableView(getBookingStatusArrayList(this.typeOfBooking));

        /*
        if (typeOfBooking.equals(BookingType.ALL_BOOKING_TYPES)) {
            loadBookingsToTableView(getBookingStatusArrayList(BookingType.ALL_BOOKING_TYPES));
        } else if (typeOfBooking.equals(BookingType.LECTUREBOOKING)) {
            loadBookingsToTableView(getBookingStatusArrayList(BookingType.LECTUREBOOKING));
        } else if (typeOfBooking.equals(BookingType.ARRANGEMENTBOOKING)) {
            loadBookingsToTableView(getBookingStatusArrayList(BookingType.ARRANGEMENTBOOKING));
        }
        */
    }

    private ArrayList<Booking> getBookingStatusArrayList(BookingType type) {
        ArrayList<Booking> result = new ArrayList<>();
        switch (type) {
            case LECTUREBOOKING: {
                result = listOfBookingStatusArrayList(listOfNonArchivedOrDeletedLectureBookings);
                break;
            }
            case ARRANGEMENTBOOKING: {
                result = listOfBookingStatusArrayList(listOfNonArchivedOrDeletedArrangementBookings);
                break;
            }
            case ALL_BOOKING_TYPES: {
                result = listOfBookingStatusArrayList(listOfNonArchivedOrDeletedBookings);
                break;
            }
        }
        return result;
    }

    private ArrayList<Booking> listOfBookingStatusArrayList(ArrayList<Booking> bookingList) {
        ArrayList<Booking> result = new ArrayList<>();
        for (Booking i : bookingList) {
            if (overviewButton.isSelected()) {
                result.add(i);
            } else if (pendingBookingsButton.isSelected() && i.getBookingStatus().equals(STATUS_PENDING)) {
                result.add(i);
            } else if (activeBookingsButton.isSelected() && i.getBookingStatus().equals(STATUS_ACTIVE)) {
                result.add(i);
            } else if (finishedBookingsButton.isSelected() && i.getBookingStatus().equals(STATUS_DONE)) {
                result.add(i);
            } else if (archivedBookingsButton.isSelected() && i.getBookingStatus().equals(STATUS_ARCHIVED)) {
                result.add(i);
            } else if (deletedBookingsButton.isSelected() && i.getBookingStatus().equals(STATUS_DELETED)) {
                result.add(i);
            }
        }
        return result;
    }

    private void acceptSelectedBooking() {
        try {
            bda.changeBookingStatus(bookingTableView.getSelectionModel().getSelectedItem(), BookingStatus.STATUS_ACTIVE);
            refetchBookingsFromDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeBookingFromTableView() {
        Booking bookingToRemove = bookingTableView.getSelectionModel().getSelectedItem();
        bookingTableView.getItems().remove(bookingToRemove);
    }

    private void deleteSelectedBookingFromDatabase() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Vil du slette bookingen?");
        alert.setContentText("Bookingen vil blive slettet fra hele systemet og kan ikke genoprettes");

        Optional<ButtonType> alertChoice = alert.showAndWait();

        if (alertChoice.get() == ButtonType.OK) {
            try {
                bda.deleteBooking(bookingTableView.getSelectionModel().getSelectedItem());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            removeBookingFromTableView();
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
            Boolean isCustomer = temp.getCustomer().getContactPerson().equals(enteredBooking)
                    || temp.getCustomer().getEmail().equals(enteredBooking);
            Boolean isLectureCustomer = false;
            if (temp instanceof LectureBooking) {
                isLectureCustomer = (((LectureBookingCustomer) temp.getCustomer()).getSchoolName().equals(enteredBooking))
                        || (((LectureBookingCustomer) temp.getCustomer()).getCommune().equals(enteredBooking))
                        || (((LectureBookingCustomer) temp.getCustomer()).getCity().equals(enteredBooking));
            }
            if (temp instanceof LectureBooking && isCustomer || isLectureCustomer) {
                bookingTableView.getSelectionModel().clearSelection();
                ObservableList<Booking> bookings = FXCollections.observableArrayList();
                bookings.add(temp);
                bookingTableView.setItems(bookings);
            } else if (isCustomer) {
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
                    categorisedBookings.addAll(listOfNonArchivedOrDeletedBookings);
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

    //Takes an ArrayList of bookings to load into TableView of bookings
    private void loadBookingsToTableView(ArrayList<Booking> listOfChosenBookings) {
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
        bookingTypeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingType"));
        bookingContactPersonColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        bookingDateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getDateTime().getDayOfMonth()+"/"+
                        cellData.getValue().getDateTime().getMonthValue()+"/"+
                        cellData.getValue().getDateTime().getYear()));
        ObservableList<Booking> bookingsToShow = FXCollections.observableArrayList();
        //bookingsToShow.clear();
        bookingsToShow.addAll(listOfChosenBookings);
        bookingsToShow.sort(new CustomBookingComparator());
        bookingTableView.setItems(bookingsToShow);
    }

    //TODO: Use getLastId to refresh TableView.
    void refetchBookingsFromDataBase() throws SQLException {
        listOfAllBookings.clear();
        listOfNonArchivedOrDeletedBookings.clear();
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

    void fetchOnlyNewBookingsFromDataBase(){

        try {
            updateAllBookingLists(bda.refreshBookings(listOfAllBookings));
        } catch (SQLException e) {
            try {
                bda = BookingDataAccessor.connect();
            } catch (SQLException e1) {
                cantConnect();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        setChosenBookingTypeIntoTableView();

    }

    //Changes text on all labels corresponding to the chosen booking in ListView
    private void showLectureBookingInformation(LectureBooking selectedLectureBooking) {
        showPendingButtons(selectedLectureBooking.getBookingStatus());
        showDeleteBookingButton(selectedLectureBooking.getBookingStatus());

        communeLabel.setVisible(true);
        cityLabel.setVisible(true);
        contactPersonLabel.setVisible(true);
        phoneNumberLabel.setVisible(true);
        emailLabel.setVisible(true);
        eanLabel.setVisible(true);
        guide_lecturerLabel.setVisible(true);
        roomLabel.setVisible(true);
        customerCommentLabel.setVisible(true);
        customerCommentTextArea.setVisible(true);
        customerCommentTextArea.setEditable(false);
        commentLabel.setVisible(true);
        commentTextArea.setVisible(true);
        commentTextArea.setEditable(false);

        if (selectedLectureBooking.getBookingStatus().equals(STATUS_PENDING)) {
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
        roomLabel.setText("Lokale: " + selectedLectureBooking.getLectureRoom().getType().toString());
        customerCommentTextArea.setText(selectedLectureBooking.getComment());
        commentTextArea.setText(selectedLectureBooking.getComment());
    }

    private void showArrangementBookingInformation(ArrangementBooking selectedArrangementBooking) {
        showPendingButtons(selectedArrangementBooking.getBookingStatus());
        showDeleteBookingButton(selectedArrangementBooking.getBookingStatus());

        cityLabel.setVisible(false);
        contactPersonLabel.setVisible(false);
        phoneNumberLabel.setVisible(false);
        emailLabel.setVisible(false);
        eanLabel.setVisible(false);
        guide_lecturerLabel.setVisible(true);
        customerCommentLabel.setVisible(true);
        customerCommentTextArea.setVisible(true);
        customerCommentTextArea.setEditable(false);
        commentLabel.setVisible(true);
        commentTextArea.setVisible(true);
        commentTextArea.setEditable(false);
        roomLabel.setVisible(false);

        if (selectedArrangementBooking.getBookingStatus().equals(STATUS_PENDING)) {
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
        guide_lecturerLabel.setText("Lokale: " + selectedArrangementBooking.getRestaurant().getType().toString());
        customerCommentTextArea.setText(selectedArrangementBooking.getCustomerComment());
        commentTextArea.setText(selectedArrangementBooking.getComment());
    }

    private void showPendingButtons(BookingStatus bookingStatus) {
        if (bookingStatus.equals(STATUS_PENDING)) {
            acceptBookingButton.setVisible(true);
            cancelBookingButton.setVisible(true);
            editBookingButton.setVisible(true);
        } else {
            acceptBookingButton.setVisible(false);
            cancelBookingButton.setVisible(false);
            editBookingButton.setVisible(true);
        }
    }

    private void showDeleteBookingButton(BookingStatus bookingStatus) {
        if (bookingStatus.equals(STATUS_DELETED)) {
            deleteButton.setVisible(true);
        } else deleteButton.setVisible(false);
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

        if (typeOfBooking.equals(BookingType.ALL_BOOKING_TYPES)) {
            listOfContactPersonNames.clear();
            for (Booking temp : listOfAllBookings) {
                listOfContactPersonNames.add(temp.getCustomer().getContactPerson());
                listOfContactPersonNames.add(temp.getCustomer().getEmail());
                if (temp instanceof LectureBooking) {
                    listOfContactPersonNames.add(((LectureBookingCustomer) temp.getCustomer()).getSchoolName());
                    listOfContactPersonNames.add(((LectureBookingCustomer) temp.getCustomer()).getCommune());
                    listOfContactPersonNames.add(((LectureBookingCustomer) temp.getCustomer()).getCity());
                }
            }
        } else if (typeOfBooking.equals(BookingType.ARRANGEMENTBOOKING)) {
            listOfContactPersonNames.clear();
            for (Booking temp : listOfArrangementBookings) {
                listOfContactPersonNames.add(temp.getCustomer().getContactPerson());
                listOfContactPersonNames.add(temp.getCustomer().getEmail());
            }
        } else if (typeOfBooking.equals(BookingType.LECTUREBOOKING)) {
            listOfContactPersonNames.clear();
            for (Booking temp : listOfLectureBookings) {
                listOfContactPersonNames.add(temp.getCustomer().getContactPerson());
                listOfContactPersonNames.add(temp.getCustomer().getEmail());
                listOfContactPersonNames.add(((LectureBookingCustomer) temp.getCustomer()).getSchoolName());
                listOfContactPersonNames.add(((LectureBookingCustomer) temp.getCustomer()).getCommune());
                listOfContactPersonNames.add(((LectureBookingCustomer) temp.getCustomer()).getCity());
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
            controller.setArrTimeHashMap(ArrTimeHashMap);
            controller.setMsc(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
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
            controller.setLecRoomHashMapForCreation(LecRoomHashMap);
            controller.setMsc(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
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
            controller.setLecRoomHashMap(LecRoomHashMap);
            controller.initData();
            controller.setMsc(this);

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
            controller.setBda(bda);
            controller.setArrTimeHashMap(ArrTimeHashMap); //Used in edit arrangement for time check.
            controller.initData();
            controller.setMsc(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
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

    @FXML
    private void showStatisticWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Statistic.fxml"));
            Parent root = loader.load();
            StatisticController controller = loader.getController();

            MenuItem chosenStatistic = (MenuItem) event.getSource();
            String menuItemText = chosenStatistic.getText();

            if (chosenStatistic.equals(chosenMenuesMenuItem)) {
                controller.setListOfBookings(listOfArrangementBookings);
                controller.setSetting(StatisticType.FOOD);
                controller.initData();
            }
            if (chosenStatistic.equals(pupilsAndTeachersMenuItem) || chosenStatistic.equals(choiceOfTopicMenuItem) ||
                    chosenStatistic.equals(gradeMenuItem) || chosenStatistic.equals(municipalityMenuItem)) {
                controller.setListOfBookings((ArrayList<Booking>) listOfLectureBookings.clone());
                switch (menuItemText) {
                    case "Over elever og lærere":
                        controller.setSetting(StatisticType.STUDENTS_AND_TEACHER);
                        break;
                    case "Over emnevalg":
                        controller.setSetting(StatisticType.TOPIC);
                        break;
                    case "Over klassetrin":
                        controller.setSetting(StatisticType.GRADE);
                        break;
                    case "Over Aalborg Kommune":
                        controller.setSetting(StatisticType.MUNICIPALITY);
                        break;
                }
                controller.initData();
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
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
                try {
                    postToCalendar((bookingTableView.getSelectionModel().getSelectedItem()));
                } catch (IOException e) {
                    GeneralController.showAlertBox(Alert.AlertType.WARNING, "Fejl med Google Calendar",
                            "Kontakt IT for at løse problemet.\n " + e.getMessage());
                }
            } else if ((bookingTableView.getSelectionModel().getSelectedItem()).getBookingType() == (BookingType.LECTUREBOOKING)) {
                SendEmail.sendConfirmationEmail((LectureBooking) (bookingTableView.getSelectionModel().getSelectedItem()));
                try {
                    postToCalendar((bookingTableView.getSelectionModel().getSelectedItem()));
                } catch (IOException e) {
                    GeneralController.showAlertBox(Alert.AlertType.WARNING, "Fejl med Google Calendar",
                            "Kontakt IT for at løse problemet..\n " + e.getMessage());
                }
            }
        }
        acceptSelectedBooking();
        removeBookingFromTableView();
        bookingTableView.getSelectionModel().select(null);
    }

    private void cancelBookingDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Vil du afvise bookingen?");
        alert.setContentText("Handlingen vil flytte bookingen til kategorien 'Slettet'");

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

    private void moveConductedBookingToArchived() {
        for (Booking temp : listOfNonArchivedOrDeletedBookings) {
            int time = (int) (Duration.between(LocalDateTime.now(), temp.getDateTime()).toDays()) + 1;
            if (time <= 0 && (temp.getBookingType().equals(BookingType.LECTUREBOOKING))) {
                try {
                    temp.setBookingStatus(BookingStatus.STATUS_ARCHIVED);
                    bda.editLecBook((LectureBooking) temp);
                } catch (SQLException e) {
                    try {
                        bda = BookingDataAccessor.connect();
                    } catch (SQLException | ClassNotFoundException e1) {
                        System.out.println("Internet issues?");
                    }
                }
            } else if (time <= 0 && (temp.getBookingType().equals(BookingType.ARRANGEMENTBOOKING))) {
                try {
                    temp.setBookingStatus(BookingStatus.STATUS_ARCHIVED);
                    bda.editArrBook((ArrangementBooking) temp);
                } catch (SQLException e) {
                    try {
                        bda = BookingDataAccessor.connect();
                    } catch (SQLException | ClassNotFoundException e1) {
                        System.out.println("Internet issues?");
                    }
                }
            }
        }
    }

    public TableView<Booking> getBookingTableView() {
        return bookingTableView;
    }

    public static void cantConnect(){
        Alert connectionAlert = new Alert(Alert.AlertType.WARNING);
        connectionAlert.setHeaderText("Forbindelsesfejl");
        connectionAlert.setContentText("Kan ikke oprette forbindelse til databasen. Tjek din internetforbindelse eller prøv igen senere");

        Optional<ButtonType> connectionAlertChoice = connectionAlert.showAndWait();
    }
}