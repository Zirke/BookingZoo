package customComparators;

import java.util.Comparator;

public class CustomBookingComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        if (o1.equals(o2)) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return o1.compareTo(o2);
    }
}

