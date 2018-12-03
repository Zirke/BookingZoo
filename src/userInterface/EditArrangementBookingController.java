package userInterface;

import bookings.ArrangementBooking;
import bookings.ArrangementTimeChecker;
import bookings.BookingDataAccessor;
import bookings.FoodOrder;
import customers.Customer;
import enums.BookingStatus;
import enums.ChoiceOfMenu;
import enums.FacilityState;
import enums.RestaurantType;
import facilities.Restaurant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Optional;

import static userInterface.GeneralController.textfieldWithOnlyNumbers;

public class EditArrangementBookingController {
    private BookingDataAccessor bda;
    private MainScreenController msc;
    private ArrangementBooking selectedArrangementBooking;
    private HashMap<LocalDateTime, ArrangementBooking> ArrTimeHashMap;

    void setSelectedArrangementBooking(ArrangementBooking selectedArrangementBooking) {
        this.selectedArrangementBooking = selectedArrangementBooking;
    }

    void setBda(BookingDataAccessor bda) {
        this.bda = bda;
    }

    void setMsc(MainScreenController msc) {
        this.msc = msc;
    }

    void setArrTimeHashMap(HashMap<LocalDateTime, ArrangementBooking> arrTimeHashMap) {
        ArrTimeHashMap = arrTimeHashMap;
    }

    @FXML
    private DatePicker datePicker;
    @FXML
    private RadioButton timeOneRadioButton, timeTwoRadioButton, participantYesRadioButton, participantNoRadioButton,
            noFoodRadioButton, menuOneRadioButton, menuTwoRadioButton, menuThreeRadioButton, menuFourRadioButton;
    @FXML
    private ToggleGroup timeGroup, participantGroup, menuGroup;
    @FXML
    private TextField noOfChildrenTextField, childNameTextField, childAgeTextField, contactPersonTextField,
            phoneNumberTextField, emailTextField, guideTextField;
    @FXML
    private ChoiceBox categoryChoiceBox, restaurantChoiceBox;
    @FXML
    private Button cancelButton, saveAndCloseButton;
    @FXML
    private TextArea customerCommentTextArea, commentTextArea;

    public void initialize() {

        categoryChoiceBox.getItems().addAll("Aktiv", "Færdig", "Arkiveret", "Slettet");

        for (RestaurantType i : RestaurantType.values()) {
            restaurantChoiceBox.getItems().add(i.toString());
        }

        saveButtonPress();

        textfieldWithOnlyNumbers(noOfChildrenTextField);
        textfieldWithOnlyNumbers(childAgeTextField);

        cancelButton.setOnMouseClicked(e -> closeWindow());
    }

    void initData() {
        datePicker.setValue(selectedArrangementBooking.getDateTime().toLocalDate());

        if (selectedArrangementBooking.getDateTime().getHour() == 10) {
            timeOneRadioButton.setSelected(true);
        } else {
            timeTwoRadioButton.setSelected(true);
        }

        noOfChildrenTextField.setText(String.valueOf(selectedArrangementBooking.getParticipants()));
        childNameTextField.setText(selectedArrangementBooking.getBirthdayChildName());
        childAgeTextField.setText(String.valueOf(selectedArrangementBooking.getBirthdayChildAge()));

        Customer temp = selectedArrangementBooking.getCustomer();
        contactPersonTextField.setText(temp.getContactPerson());
        phoneNumberTextField.setText(String.valueOf(temp.getPhoneNumber()));
        emailTextField.setText(temp.getEmail());
        guideTextField.setText(selectedArrangementBooking.getGuide());
        categoryChoiceBox.setValue(selectedArrangementBooking.getBookingStatus().toString());

        if (selectedArrangementBooking.getFormerParticipant().equals("Ja")) {
            participantYesRadioButton.setSelected(true);
        } else {
            participantNoRadioButton.setSelected(true);
        }
        switch (selectedArrangementBooking.getMenuChosen().toString()) {
            case "Ingen mad":
                noFoodRadioButton.setSelected(true);
                break;
            case "Kakao, boller og kage":
                menuOneRadioButton.setSelected(true);
                break;
            case "Frikadeller og dessert":
                menuTwoRadioButton.setSelected(true);
                break;
            case "Pasta m. kødsovs og dessert":
                menuThreeRadioButton.setSelected(true);
                break;
            case "Nuggets og pommes frites":
                menuFourRadioButton.setSelected(true);
                break;
        }
        restaurantChoiceBox.setValue(selectedArrangementBooking.getRestaurant().getType().toString());
        customerCommentTextArea.setText(selectedArrangementBooking.getCustomerComment());
        commentTextArea.setText(selectedArrangementBooking.getComment());
    }

