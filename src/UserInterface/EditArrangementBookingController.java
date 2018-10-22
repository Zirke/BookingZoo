package UserInterface;

import Bookings.ArrangementBooking;
import Bookings.FoodOrder;
import Customers.Customer;
import enums.ChoiceOfMenu;
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
        FoodOrder one = new FoodOrder(ChoiceOfMenu.NO_FOOD);
        FoodOrder two = new FoodOrder(ChoiceOfMenu.MENU_ONE);
        FoodOrder three = new FoodOrder(ChoiceOfMenu.MENU_TWO);
        FoodOrder four = new FoodOrder(ChoiceOfMenu.MENU_THREE);
        FoodOrder five = new FoodOrder(ChoiceOfMenu.MENU_FOUR);
        if (selectedArrangementBooking.getMenuChosen().equals(one)) {
            noFoodRadioButton.setSelected(true);
        } else if (selectedArrangementBooking.getMenuChosen().equals(two)) {
            menuOneRadioButton.setSelected(true);
        } else if (selectedArrangementBooking.getMenuChosen().equals(three)) {
            menuTwoRadioButton.setSelected(true);
        } else if (selectedArrangementBooking.getMenuChosen().equals(four)) {
            menuThreeRadioButton.setSelected(true);
        } else if (selectedArrangementBooking.getMenuChosen().equals(five)) {
            menuFourRadioButton.setSelected(true);
        }
    }
}
