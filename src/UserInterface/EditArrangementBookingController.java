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

import java.sql.SQLException;
import java.util.Optional;

public class EditArrangementBookingController {
    private ArrangementBooking selectedArrangementBooking;

    public void setSelectedArrangementBooking(ArrangementBooking selectedArrangementBooking) {
        this.selectedArrangementBooking = selectedArrangementBooking;
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

    public void initialize() throws SQLException, ClassNotFoundException {

        BookingDataAccessor bda = new BookingDataAccessor(
                "org.postgresql.Driver",
                "jdbc:postgresql://packy.db.elephantsql.com/jyjczxth",
                "jyjczxth",
                "nw51BNKhctporjIFT5Qhhm72jwGVJK95");

        categoryChoiceBox.getItems().addAll("Afventende","Aktiv","Færdig","Arkiveret");

        saveAndCloseButton.setOnMouseClicked(e -> {
            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
            alert2.setContentText("Er den indtastede information korrekt?");

            Optional<ButtonType> alertChoice2 = alert2.showAndWait();

            if (alertChoice2.get() == ButtonType.OK) {
                try {
                    bda.editArrBook(overwriteSelectedArrangementBooking());
                    closeWindow();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

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
        datePicker.setValue(selectedArrangementBooking.getDate());

        if (selectedArrangementBooking.getTime().equals("10:00 - 12:00")) {
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

    private ArrangementBooking overwriteSelectedArrangementBooking() {
        BookingStatus statusChoice;
        statusChoice = BookingStatus.statusChosen(categoryChoiceBox.getSelectionModel().getSelectedItem().toString());
        selectedArrangementBooking.setBookingStatus(statusChoice);
        selectedArrangementBooking.setDate(datePicker.getValue());
        RadioButton selectedTimeSlot = (RadioButton) timeGroup.getSelectedToggle();
        selectedArrangementBooking.setTime(selectedTimeSlot.getText());
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