    private void saveButtonPress() {
        saveAndCloseButton.setOnMouseClicked(e -> {
            if (datePicker.getValue() == null || !timeGroup.getSelectedToggle().isSelected() || noOfChildrenTextField.getText().isEmpty() || childNameTextField.getText().isEmpty() ||
                    childAgeTextField.getText().isEmpty() || contactPersonTextField.getText().isEmpty() || phoneNumberTextField.getText().isEmpty() || emailTextField.getText().isEmpty() ||
                    guideTextField.getText().isEmpty() || !participantGroup.getSelectedToggle().isSelected() || !menuGroup.getSelectedToggle().isSelected()) {
                GeneralController.showAlertBox(Alert.AlertType.WARNING, "Tjek alle felter", "Et eller flere felter mangler input");
            } else {
                Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
                alert2.setContentText("Er den indtastede information korrekt?");

                Optional<ButtonType> alertChoice2 = alert2.showAndWait();

                ArrangementBooking t = overwriteSelectedArrangementBooking();
                if(t != null) {
                    if (alertChoice2.get() == ButtonType.OK) {
                        try {
                            closeWindow();
                            bda.editArrBook(t);
                            msc.refetchBookingsFromDataBase();
                            msc.getBookingTableView().getSelectionModel().select(null);
                            msc.displayInformationOfSelectedBooking(msc.getBookingTableView());
                        } catch (SQLException e1) {
                            try {
                                bda = BookingDataAccessor.connect();
                            } catch (SQLException | ClassNotFoundException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    private ArrangementBooking overwriteSelectedArrangementBooking() {
        BookingStatus statusChoice = BookingStatus.statusChosen(categoryChoiceBox.getSelectionModel().getSelectedItem().toString());
        selectedArrangementBooking.setBookingStatus(statusChoice);

        RadioButton selectedTimeBtn = (RadioButton) timeGroup.getSelectedToggle();
        LocalTime tempTime;
        if (selectedTimeBtn.getText().equals("10:00 - 12:00")) {
            tempTime = LocalTime.of(10, 00, 00);
        } else {
            tempTime = LocalTime.of(12, 30, 00);
        }
        LocalDate tempDate = datePicker.getValue();
        LocalDateTime date = LocalDateTime.of(tempDate, tempTime);
        selectedArrangementBooking.setDateTime(date);
        selectedArrangementBooking.setParticipants(Integer.parseInt(noOfChildrenTextField.getText()));
        selectedArrangementBooking.setCustomerComment(customerCommentTextArea.getText());
        selectedArrangementBooking.setComment(commentTextArea.getText());
        RadioButton menuChoiceButton = (RadioButton) menuGroup.getSelectedToggle();
        ChoiceOfMenu menuChoice = ChoiceOfMenu.getChoiceOfMenu(menuChoiceButton);
        selectedArrangementBooking.setMenuChosen(new FoodOrder(menuChoice));
        selectedArrangementBooking.setRestaurant(new Restaurant(FacilityState.OCCUPIED));
        selectedArrangementBooking.setBirthdayChildName(childNameTextField.getText());
        selectedArrangementBooking.setBirthdayChildAge(Integer.parseInt(childAgeTextField.getText()));
        RadioButton formerParticipant = (RadioButton) participantGroup.getSelectedToggle();
        selectedArrangementBooking.setFormerParticipant(formerParticipant.getText());
        selectedArrangementBooking.setGuide(guideTextField.getText());
        Customer temp = new Customer(contactPersonTextField.getText(), phoneNumberTextField.getText(),
                emailTextField.getText());
        selectedArrangementBooking.setCustomer(temp);
        selectedArrangementBooking.getRestaurant().setType(RestaurantType.roomTypeChoice(restaurantChoiceBox.getSelectionModel().getSelectedItem().toString()));

        /* Arrangement booking checker*/
        ArrangementTimeChecker checker = new ArrangementTimeChecker(ArrTimeHashMap, selectedArrangementBooking);
        if(checker.isChosenTimeOccupied(date)){
            /*Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("For den valgte tidsrum og dato er der allerede \n2 børnefødselsdage");

            Optional<ButtonType> alertChoice = alert.showAndWait();
            if (alertChoice.isPresent() && alertChoice.get() == ButtonType.OK) {
                return null;
            }*/
            return checker.alert();
        }
        return selectedArrangementBooking;
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
