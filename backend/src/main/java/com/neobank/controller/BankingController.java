package com.neobank.controller;

import com.neobank.dto.*;
import com.neobank.entity.BankAccount;
import com.neobank.entity.Transaction;
import com.neobank.entity.User;
import com.neobank.service.AuthService;
import com.neobank.service.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/banking")
public class BankingController {

    private final BankingService bankingService;
    private final AuthService authService;

    @Autowired
    public BankingController(BankingService bankingService, AuthService authService) {
        this.bankingService = bankingService;
        this.authService = authService;
    }

    @PostMapping("/link-account")
    public ResponseEntity<BankingResponse> linkAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            BankAccount account = bankingService.linkAccount(
                    user,
                    request.get("bankName"),
                    request.get("accountNumber"),
                    request.get("accountType")
            );
            return ResponseEntity.ok(BankingResponse.builder()
                    .success(true)
                    .message("Account linked successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BankingResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<BankingResponse> getAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            List<BankAccount> accounts = bankingService.getAccounts(user);
            return ResponseEntity.ok(BankingResponse.builder()
                    .success(true)
                    .bankAccounts(accounts.stream().map(this::mapToAccountDTO).collect(Collectors.toList()))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BankingResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<BankingResponse> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TransferRequest request
    ) {
        try {
            User sender = authService.getUserByEmail(userDetails.getUsername());
            
            // Priority: Email > Account Number > Customer ID > Profile URL > Mobile Number
            String recipientIdentifier = request.getRecipientEmail();
            if (recipientIdentifier == null || recipientIdentifier.isEmpty()) {
                recipientIdentifier = request.getRecipientAccountNumber();
            }
            if (recipientIdentifier == null || recipientIdentifier.isEmpty()) {
                recipientIdentifier = request.getRecipientPublicId();
            }
            if (recipientIdentifier == null || recipientIdentifier.isEmpty()) {
                recipientIdentifier = request.getRecipientProfileUrl();
            }
            if (recipientIdentifier == null || recipientIdentifier.isEmpty()) {
                recipientIdentifier = request.getRecipientMobileNumber();
            }
            
            if (recipientIdentifier == null || recipientIdentifier.isEmpty()) {
                return ResponseEntity.status(400).body(BankingResponse.builder()
                        .success(false)
                        .error("Please provide recipient email, account number, or other identifier")
                        .build());
            }

            Long senderAccountId = null;
            if (request.getSenderAccountId() != null && !request.getSenderAccountId().isEmpty()) {
                try {
                    senderAccountId = Long.parseLong(request.getSenderAccountId());
                } catch (NumberFormatException e) {
                    // Ignore invalid format
                }
            }

            Transaction tx = bankingService.transfer(
                    sender,
                    recipientIdentifier,
                    request.getAmount(),
                    request.getDescription(),
                    request.getCategory(),
                    request.getPin(),
                    senderAccountId
            );
            return ResponseEntity.ok(BankingResponse.builder()
                    .success(true)
                    .transaction(mapToTransactionDTO(tx))
                    .message("Transfer successful")
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Transfer Error: " + e.getMessage());
            
            int status = 500;
            String msg = e.getMessage();

            if (msg != null) {
                if (msg.contains("Invalid Security PIN")) {
                    status = 401; // Unauthorized
                } else if (msg.contains("Recipient not found")) {
                    status = 404; // Not Found
                } else if (msg.contains("Insufficient balance")) {
                    status = 406; // Not Acceptable
                } else if (msg.contains("Cannot transfer")) {
                    status = 409; // Conflict
                } else if (msg.contains("Transfer amount") || msg.contains("Invalid PIN")) {
                    status = 400; // Bad Request
                }
            }
            
            return ResponseEntity.status(status).body(BankingResponse.builder()
                    .success(false)
                    .error(msg)
                    .build());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<BankingResponse> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            Page<Transaction> txPage = bankingService.getTransactions(user, page, size);
            
            return ResponseEntity.ok(BankingResponse.builder()
                    .success(true)
                    .transactions(txPage.getContent().stream().map(this::mapToTransactionDTO).collect(Collectors.toList()))
                    .pagination(PaginationDTO.builder()
                            .currentPage(txPage.getNumber())
                            .totalPages(txPage.getTotalPages())
                            .totalItems(txPage.getTotalElements())
                            .build())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BankingResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<BankingResponse> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            List<BankAccount> accounts = bankingService.getAccounts(user);
            double totalBalance = accounts.stream().mapToDouble(BankAccount::getBalance).sum();
            return ResponseEntity.ok(BankingResponse.builder()
                    .success(true)
                    .bankAccounts(accounts.stream().map(this::mapToAccountDTO).collect(Collectors.toList()))
                    .totalBalance(totalBalance)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BankingResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/simulate-credit")
    public ResponseEntity<BankingResponse> simulateCredit(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Double> request
    ) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            bankingService.simulateCredit(user, request.get("amount"));
            return ResponseEntity.ok(BankingResponse.builder()
                    .success(true)
                    .message("Account credited successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BankingResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/find-user/{identifier}")
    public ResponseEntity<AuthResponse> findUser(@PathVariable String identifier) {
        try {
            User user = bankingService.findRecipient(identifier);
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .user(mapToUserDTO(user))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(AuthResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .customerId(user.getCustomerId())
                .publicUrl(user.getPublicUrl())
                .accountNumber(user.getAccountNumber())
                .phoneNumber(user.getPhoneNumber())
                .mobileNumber(user.getMobileNumber())
                .build();
    }

    private BankAccountDTO mapToAccountDTO(BankAccount account) {
        return BankAccountDTO.builder()
                .id(account.getId())
                .bankName(account.getBankName())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .isActive(account.isActive())
                .build();
    }

    private TransactionDTO mapToTransactionDTO(Transaction tx) {
        // Determine transaction type based on direction
        String transactionType = "sent".equals(tx.getDirection()) ? "transfer" : "deposit";
        
        return TransactionDTO.builder()
                .id(tx.getId())
                .transactionId(tx.getTransactionId())
                .amount(tx.getAmount())
                .category(tx.getCategory())
                .description(tx.getDescription())
                .status(tx.getStatus())
                .direction(tx.getDirection())
                .type(tx.getType())
                .transactionType(transactionType)
                .createdAt(tx.getCreatedAt())
                .senderName(tx.getSenderName())
                .recipientName(tx.getRecipientName())
                .build();
    }
}
