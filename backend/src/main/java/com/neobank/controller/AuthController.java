package com.neobank.controller;

import com.neobank.dto.*;
import com.neobank.entity.User;
import com.neobank.security.JwtService;
import com.neobank.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request.getName(), request.getEmail(), request.getPassword());
            String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPasswordHash(), new ArrayList<>()));
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .token(token)
                    .user(mapToDTO(user))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(AuthResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.login(request.getEmail(), request.getPassword());
            String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPasswordHash(), new ArrayList<>()));
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .token(token)
                    .user(mapToDTO(user))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(401).body(AuthResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<AuthResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserDTO profileData
    ) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            if (profileData.getName() != null) user.setName(profileData.getName());
            if (profileData.getPhoneNumber() != null) user.setPhoneNumber(profileData.getPhoneNumber());
            
            user = authService.updateUser(user);
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .user(mapToDTO(user))
                    .message("Profile updated successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(AuthResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/set-pin")
    public ResponseEntity<AuthResponse> setPin(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        try {
            authService.setPin(userDetails.getUsername(), request.get("pin"));
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .message("PIN set successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(AuthResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/update-comprehensive-profile")
    public ResponseEntity<AuthResponse> updateComprehensiveProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserDTO profileData
    ) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            if (profileData.getDateOfBirth() != null) user.setDateOfBirth(profileData.getDateOfBirth());
            if (profileData.getGender() != null) user.setGender(profileData.getGender());
            if (profileData.getNationality() != null) user.setNationality(profileData.getNationality());
            if (profileData.getMaritalStatus() != null) user.setMaritalStatus(profileData.getMaritalStatus());
            if (profileData.getOccupation() != null) user.setOccupation(profileData.getOccupation());
            if (profileData.getMobileNumber() != null) user.setMobileNumber(profileData.getMobileNumber());
            if (profileData.getPreferredLanguage() != null) user.setPreferredLanguage(profileData.getPreferredLanguage());
            
            user = authService.updateUser(user);
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .user(mapToDTO(user))
                    .message("Comprehensive profile updated successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(AuthResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .user(mapToDTO(user))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(AuthResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .customerId(user.getCustomerId())
                .publicUrl(user.getPublicUrl())
                .profilePhoto(user.getProfilePhoto())
                .phoneNumber(user.getPhoneNumber())
                .accountNumber(user.getAccountNumber())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .nationality(user.getNationality())
                .maritalStatus(user.getMaritalStatus())
                .occupation(user.getOccupation())
                .mobileNumber(user.getMobileNumber())
                .preferredLanguage(user.getPreferredLanguage())
                .kycStatus(user.getKycStatus())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .upiIds(user.getUpiIds())
                .build();
    }
}
