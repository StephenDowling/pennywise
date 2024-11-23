package stephendowling.pennywise.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO for returning when making changes to a budget 
// should display budget info then a UserSummary DTO

public class BudgetResponse {
    private Integer budgetId;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    private UserSummary user;

    // Constructor, getters, setters

    public Integer getBudgetId() {
        return this.budgetId;
    }

    public void setBudgetId(Integer budgetId) {
        this.budgetId = budgetId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public UserSummary getUser() {
        return this.user;
    }

    public void setUser(UserSummary user) {
        this.user = user;
    }

   
    public BudgetResponse(Integer budgetId, BigDecimal amount, LocalDate startDate, LocalDate endDate, String name, UserSummary user) {
        this.budgetId = budgetId;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.user = user;
     
    }
}