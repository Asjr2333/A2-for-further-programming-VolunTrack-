package model;

public class Project {
    private int id;
    private String title;
    private String location;
    private String day;
    private int hourlyValue;
    private int registeredSlots;
    private int totalSlots;
    private boolean enabled;

    public Project() {this.enabled = true;}

    public Project(String title, String location, String day, int hourlyValue, int registeredSlots, int totalSlots) {
        this.title = title;
        this.location = location;
        this.day = day;
        this.hourlyValue = hourlyValue;
        this.registeredSlots = registeredSlots;
        this.totalSlots = totalSlots;
        this.enabled = true;

    }

    public Project(int id, String title, String location, String day,
                   int hourlyValue, int registeredSlots, int totalSlots, boolean enabled) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.day = day;
        this.hourlyValue = hourlyValue;
        this.registeredSlots = registeredSlots;
        this.totalSlots = totalSlots;
        this.enabled = enabled;
    }

    public int getAvailableSlots() {
        return totalSlots - registeredSlots;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getHourlyValue() {
        return hourlyValue;
    }

    public void setHourlyValue(int hourlyValue) {
        this.hourlyValue = hourlyValue;
    }

    public int getRegisteredSlots() {
        return registeredSlots;
    }

    public void setRegisteredSlots(int registeredSlots) {
        this.registeredSlots = registeredSlots;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
