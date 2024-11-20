package stephendowling.pennywise.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import stephendowling.pennywise.model.enums.GoalStatus;

@Entity
@Table(name = "goal")
    public class Goal {
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer goalId;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user; 

        @NotNull
        private String name;

        @NotNull
        @Digits(integer = 10, fraction = 2)
        private BigDecimal targetAmount; //using big decimal as had issue with using Double

        @NotNull
        @Digits(integer = 10, fraction = 2)
        private BigDecimal currentAmount;

        @NotNull
        private LocalDate deadline;

        @Enumerated(EnumType.STRING)
        private GoalStatus status;


    public Goal() {
    }

    public Goal(Integer goalId, User user, String name, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate deadline, GoalStatus status) {
        this.goalId = goalId;
        this.user = user;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.status = status;
    }

    public Integer getGoalId() {
        return this.goalId;
    }

    public void setGoalId(Integer goalId) {
        this.goalId = goalId;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTargetAmount() {
        return this.targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return this.currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public GoalStatus getStatus() {
        return this.status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" +
            " goalId='" + getGoalId() + "'" +
            ", user='" + getUser() + "'" +
            ", goalName='" + getName() + "'" +
            ", targetAmount='" + getTargetAmount() + "'" +
            ", currentAmount='" + getCurrentAmount() + "'" +
            ", deadline='" + getDeadline() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
        

}
