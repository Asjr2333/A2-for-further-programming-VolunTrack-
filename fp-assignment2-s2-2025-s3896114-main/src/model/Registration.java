package model;

import java.time.*;

public class Registration {
    private int id;
    private String registrationId;
    private int userId;
    private int projectId;
    private int slots;
    private int hoursPerSlot;
    private int totalContribution;
    private LocalDateTime confirmedAt;
    private String projectTitle;
    private String projectLocation;
    private String projectDay;

    public Registration() {}

    public Registration(int userId, int projectId, int slots, int hoursPerSlot, int totalContribution) {
        this.userId = userId;
        this.projectId = projectId;
        this.slots = slots;
        this.hoursPerSlot = hoursPerSlot;
        this.totalContribution = totalContribution;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public int getHoursPerSlot() {
        return hoursPerSlot;
    }

    public void setHoursPerSlot(int hoursPerSlot) {
        this.hoursPerSlot = hoursPerSlot;
    }

    public int getTotalContribution() {
        return totalContribution;
    }

    public void setTotalContribution(int totalContribution) {
        this.totalContribution = totalContribution;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public String getProjectDay() {
        return projectDay;
    }

    public void setProjectDay(String projectDay) {
        this.projectDay = projectDay;
    }
}
