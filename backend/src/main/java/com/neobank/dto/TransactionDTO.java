package com.neobank.dto;

import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    private String transactionId;
    private double amount;
    private String category;
    private String description;
    private String status;
    private String direction;
    private String type;
    private String transactionType;
    private String fraudStatus;
    private String flaggedReason;
    private LocalDateTime createdAt;


    public TransactionDTO() {}

    public static TransactionDTOBuilder builder() { return new TransactionDTOBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

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

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getFraudStatus() { return fraudStatus; }
    public void setFraudStatus(String fraudStatus) { this.fraudStatus = fraudStatus; }

    public String getFlaggedReason() { return flaggedReason; }
    public void setFlaggedReason(String flaggedReason) { this.flaggedReason = flaggedReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @com.fasterxml.jackson.annotation.JsonProperty("sender_name")
    private String senderName;

    @com.fasterxml.jackson.annotation.JsonProperty("recipient_name")
    private String recipientName;

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public static class TransactionDTOBuilder {
        private final TransactionDTO dto = new TransactionDTO();

        public TransactionDTOBuilder id(Long id) { dto.setId(id); return this; }
        public TransactionDTOBuilder transactionId(String transactionId) { dto.setTransactionId(transactionId); return this; }
        public TransactionDTOBuilder amount(double amount) { dto.setAmount(amount); return this; }
        public TransactionDTOBuilder category(String category) { dto.setCategory(category); return this; }
        public TransactionDTOBuilder description(String description) { dto.setDescription(description); return this; }
        public TransactionDTOBuilder status(String status) { dto.setStatus(status); return this; }
        public TransactionDTOBuilder direction(String direction) { dto.setDirection(direction); return this; }
        public TransactionDTOBuilder type(String type) { dto.setType(type); return this; }
        public TransactionDTOBuilder transactionType(String transactionType) { dto.setTransactionType(transactionType); return this; }
        public TransactionDTOBuilder fraudStatus(String fraudStatus) { dto.setFraudStatus(fraudStatus); return this; }
        public TransactionDTOBuilder flaggedReason(String flaggedReason) { dto.setFlaggedReason(flaggedReason); return this; }
        public TransactionDTOBuilder createdAt(LocalDateTime createdAt) { dto.setCreatedAt(createdAt); return this; }
        public TransactionDTOBuilder senderName(String senderName) { dto.setSenderName(senderName); return this; }
        public TransactionDTOBuilder recipientName(String recipientName) { dto.setRecipientName(recipientName); return this; }

        public TransactionDTO build() { return dto; }
    }
}
