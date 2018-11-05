package bookings;

import enums.LecturerStatus;

public class Lecturer {
    private String lecturerName;
    private LecturerStatus lecturerStatus;

    public Lecturer(String lecturerName, LecturerStatus lecturerStatus) {
        this.lecturerName = lecturerName;
        this.lecturerStatus = lecturerStatus;
    }

    public Lecturer(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public Lecturer() {
    }

    @Override
    public String toString() {
        return lecturerName;
    }
}
