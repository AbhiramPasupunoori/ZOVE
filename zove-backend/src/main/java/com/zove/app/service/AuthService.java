package com.zove.app.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.zove.app.dto.AuthDtos.AuthResponse;
import com.zove.app.dto.AuthDtos.LoginRequest;
import com.zove.app.dto.AuthDtos.RegisterRequest;
import com.zove.app.dto.AuthDtos.UserResponse;
import com.zove.app.model.AppUser;
import com.zove.app.repository.UserRepository;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        var email = normalizeEmail(request.email());
        var username = request.username().trim();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(CONFLICT, "Email is already registered");
        }

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(CONFLICT, "Username is already taken");
        }

        var user = new AppUser(
                request.displayName().trim(),
                username,
                email,
                passwordEncoder.encode(request.password())
        );
        var savedUser = userRepository.save(user);
        return authResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return authResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::toUserResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    private AuthResponse authResponse(AppUser user) {
        var token = jwtService.issueToken(user);
        return new AuthResponse(token.value(), token.expiresAt(), toUserResponse(user));
    }

    private UserResponse toUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getDisplayName(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getCreatedAt()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
