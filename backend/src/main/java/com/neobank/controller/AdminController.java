package com.neobank.controller;

import com.neobank.dto.*;
import com.neobank.entity.AuditLog;
import com.neobank.entity.Transaction;
import com.neobank.entity.User;
import com.neobank.service.AdminService;
import com.neobank.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    @Autowired
    public AdminController(AdminService adminService, AuthService authService) {
        this.adminService = adminService;
        this.authService = authService;
    }

    @GetMapping("/transactions/flagged")
    public ResponseEntity<BankingResponse> getFlaggedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Transaction> txPage = adminService.getFlaggedTransactions(page, size);
        return ResponseEntity.ok(BankingResponse.builder()
                .success(true)
                .transactions(txPage.getContent().stream().map(this::mapToTransactionDTO).collect(Collectors.toList()))
                .pagination(PaginationDTO.builder()
                        .currentPage(txPage.getNumber())
                        .totalPages(txPage.getTotalPages())
                        .totalItems(txPage.getTotalElements())
                        .build())
                .build());
    }

    @GetMapping("/transactions/all")
    public ResponseEntity<BankingResponse> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Transaction> txPage = adminService.getAllTransactions(page, size);
        return ResponseEntity.ok(BankingResponse.builder()
                .success(true)
                .transactions(txPage.getContent().stream().map(this::mapToTransactionDTO).collect(Collectors.toList()))
                .pagination(PaginationDTO.builder()
                        .currentPage(txPage.getNumber())
                        .totalPages(txPage.getTotalPages())
                        .totalItems(txPage.getTotalElements())
                        .build())
                .build());
    }

    @PostMapping("/transactions/{id}/fraud-status")
    public ResponseEntity<BankingResponse> updateTransactionFraudStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        User admin = authService.getUserByEmail(userDetails.getUsername());
        adminService.updateTransactionFraudStatus(id, request.get("status"), request.get("reason"), admin);
        return ResponseEntity.ok(BankingResponse.builder()
                .success(true)
                .message("Transaction fraud status updated successfully")
                .build());
    }

    @GetMapping("/users")
    public ResponseEntity<AuthResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<User> userPage = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .users(userPage.getContent().stream().map(this::mapToUserDTO).collect(Collectors.toList()))
                .pagination(PaginationDTO.builder()
                        .currentPage(userPage.getNumber())
                        .totalPages(userPage.getTotalPages())
                        .totalItems(userPage.getTotalElements())
                        .build())
                .build());
    }

    @PostMapping("/users/{id}/status")
    public ResponseEntity<AuthResponse> updateUserStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        User admin = authService.getUserByEmail(userDetails.getUsername());
        adminService.updateUserStatus(id, request.get("status"), request.get("reason"), admin);
        return ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .message("User status updated successfully")
                .build());
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(adminService.getAuditLogs());
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
                .transactionType(transactionType)
                .fraudStatus(tx.getFraudStatus())
                .flaggedReason(tx.getFlaggedReason())
                .createdAt(tx.getCreatedAt())
                .recipientName(tx.getRecipient() != null ? tx.getRecipient().getName() : null)
                .build();
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .customerId(user.getCustomerId())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .kycStatus(user.getKycStatus())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}
