package Util;

import java.time.LocalDate;

public class DateUtil {
    public static boolean isAnniversaryPeriod() {
        LocalDate today = LocalDate.now();
        LocalDate start = LocalDate.of(today.getYear(), 6, 1);
        LocalDate end = LocalDate.of(today.getYear(), 6, 30);
        return !today.isBefore(start) && !today.isAfter(end);
    }
}
