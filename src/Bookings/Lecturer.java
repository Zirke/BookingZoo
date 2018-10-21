package Bookings;

import enums.LecturerStatus;

public class Lecturer {
    private String lecturerName;
    private LecturerStatus lecturerStatus;

    public Lecturer(String lecturerName, LecturerStatus lecturerStatus) {
        this.lecturerName = lecturerName;
        this.lecturerStatus = lecturerStatus;
    }

    public Lecturer() {
    }

    @Override
    public String toString() {
        return lecturerName;
    }
}
