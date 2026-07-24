package com.safora.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmergencyContactDto {
    private Long id;
    private String name;
    private String relationship;
    private String phoneNumber;

    public EmergencyContactDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
