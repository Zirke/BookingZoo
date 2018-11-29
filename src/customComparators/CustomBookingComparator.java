package customComparators;

import bookings.Booking;

import java.util.Comparator;

public class CustomBookingComparator implements Comparator<Booking> {
    @Override
    public int compare(Booking o1, Booking o2) {
        String bstatus1 = o1.getBookingStatus().toString();
        String bstatus2 = o2.getBookingStatus().toString();
        int result = bstatus1.compareTo(bstatus2);
        if (result != 0) {
            return result;
        }
        int result1 = o1.getBookingType().toString().compareTo(o2.getBookingType().toString());
        if (result1 != 0) {
            return result1;
        }
        return o1.getDateTime().compareTo(o2.getDateTime());
    }
}