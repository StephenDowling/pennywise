package stephendowling.pennywise.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionResponse {
    private Integer transactionId;
    private BigDecimal amount;
    private LocalDate date;
    private String type;
    private String categoryName; 
    private UserSummary user;  

    // Constructors, getters, and setters

    public TransactionResponse() {
    }

    public TransactionResponse(Integer transactionId, BigDecimal amount, LocalDate date, String type, String categoryName, UserSummary user) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.categoryName = categoryName;
        this.user = user;
    }

    public Integer getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public UserSummary getUser() {
        return this.user;
    }

    public void setUser(UserSummary user) {
        this.user = user;
    }

}
