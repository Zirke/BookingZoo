package customComparators;

import bookings.Booking;

import java.util.Comparator;

public class CustomBookingComparator implements Comparator<Booking> {
    @Override
    public int compare(Booking o1, Booking o2) {
        String bstatus1 = o1.getBookingStatus().toString();
        String bstatus2 = o2.getBookingStatus().toString();
        return bstatus1.compareTo(bstatus2);
    }
}

