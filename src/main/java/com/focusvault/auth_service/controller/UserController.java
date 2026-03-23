package com.focusvault.auth_service.controller;

import com.focusvault.auth_service.entity.User;
import com.focusvault.auth_service.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, String>> getProfile(Principal principal) {

        // principal.getName() returns the email from the JWT token
        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, String> profile = new HashMap<>();
        profile.put("fullName", user.getFullName());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole().name());
        profile.put("memberSince", user.getCreatedAt().toString());

        return ResponseEntity.ok(profile);
    }
}