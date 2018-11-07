package statistics;

import bookings.ArrangementBooking;
import bookings.Booking;
import bookings.LectureBooking;
import builders.ArrangementBuilder;
import builders.LectureBuilder;
import enums.*;
import org.junit.Test;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class StatisticTest {

    @Test
    public void amountOfSchoolFromAalborgMunicipality() {
        Statistic tempStatistic = new Statistic();
        LectureBuilder lectureBuilder = new LectureBuilder();
        LectureBuilder lectureBuilder1 = new LectureBuilder();
        LectureBooking useCase, useCase1;

        ArrayList<Booking> BookingsArrayList = new ArrayList<>();

        lectureBuilder.setBookingType(BookingType.LECTUREBOOKING)
                .setCustomer("contactperson", "123324234", "mail@mail.com","generic school", 9000,
                        "Aalborg", "Ja", "45604965",456989486)
                .setParticipants(200)
                .setNoOfTeachers(2);
        useCase = lectureBuilder.build();

        lectureBuilder1.setBookingType(BookingType.LECTUREBOOKING)
                .setCustomer("contactperson1", "12323444","mail1@mail.com","generic name 2", 8000,
                        "Hellerup", "Nej", "984598435", 984596866)
                .setParticipants(10)
                .setNoOfTeachers(1);
        useCase1 = lectureBuilder1.build();

        BookingsArrayList.add(useCase);
        BookingsArrayList.add(useCase1);

        assertEquals(202,tempStatistic.amountOfSchoolFromAalborgMunicipality(BookingsArrayList));

    }

    @Test
    public void amountOfStudentsFromSchools() {
        Statistic tempStatistic = new Statistic();
        LectureBuilder lectureBuilder = new LectureBuilder();
        LectureBuilder lectureBuilder1 = new LectureBuilder();
        LectureBooking useCase, useCase1;

        ArrayList<Booking> BookingsArrayList = new ArrayList<>();

        lectureBuilder.setBookingType(BookingType.LECTUREBOOKING)
                .setCustomer("contactperson", "123324234", "mail@mail.com","generic school", 9000,
                        "Aalborg", "Ja", "45604965",456989486)
                .setParticipants(200)
                .setNoOfTeachers(2);
        useCase = lectureBuilder.build();

        lectureBuilder1.setBookingType(BookingType.LECTUREBOOKING)
                .setCustomer("contactperson1", "12323444","mail1@mail.com","generic name 2", 8000,
                        "Hellerup", "Nej", "984598435", 984596866)
                .setParticipants(10)
                .setNoOfTeachers(1);
        useCase1 = lectureBuilder1.build();

        BookingsArrayList.add(useCase);
        BookingsArrayList.add(useCase1);

        assertEquals(210, tempStatistic.amountOfStudentsFromSchools(BookingsArrayList));
    }

    @Test
    public void amountOfTeachers() {
        Statistic tempStatistic = new Statistic();
        LectureBuilder lectureBuilder = new LectureBuilder();
        LectureBuilder lectureBuilder1 = new LectureBuilder();
        LectureBooking useCase, useCase1;

        ArrayList<Booking> BookingsArrayList = new ArrayList<>();

        lectureBuilder.setBookingType(BookingType.LECTUREBOOKING)
                .setCustomer("contactperson", "123324234", "mail@mail.com","generic school", 9000,
                        "Aalborg", "Ja", "45604965",456989486)
                .setParticipants(200)
                .setNoOfTeachers(2);
        useCase = lectureBuilder.build();

        lectureBuilder1.setBookingType(BookingType.LECTUREBOOKING)
                .setCustomer("contactperson1", "12323444","mail1@mail.com","generic name 2", 8000,
                        "Hellerup", "Nej", "984598435", 984596866)
                .setParticipants(10)
                .setNoOfTeachers(1);
        useCase1 = lectureBuilder1.build();

        BookingsArrayList.add(useCase);
        BookingsArrayList.add(useCase1);

        assertEquals(3, tempStatistic.amountOfTeachers(BookingsArrayList));
    }

    @Test
    public void amountStudentsInGrade() {
        Statistic tempStatistic = new Statistic();
        LectureBuilder lectureBuilder = new LectureBuilder();
        LectureBuilder lectureBuilder1 = new LectureBuilder();
        LectureBuilder lectureBuilder2 = new LectureBuilder();
        LectureBooking useCase, useCase1, useCase2;

        ArrayList<Booking> BookingArrayList = new ArrayList<>();

        lectureBuilder.setParticipants(25)
                .setGrade(Grade.FIFTH);

        lectureBuilder1.setParticipants(10)
                .setGrade(Grade.FIFTH);

        lectureBuilder2.setParticipants(30)
                .setGrade(Grade.FIRST);

        useCase = lectureBuilder.build();
        useCase1 = lectureBuilder1.build();
        useCase2 = lectureBuilder2.build();

        BookingArrayList.add(useCase);
        BookingArrayList.add(useCase1);
        BookingArrayList.add(useCase2);

        HashMap<Grade, Integer> testMap = tempStatistic.amountStudentsInGrade(BookingArrayList);
        assertTrue(testMap.containsKey(Grade.FIFTH));
        assertEquals(14,testMap.size());
        assertNotNull(testMap);
        assertTrue(testMap.keySet().contains(Grade.TENTH));
        assertEquals(35,testMap.get(Grade.FIFTH).intValue());

    }

    @Test
    public void amountOfArrangementParticipants() {
        Statistic tempStatistic = new Statistic();
        ArrangementBuilder arrangementBuilder = new ArrangementBuilder();
        ArrangementBooking useCase;

        ArrayList<Booking> BookingArrayList = new ArrayList<>();

        arrangementBuilder.setBookingType(BookingType.ARRANGEMENTBOOKING)
                .setParticipants(200);
        useCase = arrangementBuilder.build();

        BookingArrayList.add(useCase);

        assertEquals(200,tempStatistic.amountOfArrangementParticipants(BookingArrayList));

    }

    @Test
    public void amountInGivenMonth() {
        Statistic tempStatistic = new Statistic();
        LectureBuilder lectureBuilder = new LectureBuilder();
        LectureBuilder lectureBuilder1 = new LectureBuilder();
        LectureBuilder lectureBuilder2 = new LectureBuilder();
        LectureBooking useCase, useCase1, useCase2;

        ArrayList<Booking> BookingArrayList = new ArrayList<>();
        ArrayList<Booking> BookingTeacherAL = new ArrayList<>();

        lectureBuilder.setBookingType(BookingType.LECTUREBOOKING)
                .setDate(LocalDateTime.of(2018,10,20,14,30))
                .setParticipants(15)
                .setNoOfTeachers(2);

        lectureBuilder1.setBookingType(BookingType.LECTUREBOOKING)
                .setDate(LocalDateTime.of(2018,10,15,20,30))
                .setParticipants(30);

        lectureBuilder2.setBookingType(BookingType.LECTUREBOOKING)
                .setDate(LocalDateTime.of(2018,11,10,14,30))
                .setNoOfTeachers(10);

        useCase = lectureBuilder.build();
        useCase1 = lectureBuilder1.build();
        useCase2 = lectureBuilder2.build();

        BookingArrayList.add(useCase);
        BookingArrayList.add(useCase1);
        BookingTeacherAL.add(useCase2);

        assertEquals(47,tempStatistic.amountInGivenMonth(10,2018,BookingArrayList));
        assertEquals(10,tempStatistic.amountInGivenMonth(11,2018,BookingTeacherAL));
    }

    @Test
    public void amountOfChosenCategory() {
        Statistic tempStatistic = new Statistic();
        LectureBuilder lectureBuilder = new LectureBuilder();
        LectureBuilder lectureBuilder1 = new LectureBuilder();
        LectureBuilder lectureBuilder2 = new LectureBuilder();
        LectureBooking useCase, useCase1, useCase2;

        ArrayList<Booking> BookingArrayList = new ArrayList<>();

        lectureBuilder.setBookingType(BookingType.LECTUREBOOKING)
                .setChoiceOfTopic(ChoiceOfTopic.AFRIKAS_SAVANNER)
                .setNoOfTeams(5);

        lectureBuilder1.setBookingType(BookingType.LECTUREBOOKING)
                .setChoiceOfTopic(ChoiceOfTopic.AFRIKAS_SAVANNER)
                .setNoOfTeams(2);

        lectureBuilder2.setBookingType(BookingType.LECTUREBOOKING)
                .setChoiceOfTopic(ChoiceOfTopic.DYR_DERHJEMME)
                .setNoOfTeams(9);

        useCase = lectureBuilder.build();
        useCase1 = lectureBuilder1.build();
        useCase2 = lectureBuilder2.build();

        BookingArrayList.add(useCase);
        BookingArrayList.add(useCase1);
        BookingArrayList.add(useCase2);

        assertEquals(7,tempStatistic.amountOfChosenCategory(ChoiceOfTopic.AFRIKAS_SAVANNER,BookingArrayList));
        assertEquals(9,tempStatistic.amountOfChosenCategory(ChoiceOfTopic.DYR_DERHJEMME,BookingArrayList));
    }
}