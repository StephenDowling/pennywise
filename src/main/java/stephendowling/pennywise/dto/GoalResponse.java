package stephendowling.pennywise.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalResponse {
    
    private Integer goalId;
    private String goalName;
    private BigDecimal targetAmount;
    private LocalDate deadline;
    private UserSummary user;


    public GoalResponse() {
    }

    public GoalResponse(Integer goalId, String goalName, BigDecimal targetAmount, LocalDate deadline, UserSummary user) {
        this.goalId = goalId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.user = user;
    }

    public Integer getGoalId() {
        return this.goalId;
    }

    public void setGoalId(Integer goalId) {
        this.goalId = goalId;
    }

    public String getGoalName() {
        return this.goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public BigDecimal getTargetAmount() {
        return this.targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public UserSummary getUser() {
        return this.user;
    }

    public void setUser(UserSummary user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return "{" +
            " goalId='" + getGoalId() + "'" +
            ", goalName='" + getGoalName() + "'" +
            ", targetAmount='" + getTargetAmount() + "'" +
            ", deadline='" + getDeadline() + "'" +
            ", user='" + getUser() + "'" +
            "}";
    }
    

}
