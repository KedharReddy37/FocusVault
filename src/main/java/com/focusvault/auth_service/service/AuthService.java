package com.focusvault.auth_service.service;

import com.focusvault.auth_service.dto.AuthResponseDto;
import com.focusvault.auth_service.dto.RegisterRequestDto;
import com.focusvault.auth_service.entity.Role;
import com.focusvault.auth_service.entity.User;
import com.focusvault.auth_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        return AuthResponseDto.builder()
                .message("Registration successful")
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}