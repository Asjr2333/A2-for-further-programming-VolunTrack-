package util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class DateUtil {

    private static final Map<String, DayOfWeek> DAY_MAP = new HashMap<>();

    static {
        DAY_MAP.put("Mon", DayOfWeek.MONDAY);
        DAY_MAP.put("Tue", DayOfWeek.TUESDAY);
        DAY_MAP.put("Wed", DayOfWeek.WEDNESDAY);
        DAY_MAP.put("Thu", DayOfWeek.THURSDAY);
        DAY_MAP.put("Fri", DayOfWeek.FRIDAY);
        DAY_MAP.put("Sat", DayOfWeek.SATURDAY);
        DAY_MAP.put("Sun", DayOfWeek.SUNDAY);
    }

    // Check if a user can register for a project day
    public static boolean canRegisterForDay(String projectDay) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        DayOfWeek projectDayOfWeek = DAY_MAP.get(projectDay);

        if (projectDayOfWeek == null) {
            return false;
        }

        int todayValue = today.getValue();
        int projectValue = projectDayOfWeek.getValue();

        // Allow registration only for today or future days
        return projectValue >= todayValue;
    }

    // Get all remaining days of the week (today and after)
    public static List<String> getAvailableDays() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        int todayValue = today.getValue();

        List<String> availableDays = new ArrayList<>();
        for (Map.Entry<String, DayOfWeek> entry : DAY_MAP.entrySet()) {
            if (entry.getValue().getValue() >= todayValue) {
                availableDays.add(entry.getKey());
            }
        }
        return availableDays;
    }

    // Convert DayOfWeek to its short string
    public static String getDayOfWeekString(DayOfWeek dayOfWeek) {
        for (Map.Entry<String, DayOfWeek> entry : DAY_MAP.entrySet()) {
            if (entry.getValue() == dayOfWeek) {
                return entry.getKey();
            }
        }
        return "";
    }
}