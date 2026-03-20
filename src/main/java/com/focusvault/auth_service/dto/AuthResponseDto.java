package com.focusvault.auth_service.dto;

public class AuthResponseDto {

    private String message;
    private String email;
    private String role;

    public AuthResponseDto() {}

    public AuthResponseDto(String message, String email, String role) {
        this.message = message;
        this.email = email;
        this.role = role;
    }

    public String getMessage() { return message; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public void setMessage(String message) { this.message = message; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }

    public static AuthResponseDtoBuilder builder() { return new AuthResponseDtoBuilder(); }

    public static class AuthResponseDtoBuilder {
        private String message;
        private String email;
        private String role;

        public AuthResponseDtoBuilder message(String message) { this.message = message; return this; }
        public AuthResponseDtoBuilder email(String email) { this.email = email; return this; }
        public AuthResponseDtoBuilder role(String role) { this.role = role; return this; }

        public AuthResponseDto build() {
            return new AuthResponseDto(message, email, role);
        }
    }
}