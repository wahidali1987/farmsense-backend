package com.farmsense.controller;

import com.farmsense.dto.LoginRequest;
import com.farmsense.dto.LoginResponse;
import com.farmsense.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin // frontend ke liye (React)
public class AuthController {

    private final AuthService authService;

    /* ---------------- LOGIN ---------------- */

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /* ---------------- HEALTH CHECK (OPTIONAL) ---------------- */

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Auth service is running 🚀");
    }

    @GetMapping("/api/test-auth")
    public String test(Authentication authentication) {
        return authentication == null
                ? "NO AUTH"
                : "AUTH OK: " + authentication.getName();
    }

}
