package util;

import java.util.Arrays;
import java.util.List;

public class ValidationUtil {

    private static final List<String> VALID_DAYS = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

    //Check if email is valid
    public static boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    //Check if confirmation code is 6 digits
    public static boolean validateConfirmationCode(String code) {
        if (code == null || code.length() != 6) {
            return false;
        }
        for (char c : code.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    //Check if title is non-empty and =< 30 chars
    public static boolean validateProjectTitle(String title) {
        return title != null && !title.trim().isEmpty() && title.length() <= 30;
    }

    //Check if location is non-empty and ≤ 30 chars
    public static boolean validateProjectLocation(String location) {
        return location != null && !location.trim().isEmpty() && location.length() <= 30;
    }


    //Check if day is one of Mon–Sun
    public static boolean validateDay(String day) {
        return day != null && VALID_DAYS.contains(day);
    }

    //Check if hourly value is between 1–100
    public static boolean validateHourlyValue(int value) {
        return value >= 1 && value <= 100;
    }

    //Check if total slots are between 1–100
    public static boolean validateTotalSlots(int slots) {
        return slots >= 1 && slots <= 100;
    }

    //Check if hours per slot are between 1–3
    public static boolean validateHoursPerSlot(int hours) {
        return hours >= 1 && hours <= 3;
    }

    //Check if slots per project are between 1–3
    public static boolean validateSlotsPerProject(int slots) {
        return slots >= 1 && slots <= 3;
    }
}
