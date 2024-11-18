package stephendowling.pennywise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity 
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //generated automatically, don't need to provide
    private Integer budgetId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Represents the foreign key relationship to the User entity

    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount; //using big decimal as had issue with using Double 

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    // Constructors
    public Budget() {} //empty constructor good practice 

    public Budget(User user, BigDecimal amount, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and setters
    public Integer getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Integer budgetId) {
        this.budgetId = budgetId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
