package com.neobank;

import com.neobank.entity.User;
import com.neobank.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class NeobankApplication {
    public static void main(String[] args) {
        SpringApplication.run(NeobankApplication.class, args);
    }

    @Bean
    public CommandLineRunner setupAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@neobank.com").isEmpty()) {
                User admin = User.builder()
                        .name("System Admin")
                        .email("admin@neobank.com")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .customerId("ADMIN001")
                        .publicUrl("admin-neobank")
                        .role("ROLE_ADMIN")
                        .status("ACTIVE")
                        .kycStatus("verified")
                        .build();
                userRepository.save(admin);
                System.out.println("Default Admin Account Created: admin@neobank.com / admin123");
            }
        };
    }
}
