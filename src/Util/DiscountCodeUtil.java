package Util;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DiscountCodeUtil {
    public static boolean isValidFitFuelPeriod() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(LocalDate.of(2025, 5, 10)) &&
                !today.isAfter(LocalDate.of(2025, 5, 20));
    }

    public static boolean isValidCode(String code) {
        return code.equalsIgnoreCase("FITFUEL") && isValidFitFuelPeriod();
    }

    public static BigDecimal getDiscountRate(String code) {
        if (code.equalsIgnoreCase("FITFUEL")) return new BigDecimal("0.75");
        return BigDecimal.ONE;
    }
}
