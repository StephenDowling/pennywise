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
import stephendowling.pennywise.model.enums.TransactionType;

@Entity
@Table(name = "transaction")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; 

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category must not be null")
    private Category category; 

    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount; //using big decimal as had issue with using Double

    @NotNull
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private TransactionType type;


    public Transaction() {
    }

    public Transaction(Integer transactionId, User user, Category category, BigDecimal amount, LocalDate date, TransactionType type) {
        this.transactionId = transactionId;
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

    public Integer getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public TransactionType getType() {
        return this.type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" +
            " transactionId='" + getTransactionId() + "'" +
            ", user='" + getUser() + "'" +
            ", category='" + getCategory() + "'" +
            ", amount='" + getAmount() + "'" +
            ", date='" + getDate() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
    

}
