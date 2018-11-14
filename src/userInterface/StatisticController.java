package userInterface;

import bookings.ArrangementBooking;
import bookings.Booking;
import bookings.LectureBooking;
import enums.ChoiceOfMenu;
import enums.ChoiceOfTopic;
import enums.Grade;
import enums.StatisticType;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static statistics.Statistic.*;

public class StatisticController {
    public CheckComboBox gradeComboBox;
    private ArrayList<Booking> listOfBookings = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate finishDate;
    private StatisticType setting;

    public HBox hboxWithCharts;
    public VBox dataVBox;
    public DatePicker startPicker;
    public DatePicker finishPicker;
    public Button calculateButton;
    public Label headlineLabel;
    @FXML
    private Button returnButton, printButton;
    private Boolean calculateAlreadePressed = false;

    public StatisticType getSetting() {
        return setting;
    }

    void setSetting(StatisticType setting) {
        this.setting = setting;
    }

    void setListOfBookings(ArrayList<Booking> listOfBookings) {
        this.listOfBookings.addAll(listOfBookings);
    }

    void initData() {
        gradeComboBox.setVisible(false);
        headlineLabel.setText(setting.toString());
        calculateButtonPressed();
        printButtonPressed();

        switch (setting){
            case STUDENTS_AND_TEACHER:{
                calculateButtonPressed();
            }break;
            case GRADE:{
                gradeSceneGeneration();
            }break;
        }

        returnButton.setOnMouseClicked(e ->{
            Stage stage = ((Stage)returnButton.getScene().getWindow());
            stage.close();
        });
    }
    private void printButtonPressed(){
        printButton.setOnMouseClicked(e -> saveChartsAsPicture(hboxWithCharts));
    }

    private void calculateButtonPressed(){
        calculateButton.setOnMouseClicked(e ->{
            startDate = startPicker.getValue();
            finishDate = finishPicker.getValue();
            if(listOfBookings.get(0) instanceof LectureBooking) {
                lectureBookingInSelectedInterval();
            }else{
                arrangementBookingInSelectedInterval();
            }
            if(calculateAlreadePressed){
                clearWindow();
            }
            calculateAlreadePressed = true;
            for (Node i : dataVBox.getChildren()) {
                i.setVisible(true);
            }
            switch (setting){
                case STUDENTS_AND_TEACHER: labelGenerationForTeachersAndStudents();break;
                case TOPIC: labelGenerationForTopic();break;
                case GRADE: labelGenerationForGrade();break;
                case MUNICIPALITY: labelGenerationMunicipality();break;
                case FOOD: labelGenerationForMenu();break;
            }

        });
    }

    private void lectureBookingInSelectedInterval(){
        ArrayList<Booking> removeList = new ArrayList<>();
        for(Booking i : listOfBookings){
            LectureBooking x = (LectureBooking) i;
            Boolean isAfterFinishDate = x.getDateTime().toLocalDate().isAfter(finishDate);
            Boolean isBeforeStartDate = x.getDateTime().toLocalDate().isBefore(startDate);
            if(isAfterFinishDate || isBeforeStartDate){
                removeList.add(i);
            }
        }
        listOfBookings.removeAll(removeList);

        if (listOfBookings.size() == 0) {
            GeneralController.showAlertBox(Alert.AlertType.INFORMATION, "Prøv igen",
                    "Der er ingen bookings inden for det valgte interval");
        }
    }

