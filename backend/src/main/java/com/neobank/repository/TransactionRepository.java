package com.neobank.repository;

import com.neobank.entity.Transaction;
import com.neobank.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    List<Transaction> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
    Page<Transaction> findByFraudStatusOrderByCreatedAtDesc(String fraudStatus, Pageable pageable);
    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // Find all transactions where user is sender OR recipient
    @Query("SELECT t FROM Transaction t WHERE t.user = :user OR t.recipient = :user ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserOrRecipientOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);
}
