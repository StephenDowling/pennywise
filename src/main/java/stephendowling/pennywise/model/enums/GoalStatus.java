package stephendowling.pennywise.model.enums;

//for use when value can only be In Progress or Achieved 
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