    private void labelGenerationForTeachersAndStudents(){
        setSceneToMaxHeight();

        int count = 0;
        int studentCount =amountOfStudentsFromSchools(listOfBookings);
        if(studentCount > 0) {
            Label amountOfStudent = new Label();
            amountOfStudent.setText("Antal elever: " + studentCount);
            amountOfStudent.setFont(Font.font(14));
            dataVBox.getChildren().add(amountOfStudent);
        }
        int teacherCount = amountOfTeachers(listOfBookings);
        if(teacherCount > 0){
            Label amountOfTeachers = new Label("Antal lærere: " + teacherCount);
            amountOfTeachers.setFont(Font.font(14));
            dataVBox.getChildren().add(amountOfTeachers);
        }

        final CategoryAxis xStudent = new CategoryAxis();
        final NumberAxis yStudent = new NumberAxis();
        xStudent.setLabel("Dato");
        yStudent.setLabel("Antal elever");
        final LineChart<String,Number> lineChart =
                new LineChart<String,Number>(xStudent,yStudent);

        XYChart.Series studentSeries = new XYChart.Series();
        ArrayList<Booking> temp = new ArrayList<>();
        lineChart.setLegendVisible(false);
        HashMap<String, Integer> studentMap = hashMapGenerationForStudent();
        for(String i : studentMap.keySet()){
            studentSeries.getData().add(new XYChart.Data(i, studentMap.get(i)));
            count += studentMap.get(i);
        }
        assert count == studentCount;
        LineChart<String,Number> teacherChart = lineChartForTeachers();
        lineChart.getData().add(studentSeries);
        VBox charts = new VBox();

        charts.getChildren().addAll(lineChart,teacherChart);
        hboxWithCharts.getChildren().add(charts);

    }

    private void setSceneToMaxHeight(){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        hboxWithCharts.getScene().getWindow().setY(bounds.getMinY());
        hboxWithCharts.getScene().getWindow().setHeight(bounds.getHeight());
        returnButton.setLayoutY(bounds.getMinY() - 10);
    }

    private void setSceneForTopic(){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        hboxWithCharts.getScene().getWindow().setY(bounds.getMinY());
        double formerX = hboxWithCharts.getScene().getWindow().getX();
        double formerWidth = hboxWithCharts.getScene().getWindow().getWidth();
        hboxWithCharts.getScene().getWindow().setX(formerX - 100);

        hboxWithCharts.getScene().getWindow().setWidth(formerWidth + 200);
        hboxWithCharts.getScene().getWindow().setHeight(bounds.getHeight());
        returnButton.setLayoutY(bounds.getMinY() - 10);
    }

    private HashMap<String, Integer> hashMapGenerationForStudent(){
        HashMap<String, Integer> temp = new HashMap<>();
        ArrayList<Booking> j = new ArrayList<>();
        for(Booking i : listOfBookings){
            j.add(i);
            if(temp.containsKey(i.getDateTime().toLocalDate().toString())){
                int t = temp.get(i.getDateTime().toLocalDate().toString());
                temp.put(i.getDateTime().toLocalDate().toString(), t + amountOfStudentsFromSchools(j));
            }else{
                temp.put(i.getDateTime().toLocalDate().toString(), amountOfStudentsFromSchools(j));
            }
            j.remove(i);
        }
        return temp;
    }

    private LineChart<String,Number> lineChartForTeachers(){
        final CategoryAxis xTeacher = new CategoryAxis();
        final NumberAxis yTeacher = new NumberAxis();
        xTeacher.setLabel("Dato");
        yTeacher.setLabel("Antal lærere");
        final LineChart<String,Number> lineChart =
                new LineChart<String,Number>(xTeacher,yTeacher);

        XYChart.Series teacherSeries = new XYChart.Series();
        lineChart.setLegendVisible(false);
        HashMap<String,Integer> teacherMap = hashMapGenerationForTeacher();
        for(String i : teacherMap.keySet()){
            teacherSeries.getData().add(new XYChart.Data(i, teacherMap.get(i)));
        }
        lineChart.getData().addAll(teacherSeries);
        return lineChart;
    }
    private HashMap<String, Integer> hashMapGenerationForTeacher(){
        HashMap<String, Integer> temp = new HashMap<>();
        ArrayList<Booking> j = new ArrayList<>();
        for(Booking i : listOfBookings){
            j.add(i);
            if(temp.containsKey(i.getDateTime().toLocalDate().toString())){
                int t = temp.get(i.getDateTime().toLocalDate().toString());
                temp.put(i.getDateTime().toLocalDate().toString(), t + amountOfTeachers(j));
            }else{
                temp.put(i.getDateTime().toLocalDate().toString(), amountOfTeachers(j));
            }
            j.remove(i);
        }
        return temp;
    }


