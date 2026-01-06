package com.neobank.repository;

import com.neobank.entity.BankAccount;
import com.neobank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByUserAndIsActiveTrue(User user);
    Optional<BankAccount> findByAccountNumberAndIsActiveTrue(String accountNumber);
    Optional<BankAccount> findFirstByUserAndIsActiveTrue(User user);
}
