package com.neobank.repository;

import com.neobank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCustomerId(String customerId);
    Optional<User> findByPublicUrl(String publicUrl);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByMobileNumber(String mobileNumber);
    Optional<User> findByAccountNumber(String accountNumber);
    org.springframework.data.domain.Page<User> findAllByOrderByCreatedAtDesc(org.springframework.data.domain.Pageable pageable);
}
