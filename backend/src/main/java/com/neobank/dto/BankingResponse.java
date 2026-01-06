package com.neobank.dto;

import java.util.List;

public class BankingResponse {
    private boolean success;
    private List<BankAccountDTO> bankAccounts;
    private List<TransactionDTO> transactions;
    private PaginationDTO pagination;
    private Double totalBalance;
    private TransactionDTO transaction;
    private String message;
    private String error;

    public BankingResponse() {}

    public static BankingResponseBuilder builder() { return new BankingResponseBuilder(); }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public List<BankAccountDTO> getBankAccounts() { return bankAccounts; }
    public void setBankAccounts(List<BankAccountDTO> bankAccounts) { this.bankAccounts = bankAccounts; }

    public List<TransactionDTO> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionDTO> transactions) { this.transactions = transactions; }

    public PaginationDTO getPagination() { return pagination; }
    public void setPagination(PaginationDTO pagination) { this.pagination = pagination; }

    public Double getTotalBalance() { return totalBalance; }
    public void setTotalBalance(Double totalBalance) { this.totalBalance = totalBalance; }

    public TransactionDTO getTransaction() { return transaction; }
    public void setTransaction(TransactionDTO transaction) { this.transaction = transaction; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public static class BankingResponseBuilder {
        private final BankingResponse r = new BankingResponse();

        public BankingResponseBuilder success(boolean success) { r.setSuccess(success); return this; }
        public BankingResponseBuilder bankAccounts(List<BankAccountDTO> bankAccounts) { r.setBankAccounts(bankAccounts); return this; }
        public BankingResponseBuilder transactions(List<TransactionDTO> transactions) { r.setTransactions(transactions); return this; }
        public BankingResponseBuilder pagination(PaginationDTO pagination) { r.setPagination(pagination); return this; }
        public BankingResponseBuilder totalBalance(Double totalBalance) { r.setTotalBalance(totalBalance); return this; }
        public BankingResponseBuilder transaction(TransactionDTO transaction) { r.setTransaction(transaction); return this; }
        public BankingResponseBuilder message(String message) { r.setMessage(message); return this; }
        public BankingResponseBuilder error(String error) { r.setError(error); return this; }

        public BankingResponse build() { return r; }
    }
}
