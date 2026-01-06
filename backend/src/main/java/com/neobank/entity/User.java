package com.neobank.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String customerId;

    @Column(unique = true, nullable = false)
    private String publicUrl;

    private String pinHash;
    private String profilePhoto;
    private String phoneNumber;

    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    @Column(unique = true)
    private String accountNumber;
    
    private String dateOfBirth;
    private String gender;
    private String nationality;
    private String maritalStatus;
    private String occupation;

    private String mobileNumber;
    private String landlineNumber;
    private String preferredLanguage;

    private String kycStatus;
    private LocalDateTime kycLastUpdated;

    private boolean internetBanking;
    private boolean mobileBanking;
    private boolean smsAlerts;
    private boolean eStatements;

    @ElementCollection
    @CollectionTable(name = "user_upi_ids", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "upi_id")
    private List<String> upiIds;

    private String role; // ROLE_USER, ROLE_ADMIN
    private String status; // ACTIVE, BLOCKED, FROZEN

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Default constructor
    public User() {}

    // Builder pattern implementation
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getPublicUrl() { return publicUrl; }
    public void setPublicUrl(String publicUrl) { this.publicUrl = publicUrl; }

    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getLandlineNumber() { return landlineNumber; }
    public void setLandlineNumber(String landlineNumber) { this.landlineNumber = landlineNumber; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }

    public LocalDateTime getKycLastUpdated() { return kycLastUpdated; }
    public void setKycLastUpdated(LocalDateTime kycLastUpdated) { this.kycLastUpdated = kycLastUpdated; }

    public boolean isInternetBanking() { return internetBanking; }
    public void setInternetBanking(boolean internetBanking) { this.internetBanking = internetBanking; }

    public boolean isMobileBanking() { return mobileBanking; }
    public void setMobileBanking(boolean mobileBanking) { this.mobileBanking = mobileBanking; }

    public boolean isSmsAlerts() { return smsAlerts; }
    public void setSmsAlerts(boolean smsAlerts) { this.smsAlerts = smsAlerts; }

    public boolean isEStatements() { return eStatements; }
    public void setEStatements(boolean eStatements) { this.eStatements = eStatements; }

    public List<String> getUpiIds() { return upiIds; }
    public void setUpiIds(List<String> upiIds) { this.upiIds = upiIds; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder class
    public static class UserBuilder {
        private final User user = new User();

        public UserBuilder id(Long id) { user.setId(id); return this; }
        public UserBuilder email(String email) { user.setEmail(email); return this; }
        public UserBuilder passwordHash(String passwordHash) { user.setPasswordHash(passwordHash); return this; }
        public UserBuilder name(String name) { user.setName(name); return this; }
        public UserBuilder customerId(String customerId) { user.setCustomerId(customerId); return this; }
        public UserBuilder publicUrl(String publicUrl) { user.setPublicUrl(publicUrl); return this; }
        public UserBuilder pinHash(String pinHash) { user.setPinHash(pinHash); return this; }
        public UserBuilder profilePhoto(String profilePhoto) { user.setProfilePhoto(profilePhoto); return this; }
        public UserBuilder phoneNumber(String phoneNumber) { user.setPhoneNumber(phoneNumber); return this; }
        public UserBuilder street(String street) { user.setStreet(street); return this; }
        public UserBuilder city(String city) { user.setCity(city); return this; }
        public UserBuilder state(String state) { user.setState(state); return this; }
        public UserBuilder zipCode(String zipCode) { user.setZipCode(zipCode); return this; }
        public UserBuilder country(String country) { user.setCountry(country); return this; }
        public UserBuilder accountNumber(String accountNumber) { user.setAccountNumber(accountNumber); return this; }
        public UserBuilder dateOfBirth(String dateOfBirth) { user.setDateOfBirth(dateOfBirth); return this; }
        public UserBuilder gender(String gender) { user.setGender(gender); return this; }
        public UserBuilder nationality(String nationality) { user.setNationality(nationality); return this; }
        public UserBuilder maritalStatus(String maritalStatus) { user.setMaritalStatus(maritalStatus); return this; }
        public UserBuilder occupation(String occupation) { user.setOccupation(occupation); return this; }
        public UserBuilder mobileNumber(String mobileNumber) { user.setMobileNumber(mobileNumber); return this; }
        public UserBuilder landlineNumber(String landlineNumber) { user.setLandlineNumber(landlineNumber); return this; }
        public UserBuilder preferredLanguage(String preferredLanguage) { user.setPreferredLanguage(preferredLanguage); return this; }
        public UserBuilder kycStatus(String kycStatus) { user.setKycStatus(kycStatus); return this; }
        public UserBuilder kycLastUpdated(LocalDateTime kycLastUpdated) { user.setKycLastUpdated(kycLastUpdated); return this; }
        public UserBuilder internetBanking(boolean internetBanking) { user.setInternetBanking(internetBanking); return this; }
        public UserBuilder mobileBanking(boolean mobileBanking) { user.setMobileBanking(mobileBanking); return this; }
        public UserBuilder smsAlerts(boolean smsAlerts) { user.setSmsAlerts(smsAlerts); return this; }
        public UserBuilder eStatements(boolean eStatements) { user.setEStatements(eStatements); return this; }
        public UserBuilder upiIds(List<String> upiIds) { user.setUpiIds(upiIds); return this; }
        public UserBuilder role(String role) { user.setRole(role); return this; }
        public UserBuilder status(String status) { user.setStatus(status); return this; }

        public User build() { return user; }
    }
}
