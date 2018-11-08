package userInterface;

import bookings.Booking;
import bookings.LectureBooking;
import enums.ChoiceOfTopic;
import enums.StatisticType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static statistics.Statistic.*;

public class StatisticController {
    private ArrayList<Booking> lectureBookings;
    private LocalDate startDate;
    private LocalDate finishDate;
    private StatisticType setting;

    public HBox hboxWithCharts;
    public VBox dataVBOx;
    public DatePicker startPicker;
    public DatePicker finishPicker;
    public Button calculateButton;
    public Label headlineLabel;
    @FXML
    private Button returnButton;


    public StatisticType getSetting() {
        return setting;
    }

    public void setSetting(StatisticType setting) {
        this.setting = setting;
    }

    void setLectureBookings(ArrayList<Booking> lectureBookings) {
        this.lectureBookings = lectureBookings;
    }

    void initialise(){

        headlineLabel.setText(setting.toString());
        calculateButtonPressed();

        switch (setting){
            case STUDENTS_AND_TEACHER:{
                calculateButtonPressed();
            }break;
            case GRADE:{

            }
        }


        returnButton.setOnMouseClicked(e ->{
            Stage stage = ((Stage)returnButton.getScene().getWindow());
            stage.close();
        });

    }

    private void calculateButtonPressed(){
        calculateButton.setOnMouseClicked(e ->{
            startDate = startPicker.getValue();
            finishDate = finishPicker.getValue();
            lectureBookingInSelectedInterval();
            for(Node i : dataVBOx.getChildren()){
                i.setVisible(true);
            }

            switch (setting){
                case STUDENTS_AND_TEACHER: labelGenerationForTeachersAndStudents();break;
                case TOPIC: labelGenerationForTopic();break;
            }

        });
    }

    private void lectureBookingInSelectedInterval(){
        ArrayList<Booking> removeList = new ArrayList<>();
        for(Booking i : lectureBookings){
            LectureBooking x = (LectureBooking) i;
            Boolean isAfterFinishDate = x.getDateTime().toLocalDate().isAfter(finishDate);
            Boolean isBeforeStartDate = x.getDateTime().toLocalDate().isBefore(startDate);
            if(isAfterFinishDate || isBeforeStartDate){
                removeList.add(i);
            }
        }

        lectureBookings.removeAll(removeList);
    }

    private void labelGenerationForTeachersAndStudents(){
        int count = 0;
        int studentCount =amountOfStudentsFromSchools(lectureBookings);
        if(studentCount > 0) {
            Label amountOfStudent = new Label();
            amountOfStudent.setText("Antal elever: " + studentCount);
            amountOfStudent.setFont(Font.font(14));
            dataVBOx.getChildren().add(amountOfStudent);
        }
        int teacherCount = amountOfTeachers(lectureBookings);
        if(teacherCount > 0){
            Label amountOfTeachers = new Label("Antal lærere: " + teacherCount);
            amountOfTeachers.setFont(Font.font(14));
            dataVBOx.getChildren().add(amountOfTeachers);
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

    private HashMap<String, Integer> hashMapGenerationForStudent(){
        HashMap<String, Integer> temp = new HashMap<>();
        ArrayList<Booking> j = new ArrayList<>();
        for(Booking i : lectureBookings){
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
        for(Booking i : lectureBookings){
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

    //Noget galt med amountOfChosenCategory() metode.
    private void labelGenerationForTopic(){
        int dyrDerHjemmeTopic = amountOfChosenCategory(ChoiceOfTopic.DYR_DERHJEMME, lectureBookings);
        labelGeneration(dyrDerHjemmeTopic, "Antal hold i " + ChoiceOfTopic.DYR_DERHJEMME.toString()+": " + dyrDerHjemmeTopic);

        int temp = amountOfChosenCategory(ChoiceOfTopic.HVERDAG_ZOO, lectureBookings);
        labelGeneration(dyrDerHjemmeTopic, "Antal hold i " + ChoiceOfTopic.HVERDAG_ZOO.toString()+": " + temp);

        temp = amountOfChosenCategory(ChoiceOfTopic.KRYBDYR, lectureBookings);
        labelGeneration(dyrDerHjemmeTopic, "Antal hold i " + ChoiceOfTopic.KRYBDYR.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.GROENDLANDS_DYR, lectureBookings);
        labelGeneration(dyrDerHjemmeTopic, "Antal hold i " + ChoiceOfTopic.GROENDLANDS_DYR.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.AFRIKAS_SAVANNER, lectureBookings);
        labelGeneration(dyrDerHjemmeTopic, "Antal hold i " + ChoiceOfTopic.AFRIKAS_SAVANNER.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.AKTIVERINGSVAERKSTED, lectureBookings);
        labelGeneration(dyrDerHjemmeTopic, "Antal hold i " + ChoiceOfTopic.AKTIVERINGSVAERKSTED.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.SANSEOPLEVELSER, lectureBookings);
        labelGeneration(dyrDerHjemmeTopic, "Antal hold i " + ChoiceOfTopic.SANSEOPLEVELSER.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.DYRS_TILPASNING, lectureBookings);
        labelGeneration(dyrDerHjemmeTopic, "Antal hold i " + ChoiceOfTopic.DYRS_TILPASNING.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.EVOLUTION, lectureBookings);
        labelGeneration(dyrDerHjemmeTopic, "Antal hold i " + ChoiceOfTopic.EVOLUTION.toString()+": " + temp);
        temp = amountOfChosenCategory(ChoiceOfTopic.ZOO_SOM_VIRKSOMHED, lectureBookings);
        labelGeneration(dyrDerHjemmeTopic, "Antal hold i " + ChoiceOfTopic.ZOO_SOM_VIRKSOMHED.toString()+": " + temp);
    }

    private void labelGeneration(int amount, String labelText){
        if(amount > 0){
            Label amountLabel = new Label(labelText);
            amountLabel.setFont(Font.font(14));
            dataVBOx.getChildren().add(amountLabel);
        }
    }

}
