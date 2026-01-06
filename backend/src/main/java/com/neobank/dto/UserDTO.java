package com.neobank.dto;

import java.util.List;

public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String customerId;
    private String publicUrl;
    private String profilePhoto;
    private String phoneNumber;
    private String accountNumber;
    private String dateOfBirth;
    private String gender;
    private String nationality;
    private String maritalStatus;
    private String occupation;
    private String mobileNumber;
    private String preferredLanguage;
    private String kycStatus;
    private String role;
    private String status;
    private String createdAt;
    private List<String> upiIds;

    public UserDTO() {}

    public static UserDTOBuilder builder() { return new UserDTOBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getPublicUrl() { return publicUrl; }
    public void setPublicUrl(String publicUrl) { this.publicUrl = publicUrl; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

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

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<String> getUpiIds() { return upiIds; }
    public void setUpiIds(List<String> upiIds) { this.upiIds = upiIds; }

    public static class UserDTOBuilder {
        private final UserDTO dto = new UserDTO();

        public UserDTOBuilder id(Long id) { dto.setId(id); return this; }
        public UserDTOBuilder email(String email) { dto.setEmail(email); return this; }
        public UserDTOBuilder name(String name) { dto.setName(name); return this; }
        public UserDTOBuilder customerId(String customerId) { dto.setCustomerId(customerId); return this; }
        public UserDTOBuilder publicUrl(String publicUrl) { dto.setPublicUrl(publicUrl); return this; }
        public UserDTOBuilder profilePhoto(String profilePhoto) { dto.setProfilePhoto(profilePhoto); return this; }
        public UserDTOBuilder phoneNumber(String phoneNumber) { dto.setPhoneNumber(phoneNumber); return this; }
        public UserDTOBuilder accountNumber(String accountNumber) { dto.setAccountNumber(accountNumber); return this; }
        public UserDTOBuilder dateOfBirth(String dateOfBirth) { dto.setDateOfBirth(dateOfBirth); return this; }
        public UserDTOBuilder gender(String gender) { dto.setGender(gender); return this; }
        public UserDTOBuilder nationality(String nationality) { dto.setNationality(nationality); return this; }
        public UserDTOBuilder maritalStatus(String maritalStatus) { dto.setMaritalStatus(maritalStatus); return this; }
        public UserDTOBuilder occupation(String occupation) { dto.setOccupation(occupation); return this; }
        public UserDTOBuilder mobileNumber(String mobileNumber) { dto.setMobileNumber(mobileNumber); return this; }
        public UserDTOBuilder preferredLanguage(String preferredLanguage) { dto.setPreferredLanguage(preferredLanguage); return this; }
        public UserDTOBuilder kycStatus(String kycStatus) { dto.setKycStatus(kycStatus); return this; }
        public UserDTOBuilder role(String role) { dto.setRole(role); return this; }
        public UserDTOBuilder status(String status) { dto.setStatus(status); return this; }
        public UserDTOBuilder createdAt(String createdAt) { dto.setCreatedAt(createdAt); return this; }
        public UserDTOBuilder upiIds(List<String> upiIds) { dto.setUpiIds(upiIds); return this; }

        public UserDTO build() { return dto; }
    }
}
