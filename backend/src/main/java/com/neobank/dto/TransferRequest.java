package com.neobank.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class TransferRequest {
    private String recipientPublicId;
    private String recipientAccountNumber;
    private String recipientProfileUrl;
    private String recipientMobileNumber;
    private String recipientEmail;
    private double amount;
    private String description;
    private String category;
    private String pin;
    private String senderAccountId;

    public TransferRequest() {}

    public String getRecipientPublicId() { return recipientPublicId; }
    public void setRecipientPublicId(String recipientPublicId) { this.recipientPublicId = recipientPublicId; }

    public String getRecipientAccountNumber() { return recipientAccountNumber; }
    public void setRecipientAccountNumber(String recipientAccountNumber) { this.recipientAccountNumber = recipientAccountNumber; }

    public String getRecipientProfileUrl() { return recipientProfileUrl; }
    public void setRecipientProfileUrl(String recipientProfileUrl) { this.recipientProfileUrl = recipientProfileUrl; }

    public String getRecipientMobileNumber() { return recipientMobileNumber; }
    public void setRecipientMobileNumber(String recipientMobileNumber) { this.recipientMobileNumber = recipientMobileNumber; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public String getSenderAccountId() { return senderAccountId; }
    public void setSenderAccountId(String senderAccountId) { this.senderAccountId = senderAccountId; }
}

