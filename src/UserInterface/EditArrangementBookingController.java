package UserInterface;

import Bookings.ArrangementBooking;
import Customers.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private TextArea customerCommentTextArea, commentTextArea;

    public void initialize() {

    }

    void initData() {
        datePicker.setValue(selectedArrangementBooking.getDate());

        if (selectedArrangementBooking.getTime().equals("10:00 - 12:00")) {
            timeOneRadioButton.setSelected(true);
        } else {
            timeTwoRadioButton.setSelected(false);
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
    }
}
