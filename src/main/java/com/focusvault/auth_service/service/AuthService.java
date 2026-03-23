package com.focusvault.auth_service.service;

import com.focusvault.auth_service.dto.AuthResponseDto;
import com.focusvault.auth_service.dto.LoginRequestDto;
import com.focusvault.auth_service.dto.RegisterRequestDto;
import com.focusvault.auth_service.entity.Role;
import com.focusvault.auth_service.entity.User;
import com.focusvault.auth_service.repository.UserRepository;
import com.focusvault.auth_service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ── Register ───────────────────────────────────
    public AuthResponseDto register(RegisterRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponseDto.builder()
                .message("Registration successful")
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .build();
    }

    // ── Login ──────────────────────────────────────
    public AuthResponseDto login(LoginRequestDto request) {

        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check if password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3. Generate JWT token
        String token = jwtService.generateToken(user.getEmail());

        // 4. Return response with token
        return AuthResponseDto.builder()
                .message("Login successful")
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .build();
    }
}