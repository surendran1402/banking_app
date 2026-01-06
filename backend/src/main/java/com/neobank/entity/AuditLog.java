package com.neobank.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;
    private String adminEmail;
    private String action; // e.g., "BLOCK_TRANSACTION", "FREEZE_ACCOUNT"
    private String targetType; // "TRANSACTION", "USER"
    private Long targetId;
    private String reason;
    private String details;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public AuditLog() {}

    public static AuditLogBuilder builder() { return new AuditLogBuilder(); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class AuditLogBuilder {
        private final AuditLog log = new AuditLog();

        public AuditLogBuilder id(Long id) { log.setId(id); return this; }
        public AuditLogBuilder adminId(Long adminId) { log.setAdminId(adminId); return this; }
        public AuditLogBuilder adminEmail(String adminEmail) { log.setAdminEmail(adminEmail); return this; }
        public AuditLogBuilder action(String action) { log.setAction(action); return this; }
        public AuditLogBuilder targetType(String targetType) { log.setTargetType(targetType); return this; }
        public AuditLogBuilder targetId(Long targetId) { log.setTargetId(targetId); return this; }
        public AuditLogBuilder reason(String reason) { log.setReason(reason); return this; }
        public AuditLogBuilder details(String details) { log.setDetails(details); return this; }
        public AuditLogBuilder createdAt(LocalDateTime createdAt) { log.setCreatedAt(createdAt); return this; }

        public AuditLog build() { return log; }
    }
}
