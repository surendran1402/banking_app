package com.neobank.service;

import com.neobank.entity.AuditLog;
import com.neobank.entity.BankAccount;
import com.neobank.entity.Transaction;
import com.neobank.entity.User;
import com.neobank.repository.AuditLogRepository;
import com.neobank.repository.BankAccountRepository;
import com.neobank.repository.TransactionRepository;
import com.neobank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public AdminService(UserRepository userRepository, TransactionRepository transactionRepository, AuditLogRepository auditLogRepository, BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public Page<Transaction> getFlaggedTransactions(int page, int size) {
        return transactionRepository.findByFraudStatusOrderByCreatedAtDesc("PENDING", PageRequest.of(page, size));
    }

    public Page<Transaction> getAllTransactions(int page, int size) {
        return transactionRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    @Transactional
    public void updateTransactionFraudStatus(Long transactionId, String status, String reason, User admin) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        String previousStatus = tx.getFraudStatus();
        
        // Handle Balance Updates based on Status Transition
        if ("PENDING".equals(previousStatus)) {
            if ("APPROVED".equals(status)) {
                // Money was deducted from sender but HELD from recipient. Now credit recipient.
                User recipient = tx.getRecipient();
                if (recipient != null) {
                    BankAccount recipientAccount = bankAccountRepository.findFirstByUserAndIsActiveTrue(recipient)
                            .orElseThrow(() -> new RuntimeException("Recipient account not found"));
                    
                    recipientAccount.setBalance(recipientAccount.getBalance() + tx.getAmount());
                    bankAccountRepository.save(recipientAccount);
                }
            } else if ("BLOCKED".equals(status)) {
                // Money was deducted from sender. Since it's blocked, we REFUND the sender.
                // (Alternatively, we could move it to a seizure account, but refund is safer for now)
                User sender = tx.getUser();
                if (sender != null) {
                    BankAccount senderAccount = bankAccountRepository.findFirstByUserAndIsActiveTrue(sender)
                            .orElseThrow(() -> new RuntimeException("Sender account not found"));
                    
                    senderAccount.setBalance(senderAccount.getBalance() + tx.getAmount());
                    bankAccountRepository.save(senderAccount);
                }
            }
        }

        tx.setFraudStatus(status);
        transactionRepository.save(tx);

        auditLogRepository.save(AuditLog.builder()
                .adminId(admin.getId())
                .adminEmail(admin.getEmail())
                .action("UPDATE_TRANSACTION_FRAUD_STATUS")
                .targetType("TRANSACTION")
                .targetId(transactionId)
                .reason(reason)
                .details("Status changed from " + previousStatus + " to " + status)
                .build());
    }

    public Page<User> getAllUsers(int page, int size) {
        return userRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    @Transactional
    public void updateUserStatus(Long userId, String status, String reason, User admin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus(status);
        userRepository.save(user);

        auditLogRepository.save(AuditLog.builder()
                .adminId(admin.getId())
                .adminEmail(admin.getEmail())
                .action("UPDATE_USER_STATUS")
                .targetType("USER")
                .targetId(userId)
                .reason(reason)
                .details("Status changed to: " + status)
                .build());
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAll();
    }
}
