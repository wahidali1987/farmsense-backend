package com.farmsense.service;

import com.farmsense.entity.User;
import com.farmsense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* ---------------- CREATE USER ---------------- */

    public User createUser(String name, String email, String rawPassword, String role) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User already exists with email: " + email);
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    /* ---------------- GET USER ---------------- */

    public User getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getActiveUserByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("User not found or inactive"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /* ---------------- UPDATE USER ---------------- */

    public User updateUser(UUID userId, String name, String role) {
        User user = getById(userId);

        user.setName(name);
        user.setRole(role);

        return userRepository.save(user);
    }

    /* ---------------- PASSWORD ---------------- */

    public void updatePassword(UUID userId, String newPassword) {
        User user = getById(userId);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /* ---------------- ACTIVATE / DEACTIVATE ---------------- */

    public void deactivateUser(UUID userId) {
        User user = getById(userId);
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void activateUser(UUID userId) {
        User user = getById(userId);
        user.setIsActive(true);
        userRepository.save(user);
    }
}
