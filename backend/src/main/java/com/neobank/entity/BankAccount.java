package com.neobank.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String bankName;
    private String accountNumber;
    private String accountType;
    private double balance;
    private boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public BankAccount() {}

    public static BankAccountBuilder builder() { return new BankAccountBuilder(); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class BankAccountBuilder {
        private final BankAccount account = new BankAccount();

        public BankAccountBuilder id(Long id) { account.setId(id); return this; }
        public BankAccountBuilder user(User user) { account.setUser(user); return this; }
        public BankAccountBuilder bankName(String bankName) { account.setBankName(bankName); return this; }
        public BankAccountBuilder accountNumber(String accountNumber) { account.setAccountNumber(accountNumber); return this; }
        public BankAccountBuilder accountType(String accountType) { account.setAccountType(accountType); return this; }
        public BankAccountBuilder balance(double balance) { account.setBalance(balance); return this; }
        public BankAccountBuilder isActive(boolean isActive) { account.setActive(isActive); return this; }
        public BankAccountBuilder createdAt(LocalDateTime createdAt) { account.setCreatedAt(createdAt); return this; }

        public BankAccount build() { return account; }
    }
}
