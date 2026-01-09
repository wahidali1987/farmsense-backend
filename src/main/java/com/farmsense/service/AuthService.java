package com.farmsense.service;

import com.farmsense.dto.LoginRequest;
import com.farmsense.dto.LoginResponse;
import com.farmsense.entity.User;
import com.farmsense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /* ---------------- LOGIN ---------------- */

    public LoginResponse login(LoginRequest request) {

        // 1️⃣ User fetch
        User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or user inactive"));

        // 2️⃣ Password verify
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        // 3️⃣ JWT generate
        String token = jwtService.generateToken(user);

        // 4️⃣ Response
        return LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
