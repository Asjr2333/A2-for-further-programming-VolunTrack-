package model;

public class CartItem {
    private Project project;
    private int slots;
    private int hoursPerSlot;

    public CartItem(Project project, int slots, int hoursPerSlot) {
        this.project = project;
        this.slots = slots;
        this.hoursPerSlot = hoursPerSlot;
    }

    public int getTotalContributions(){
        return project.getHourlyValue() * hoursPerSlot * slots;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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
}
