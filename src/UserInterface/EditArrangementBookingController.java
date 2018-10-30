package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.BookingDataAccessor;
import Bookings.FoodOrder;
import Customers.Customer;
import enums.BookingStatus;
import enums.ChoiceOfMenu;
import enums.FacilityState;
import facilities.Restaurant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class EditArrangementBookingController {
    private BookingDataAccessor bda;
    private ArrangementBooking selectedArrangementBooking;

    void setSelectedArrangementBooking(ArrangementBooking selectedArrangementBooking) {
        this.selectedArrangementBooking = selectedArrangementBooking;
    }

    void setBda(BookingDataAccessor bda) {
        this.bda = bda;
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
    private ChoiceBox categoryChoiceBox;
    @FXML
    private Button cancelButton, saveAndCloseButton;
    @FXML
    private TextArea customerCommentTextArea, commentTextArea;

    public void initialize() {

        categoryChoiceBox.getItems().addAll("Afventende", "Aktiv", "Færdig", "Arkiveret", "Slettet");

        saveButtonPress();

        noOfChildrenTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                noOfChildrenTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        childAgeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                childAgeTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
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
        customerCommentTextArea.setText(selectedArrangementBooking.getCustomerComment());
        commentTextArea.setText(selectedArrangementBooking.getComment());

    }

    private void saveButtonPress(){
        saveAndCloseButton.setOnMouseClicked(e -> {
            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
            alert2.setContentText("Er den indtastede information korrekt?");

            Optional<ButtonType> alertChoice2 = alert2.showAndWait();

            if (alertChoice2.get() == ButtonType.OK) {
                try {
                    bda.editArrBook(overwriteSelectedArrangementBooking());
                    closeWindow();
                } catch (SQLException | IOException | GeneralSecurityException | ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private ArrangementBooking overwriteSelectedArrangementBooking() {
        BookingStatus statusChoice;
        statusChoice = BookingStatus.statusChosen(categoryChoiceBox.getSelectionModel().getSelectedItem().toString());
        selectedArrangementBooking.setBookingStatus(statusChoice);


        RadioButton selectedTimeBtn = (RadioButton) timeGroup.getSelectedToggle();
        LocalTime tempTime;
        if(selectedTimeBtn.getText().equals("10:00 - 12:00")){
            tempTime = LocalTime.of(10,00,00);
        } else{
            tempTime = LocalTime.of(12,30,00);
        }
        LocalDate tempDate = datePicker.getValue();
        LocalDateTime date = LocalDateTime.of(tempDate,tempTime);
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


        return selectedArrangementBooking;
    }


    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
