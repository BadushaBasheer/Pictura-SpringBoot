package com.socialmedia.socialmedia.response;

import com.socialmedia.socialmedia.enums.UserRole;
import lombok.Builder;

import java.util.Objects;

@Builder
public record LoginResponse(String token, Long userId, UserRole userRole, Long expiresIn) {

    public LoginResponse build() {
        Objects.requireNonNull(token, "Token must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(userRole, "User role must not be null");
        Objects.requireNonNull(expiresIn, "Expires In must not be null");

        return new LoginResponse(token, userId, userRole, expiresIn);
    }
}

