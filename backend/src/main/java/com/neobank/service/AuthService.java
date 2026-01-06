package com.neobank.service;

import com.neobank.entity.User;
import com.neobank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        String customerId = "CUST_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String publicUrl = name.toLowerCase().replace(" ", "-") + "-" + UUID.randomUUID().toString().substring(0, 4);
        // Generate unique account number with NB prefix
        String accountNumber = "NB" + System.currentTimeMillis() % 1000000 + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        User user = User.builder()
                .name(name)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .customerId(customerId)
                .publicUrl(publicUrl)
                .accountNumber(accountNumber)
                .kycStatus("pending")
                .role("ROLE_USER")
                .status("ACTIVE")
                .build();

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void setPin(String email, String pin) {
        User user = getUserByEmail(email);
        user.setPinHash(passwordEncoder.encode(pin));
        userRepository.save(user);
    }
}
