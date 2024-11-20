package stephendowling.pennywise.model.enums;

public enum GoalStatus {
    IN_PROGRESS("In Progress"),
    ACHIEVED("Achieved");

    private final String displayName;

    GoalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
