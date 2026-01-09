package com.farmsense.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LoginResponse {

    private String token;
    private UUID userId;
    private String name;
    private String email;
    private String role;
}