    private void labelGenerationForTopic(){
        int totalAmount = 0;
        int dyrDerHjemmeTopic = amountOfChosenCategory(ChoiceOfTopic.DYR_DERHJEMME, listOfBookings);
        totalAmount += dyrDerHjemmeTopic;
        labelGeneration(1, "Antal hold i:");
        labelGeneration(dyrDerHjemmeTopic,  ChoiceOfTopic.DYR_DERHJEMME.toString()+": " + dyrDerHjemmeTopic);
        dataVBox.setMinWidth(300);
        int temp = amountOfChosenCategory(ChoiceOfTopic.HVERDAG_ZOO, listOfBookings); totalAmount += temp;
        labelGeneration(temp, ChoiceOfTopic.HVERDAG_ZOO.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.KRYBDYR, listOfBookings); totalAmount += temp;
        labelGeneration(temp, ChoiceOfTopic.KRYBDYR.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.GROENDLANDS_DYR, listOfBookings); totalAmount += temp;
        labelGeneration(temp,  ChoiceOfTopic.GROENDLANDS_DYR.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.AFRIKAS_SAVANNER, listOfBookings); totalAmount += temp;
        labelGeneration(temp,  ChoiceOfTopic.AFRIKAS_SAVANNER.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.AKTIVERINGSVAERKSTED, listOfBookings); totalAmount += temp;
        labelGeneration(temp,  ChoiceOfTopic.AKTIVERINGSVAERKSTED.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.SANSEOPLEVELSER, listOfBookings); totalAmount += temp;
        labelGeneration(temp,  ChoiceOfTopic.SANSEOPLEVELSER.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.DYRS_TILPASNING, listOfBookings); totalAmount += temp;
        labelGeneration(temp,  ChoiceOfTopic.DYRS_TILPASNING.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.EVOLUTION, listOfBookings); totalAmount += temp;
        labelGeneration(temp,  ChoiceOfTopic.EVOLUTION.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.ZOO_SOM_VIRKSOMHED, listOfBookings); totalAmount += temp;
        labelGeneration(temp,  ChoiceOfTopic.ZOO_SOM_VIRKSOMHED.toString()+": " + temp);
        setSceneForTopic();
        topicPieChartGeneration(totalAmount);
    }

    private void labelGeneration(int amount, String labelText){
        if(amount > 0){
            Label amountLabel = new Label(labelText);
            amountLabel.setFont(Font.font(14));
            dataVBox.getChildren().add(amountLabel);

            /*if(labelText.length() > 19){
                Label amountLabel = new Label(labelText.substring(0,20));
                Label amountLabel1 = new Label(labelText.substring(20));
                amountLabel.setFont(Font.font(14));
                amountLabel1.setFont(Font.font(14));
                dataVBox.getChildren().add(amountLabel);
                dataVBox.getChildren().add(amountLabel1);
            }else {
                Label amountLabel = new Label(labelText);
                amountLabel.setFont(Font.font(14));
                dataVBox.getChildren().add(amountLabel);
            }*/
        }
    }

