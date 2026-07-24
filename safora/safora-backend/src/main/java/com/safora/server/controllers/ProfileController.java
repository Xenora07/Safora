package com.safora.server.controllers;

import com.safora.server.dtos.EmergencyContactDto;
import com.safora.server.dtos.UserProfileDto;
import com.safora.server.entities.EmergencyContact;
import com.safora.server.entities.User;
import com.safora.server.repositories.EmergencyContactRepository;
import com.safora.server.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final EmergencyContactRepository contactRepository;

    public ProfileController(UserRepository userRepository, EmergencyContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile(@RequestHeader(value = "X-User-Id", required = true) Long userId) {
        return userRepository.findById(userId)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/contacts")
    public ResponseEntity<EmergencyContactDto> addContact(@RequestHeader("X-User-Id") Long userId, @RequestBody EmergencyContactDto contactDto) {
        return userRepository.findById(userId).map(user -> {
            EmergencyContact contact = new EmergencyContact();
            contact.setUser(user);
            contact.setName(contactDto.getName());
            contact.setPhone(contactDto.getPhoneNumber());
            contact.setRelation(contactDto.getRelationship());
            
            EmergencyContact saved = contactRepository.save(contact);
            contactDto.setId(saved.getId());
            return ResponseEntity.ok(contactDto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/contacts/{id}")
    public ResponseEntity<EmergencyContactDto> editContact(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id, @RequestBody EmergencyContactDto contactDto) {
        return contactRepository.findById(id).map(contact -> {
            if (!contact.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403).<EmergencyContactDto>build();
            }
            contact.setName(contactDto.getName());
            contact.setPhone(contactDto.getPhoneNumber());
            contact.setRelation(contactDto.getRelationship());
            contactRepository.save(contact);
            contactDto.setId(contact.getId());
            return ResponseEntity.ok(contactDto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<Void> deleteContact(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return contactRepository.findById(id).map(contact -> {
            if (!contact.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403).<Void>build();
            }
            contactRepository.delete(contact);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    private UserProfileDto mapToDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        
        List<EmergencyContactDto> contacts = user.getEmergencyContacts().stream().map(c -> {
            EmergencyContactDto cdto = new EmergencyContactDto();
            cdto.setId(c.getId());
            cdto.setName(c.getName());
            cdto.setRelationship(c.getRelation());
            cdto.setPhoneNumber(c.getPhone());
            return cdto;
        }).collect(Collectors.toList());
        
        dto.setEmergencyContacts(contacts);
        return dto;
    }
}
