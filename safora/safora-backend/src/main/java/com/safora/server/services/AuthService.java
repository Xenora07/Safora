package com.safora.server.services;

import com.safora.server.dtos.AuthResponse;
import com.safora.server.dtos.LoginRequest;
import com.safora.server.dtos.RegisterRequest;
import com.safora.server.entities.User;
import com.safora.server.repositories.UserRepository;
import com.safora.server.utils.PasswordUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(PasswordUtils.hashPassword(request.getPassword()));

        userRepository.save(user);

        return new AuthResponse(UUID.randomUUID().toString(), user.getId(), user.getFullName(), "Registration successful");
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> optUser = userRepository.findByEmail(request.getEmail());
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = optUser.get();
        if (!user.getPasswordHash().equals(PasswordUtils.hashPassword(request.getPassword()))) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return new AuthResponse(UUID.randomUUID().toString(), user.getId(), user.getFullName(), "Login successful");
    }
}
