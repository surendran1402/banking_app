package com.neobank.service;

import com.neobank.entity.BankAccount;
import com.neobank.entity.Transaction;
import com.neobank.entity.User;
import com.neobank.repository.BankAccountRepository;
import com.neobank.repository.TransactionRepository;
import com.neobank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BankingService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- MAIN TRANSFER FUNCTION ---
    @Transactional
    public Transaction transfer(User sender, String recipientIdentifier, double amount, String description, String category, String pin, Long senderAccountId) {
        
        // 1. PIN CHECK: Must be 4 digits. Logic is simplified for testing.
        if (pin == null || pin.length() != 4) {
            throw new RuntimeException("Invalid Security PIN. Please enter any 4 digits.");
        }

        // 2. FIND RECIPIENT
        User recipient = findRecipient(recipientIdentifier);
        if (recipient.getId().equals(sender.getId())) {
            throw new RuntimeException("Cannot transfer to yourself.");
        }

        // 3. GET OR CREATE ACCOUNTS (Auto-creates account if missing so transfer works)
        BankAccount senderAccount = getOrCreateAccount(sender, 10000.0); // Give default money
        BankAccount recipientAccount = getOrCreateAccount(recipient, 0.0);

        // 4. CHECK BALANCE
        if (senderAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance.");
        }

        // 5. UPDATE BALANCES
        // Deduct from Sender immediately
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        bankAccountRepository.save(senderAccount);

        // Fraud Detection Logic
        String fraudStatus = "NONE";
        String flaggedReason = null;
        
        if (amount > 5000) {
            fraudStatus = "PENDING";
            flaggedReason = "High Value Transaction (> 5,000)";
        }

        // Only credit recipient if NOT flagged as fraud
        if ("NONE".equals(fraudStatus)) {
            recipientAccount.setBalance(recipientAccount.getBalance() + amount);
            bankAccountRepository.save(recipientAccount);
        } else {
            System.out.println("Transaction flagged as fraud. Money deducted from sender but held from recipient.");
        }

        // 6. RECORD TRANSACTIONS (One for Sender, One for Recipient)
        String txId = "TX" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Save Sender's Record
        Transaction senderTx = saveTransaction(txId, sender, recipient, senderAccount, amount, "sent", description, category, fraudStatus, flaggedReason);
        
        // Save Recipient's Record
        saveTransaction(txId, recipient, sender, recipientAccount, amount, "received", description, category, "NONE", null);

        return senderTx;
    }

    // --- HELPER METHODS ---

    // Find Recipient by Email, Account Number, etc.
    public User findRecipient(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByAccountNumber(identifier))
                .or(() -> userRepository.findByCustomerId(identifier))
                .or(() -> userRepository.findByPublicUrl(identifier))
                .or(() -> userRepository.findByMobileNumber(identifier))
                .orElseThrow(() -> new RuntimeException("Recipient not found. Please check details."));
    }

    // Get Account or Create one if it doesn't exist
    private BankAccount getOrCreateAccount(User user, double initialBalance) {
        return bankAccountRepository.findFirstByUserAndIsActiveTrue(user)
                .orElseGet(() -> {
                    BankAccount newAcc = BankAccount.builder()
                            .user(user)
                            .bankName("NeoBank")
                            .accountNumber(user.getAccountNumber() != null ? user.getAccountNumber() : "NB" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                            .accountType("Savings")
                            .balance(initialBalance)
                            .isActive(true)
                            .build();
                    return bankAccountRepository.save(newAcc);
                });
    }

    // Save a single transaction record with DEBUGGING
    private Transaction saveTransaction(String txId, User user, User otherParty, BankAccount account, double amount, String direction, String desc, String cat, String fraudStatus, String flaggedReason) {
        System.out.println("DEBUG: Attempting to save transaction for user: " + user.getEmail() + ", Direction: " + direction);
        try {
            Transaction tx = Transaction.builder()
                    .transactionId(txId)
                    .user(user)
                    .recipient(otherParty)
                    .account(account)
                    .amount(amount)
                    .direction(direction)
                    .description(desc)
                    .category(cat != null ? cat : "Transfer")
                    .status("completed")
                    .fraudStatus(fraudStatus)
                    .flaggedReason(flaggedReason)
                    .build();
            
            Transaction savedTx = transactionRepository.saveAndFlush(tx);
            System.out.println("DEBUG: Success! Transaction saved. DB ID: " + savedTx.getId());
            return savedTx;
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failed to save transaction! Reason: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database Save Failed: " + e.getMessage());
        }
    }

    // --- OTHER REQUIRED METHODS (For Dashboard/Setup) ---

    // Get Link Accounts (Auto-provisions if empty to fix UI selection issues)
    public List<BankAccount> getAccounts(User user) {
        List<BankAccount> accounts = bankAccountRepository.findByUserAndIsActiveTrue(user);
        if (accounts.isEmpty()) {
            System.out.println("No accounts found for user " + user.getEmail() + ". Auto-provisioning default account.");
            BankAccount newAcc = getOrCreateAccount(user, 10000.0);
            accounts = List.of(newAcc);
        }
        return accounts;
    }

    // Get History
    public Page<Transaction> getTransactions(User user, int page, int size) {
        return transactionRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, size));
    }

    // Legacy method for linking
    public BankAccount linkAccount(User user, String bankName, String accountNumber, String accountType) {
        return getOrCreateAccount(user, 10000.0);
    }

    // Add Money (Simulate)
    @Transactional
    public BankAccount simulateCredit(User user, double amount) {
        BankAccount account = getOrCreateAccount(user, 0.0);
        account.setBalance(account.getBalance() + amount);
        
        String txId = "TX" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        saveTransaction(txId, user, null, account, amount, "received", "Demo Credit", "Income", "NONE", null);
        
        return bankAccountRepository.save(account);
    }
}
