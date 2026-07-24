package com.safora.server.dtos;

import java.util.List;

public class UserProfileDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private List<EmergencyContactDto> emergencyContacts;

    public UserProfileDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public List<EmergencyContactDto> getEmergencyContacts() { return emergencyContacts; }
    public void setEmergencyContacts(List<EmergencyContactDto> emergencyContacts) { this.emergencyContacts = emergencyContacts; }
}
