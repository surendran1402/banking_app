package com.neobank.dto;

public class BankAccountDTO {
    private Long id;
    private String bankName;
    private String accountNumber;
    private String accountType;
    private double balance;
    private boolean isActive;

    public BankAccountDTO() {}

    public static BankAccountDTOBuilder builder() { return new BankAccountDTOBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public static class BankAccountDTOBuilder {
        private final BankAccountDTO dto = new BankAccountDTO();

        public BankAccountDTOBuilder id(Long id) { dto.setId(id); return this; }
        public BankAccountDTOBuilder bankName(String bankName) { dto.setBankName(bankName); return this; }
        public BankAccountDTOBuilder accountNumber(String accountNumber) { dto.setAccountNumber(accountNumber); return this; }
        public BankAccountDTOBuilder accountType(String accountType) { dto.setAccountType(accountType); return this; }
        public BankAccountDTOBuilder balance(double balance) { dto.setBalance(balance); return this; }
        public BankAccountDTOBuilder isActive(boolean isActive) { dto.setActive(isActive); return this; }

        public BankAccountDTO build() { return dto; }
    }
}