    private void topicPieChartGeneration(int totalAmount){
        ObservableList<PieChart.Data> topicChart = FXCollections.observableArrayList();
        addPieChartData(topicChart, ChoiceOfTopic.DYR_DERHJEMME);
        addPieChartData(topicChart, ChoiceOfTopic.HVERDAG_ZOO);
        addPieChartData(topicChart, ChoiceOfTopic.KRYBDYR);
        addPieChartData(topicChart, ChoiceOfTopic.GROENDLANDS_DYR);
        addPieChartData(topicChart, ChoiceOfTopic.AFRIKAS_SAVANNER);
        addPieChartData(topicChart, ChoiceOfTopic.AKTIVERINGSVAERKSTED);
        addPieChartData(topicChart, ChoiceOfTopic.SANSEOPLEVELSER);
        addPieChartData(topicChart, ChoiceOfTopic.DYRS_TILPASNING);
        addPieChartData(topicChart, ChoiceOfTopic.EVOLUTION);
        addPieChartData(topicChart, ChoiceOfTopic.ZOO_SOM_VIRKSOMHED);

        final PieChart chart = new PieChart(topicChart);

        chart.setTitle("Emnefordeling");

        chart.setLabelsVisible(false);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        chart.getData().forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), " ", String.valueOf(numberFormat.format(((data.getPieValue()/totalAmount)*100))) , " %"
                        )
                )
        );

        hboxWithCharts.getChildren().add(chart);
    }

    private void addPieChartData(ObservableList<PieChart.Data> topicChart, ChoiceOfTopic topic){
        int temp = amountOfChosenCategory(topic, listOfBookings);
        if(temp > 0){
            topicChart.add(new PieChart.Data(topic.toString(), amountOfChosenCategory(topic, listOfBookings)));
        }
    }

    private void clearWindow(){
        Node temp = hboxWithCharts.getChildren().get(0);
        hboxWithCharts.getChildren().clear();
        hboxWithCharts.getChildren().add(temp);

        temp = dataVBox.getChildren().get(0);
        temp.setVisible(false);
        dataVBox.getChildren().clear();
        dataVBox.getChildren().add(temp);
    }

    private void gradeSceneGeneration(){
        gradeComboBox.setVisible(true);
        final ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Vælg Alle");
        for (Grade i : Grade.values()) {
            items.add(i.toString());
            i.toString();
        }
        gradeComboBox.getItems().addAll(items);
    }

    private void labelGenerationForGrade(){
        ObservableList<Integer> chosenItems = gradeComboBox.getCheckModel().getCheckedIndices();
        ArrayList<Grade> grades = new ArrayList<>();

        setSceneToMaxHeight();

        for(Integer i : chosenItems){
            String x = (String)gradeComboBox.getCheckModel().getItem(i);
            if("Vælg Alle".equals(x)){
                grades.clear();
                grades.addAll(Arrays.asList(Grade.values()));
                break;
            }else{
                grades.add(Grade.gradeChosen(x));
            }

        }

        ArrayList<Booking> gradeBookings = arrayListWithOnlyGrades(grades);
        HashMap<Grade, Integer> gradeHashMap = amountStudentsInGrade(gradeBookings);
        labelGeneration(1, "Elever i:");
        for(Grade i : grades){
            String labelText = (i.toString() +" :  " + gradeHashMap.get(i));
            labelGeneration(1, labelText);
        }

        BarChart<String,Number> bc = barChartFOrGrade();
        XYChart.Series series = new XYChart.Series();
        for(Grade i : grades){
            if(gradeHashMap.get(i) > 0) {
                series.getData().add(new XYChart.Data(i.toString(), gradeHashMap.get(i)));
            }
        }
        if(!series.getData().isEmpty()) {
            bc.getData().add(series);
            hboxWithCharts.getChildren().add(bc);
        }

    }

    private ArrayList<Booking> arrayListWithOnlyGrades(ArrayList<Grade> gradeList){
        ArrayList<Booking> gradeBookingList = new ArrayList<>();

        for(Booking i : listOfBookings){
            LectureBooking x = (LectureBooking) i;
            for(Grade j : gradeList){
                if(x.getGrade().equals(j)){
                    gradeBookingList.add(i);
                }
            }
        }

        return gradeBookingList;
    }

    private BarChart<String,Number> barChartFOrGrade(){
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Antal elever i klasser");
        xAxis.setLabel("Klassetrin");
        yAxis.setLabel("Antal elever");

        return bc;
    }

    private void labelGenerationMunicipality(){
        lectureBookingInSelectedInterval();
        setSceneToMaxHeight();
        ArrayList<Booking> fromAalborgMunicipality =(ArrayList<Booking>) listOfBookings.clone();
        int amountFromAalborg = amountOfSchoolFromAalborgMunicipality(fromAalborgMunicipality);
        int totalAmount = 0;
        for(Booking i : listOfBookings){
            totalAmount += i.getParticipants();
        }
        int amountNotAalborg = totalAmount - amountFromAalborg;
        labelGeneration(1, "Antal elever: " + totalAmount);
        labelGeneration(1, "Antal elever fra Aalborg Kommune: " + amountFromAalborg);
        labelGeneration(1, "Antal elever uden for Aalborg Kommune: " + amountNotAalborg);

        ObservableList<PieChart.Data> data =
                FXCollections.observableArrayList(
                        new PieChart.Data("Fra Aalborg Kommune", amountFromAalborg),
                        new PieChart.Data("Ikke Fra Aalborg Kommune", amountNotAalborg));
        final PieChart chart = new PieChart(data);
        chart.setTitle("Fordeling af elever i og uden for Aalborg Kommune");
        chart.setLabelsVisible(false);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        int finalTotalAmount = totalAmount;
        chart.getData().forEach(e ->
                e.nameProperty().bind(
                        Bindings.concat(
                                e.getName(), " ", String.valueOf(numberFormat.format(((e.getPieValue()/ finalTotalAmount)*100))) , " %"
                        )
                )
        );
        dataVBox.setMinWidth(300);
        if(totalAmount != 0){
            hboxWithCharts.getChildren().add(chart);
        }
    }


    private void addPieChartMenuData(ObservableList<PieChart.Data> topicChart, ChoiceOfMenu menu){
        int temp = amountOfMenuChosen(menu, listOfBookings);
        if(temp > 0){
            topicChart.add(new PieChart.Data(menu.toString(), amountOfMenuChosen(menu, listOfBookings)));
        }
    }
    private void menuPieChartGeneration(int totalAmount){
        ObservableList<PieChart.Data> menuChart = FXCollections.observableArrayList();
        addPieChartMenuData(menuChart, ChoiceOfMenu.NO_FOOD);
        addPieChartMenuData(menuChart, ChoiceOfMenu.MENU_ONE);
        addPieChartMenuData(menuChart, ChoiceOfMenu.MENU_TWO);
        addPieChartMenuData(menuChart, ChoiceOfMenu.MENU_THREE);
        addPieChartMenuData(menuChart, ChoiceOfMenu.MENU_FOUR);

        final PieChart chart = new PieChart(menuChart);


        chart.setTitle("Madfordeling");

        chart.setLabelsVisible(false);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        chart.getData().forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), " ", String.valueOf(numberFormat.format(((data.getPieValue()/totalAmount)*100))) , " %"
                        )
                )
        );

        hboxWithCharts.getChildren().add(chart);
        //printButton.setOnMouseClicked(e->saveMenuAsPNG(hboxWithCharts));
    }
    private void labelGenerationForMenu(){
        int totalAmount = 0;
        int noFoodChosen = amountOfMenuChosen(ChoiceOfMenu.NO_FOOD, listOfBookings);
        totalAmount += noFoodChosen;
        labelGeneration(1, "Antal personer har fået:");
        labelGeneration(noFoodChosen,  ChoiceOfMenu.NO_FOOD.toString()+": " + noFoodChosen);
        dataVBox.setMinWidth(300);
        int temp = amountOfMenuChosen(ChoiceOfMenu.MENU_ONE, listOfBookings); totalAmount += temp;
        labelGeneration(temp, ChoiceOfMenu.MENU_ONE.toString()+": " + temp);
        temp = amountOfMenuChosen(ChoiceOfMenu.MENU_TWO, listOfBookings); totalAmount += temp;
        labelGeneration(temp, ChoiceOfMenu.MENU_TWO.toString()+": " + temp);
        temp = amountOfMenuChosen(ChoiceOfMenu.MENU_THREE, listOfBookings); totalAmount += temp;
        labelGeneration(temp,  ChoiceOfMenu.MENU_THREE.toString()+": " + temp);
        temp = amountOfMenuChosen(ChoiceOfMenu.MENU_FOUR, listOfBookings); totalAmount += temp;
        labelGeneration(temp,  ChoiceOfMenu.MENU_FOUR.toString()+": " + temp);
        setSceneForTopic();
        menuPieChartGeneration(totalAmount);
    }

    private void arrangementBookingInSelectedInterval(){
        ArrayList<Booking> removeList = new ArrayList<>();
        for(Booking i : listOfBookings){
            ArrangementBooking x = (ArrangementBooking) i;
            Boolean isAfterFinishDate = x.getDateTime().toLocalDate().isAfter(finishDate);
            Boolean isBeforeStartDate = x.getDateTime().toLocalDate().isBefore(startDate);
            if(isAfterFinishDate || isBeforeStartDate){
                removeList.add(i);
            }
        }
        listOfBookings.removeAll(removeList);

        if (listOfBookings.size() == 0) {
            GeneralController.showAlertBox(Alert.AlertType.INFORMATION, "Prøv igen",
                    "Der er ingen bookings inden for det valgte interval");
        }
    }
    private void saveChartsAsPicture(HBox hboxWithCharts){
        WritableImage image = hboxWithCharts.snapshot(new SnapshotParameters(), null);
        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(imageFilter);
        File file = fileChooser.showSaveDialog(printButton.getScene().getWindow());
        try{
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
