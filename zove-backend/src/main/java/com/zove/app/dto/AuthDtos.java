package com.zove.app.dto;

import java.time.Instant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 2, max = 80) String displayName,
            @NotBlank @Pattern(regexp = "^[a-zA-Z0-9_]{3,40}$") String username,
            @NotBlank @Email @Size(max = 160) String email,
            @NotBlank @Size(min = 8, max = 120) String password
    ) {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record AuthResponse(
            String token,
            Instant expiresAt,
            UserResponse user
    ) {
    }

    public record UserResponse(
            Long id,
            String displayName,
            String username,
            String email,
            String bio,
            String avatarUrl,
            Instant createdAt
    ) {
    }
}
