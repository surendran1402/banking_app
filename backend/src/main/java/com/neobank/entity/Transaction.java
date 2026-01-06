package com.neobank.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private BankAccount account;

    private double amount;
    private String category;
    private String description;
    private String status;
    private String direction; // "sent" or "received"
    private String type; // "transfer", "credit", etc.
    private String fraudStatus; // NONE, PENDING, APPROVED, BLOCKED, INVESTIGATING
    private String flaggedReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Transaction() {}

    public static TransactionBuilder builder() { return new TransactionBuilder(); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public BankAccount getAccount() { return account; }
    public void setAccount(BankAccount account) { this.account = account; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFraudStatus() { return fraudStatus; }
    public void setFraudStatus(String fraudStatus) { this.fraudStatus = fraudStatus; }

    public String getFlaggedReason() { return flaggedReason; }
    public void setFlaggedReason(String flaggedReason) { this.flaggedReason = flaggedReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @com.fasterxml.jackson.annotation.JsonProperty("sender_name")
    public String getSenderName() {
        if ("sent".equalsIgnoreCase(direction)) {
            return user != null ? user.getName() : "Unknown";
        } else {
            return recipient != null ? recipient.getName() : "Unknown";
        }
    }

    @com.fasterxml.jackson.annotation.JsonProperty("recipient_name")
    public String getRecipientName() {
        if ("sent".equalsIgnoreCase(direction)) {
            return recipient != null ? recipient.getName() : "Unknown";
        } else {
            return user != null ? user.getName() : "Unknown";
        }
    }

    public static class TransactionBuilder {
        private final Transaction tx = new Transaction();

        public TransactionBuilder id(Long id) { tx.setId(id); return this; }
        public TransactionBuilder transactionId(String transactionId) { tx.setTransactionId(transactionId); return this; }
        public TransactionBuilder user(User user) { tx.setUser(user); return this; }
        public TransactionBuilder recipient(User recipient) { tx.setRecipient(recipient); return this; }
        public TransactionBuilder account(BankAccount account) { tx.setAccount(account); return this; }
        public TransactionBuilder amount(double amount) { tx.setAmount(amount); return this; }
        public TransactionBuilder category(String category) { tx.setCategory(category); return this; }
        public TransactionBuilder description(String description) { tx.setDescription(description); return this; }
        public TransactionBuilder status(String status) { tx.setStatus(status); return this; }
        public TransactionBuilder direction(String direction) { tx.setDirection(direction); return this; }
        public TransactionBuilder type(String type) { tx.setType(type); return this; }
        public TransactionBuilder fraudStatus(String fraudStatus) { tx.setFraudStatus(fraudStatus); return this; }
        public TransactionBuilder flaggedReason(String flaggedReason) { tx.setFlaggedReason(flaggedReason); return this; }
        public TransactionBuilder createdAt(LocalDateTime createdAt) { tx.setCreatedAt(createdAt); return this; }

        public Transaction build() { return tx; }
    }
}
