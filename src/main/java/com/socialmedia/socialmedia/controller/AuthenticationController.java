package com.socialmedia.socialmedia.controller;

import com.socialmedia.socialmedia.config.CustomUserDetailService;
import com.socialmedia.socialmedia.dto.LoginDTO;
import com.socialmedia.socialmedia.dto.RegisterDTO;
import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.response.AuthResponse;
import com.socialmedia.socialmedia.response.LoginResponse;
import com.socialmedia.socialmedia.response.RefreshResponse;
import com.socialmedia.socialmedia.services.AuthService;
import com.socialmedia.socialmedia.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtUtil jwtUtil;

    private final AuthService authService;

    private final CustomUserDetailService customUserDetailService;


    @PostMapping("/login")
    public LoginResponse authenticate(@RequestBody @Valid LoginDTO loginDTO) {
        return authService.authenticate(loginDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<?> signUpUser(@RequestBody @Valid RegisterDTO registerDTO) {
        if (authService.hasUserWithEmail(registerDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User already exist with this email");
        }
        var createdUser = authService.registerUser(registerDTO);
        if (createdUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not created, please try again later!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }


    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }


    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, Object> data) {
        try {
            // Extract the relevant data from the request
            Map<String, Object> googleData = (Map<String, Object>) data.get("data");
            String email = (String) googleData.get("email");
            String name = (String) googleData.get("name");

            // Create UserDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(email);
            userDTO.setName(name);

            // Process Google sign-in
            UserDTO responseUserDTO = authService.googleSignIn(userDTO);

            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google login failed");
        }
    }

    @GetMapping("/activate-account")
    public void confirm(@RequestParam String token) {
        authService.activateAccount(token);
    }

    @GetMapping("/resend-otp")
    public ResponseEntity<String> resendActivationToken(@RequestParam String email) {
        try {
            authService.resendActivationToken(email);
            return ResponseEntity.ok("Activation token has been resent to your email address.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshResponse refreshResponse) {
        String refreshToken = refreshResponse.getRefreshToken();
        if (jwtUtil.isRefreshTokenValid(refreshToken, this.customUserDetailService.loadUserByUsername(jwtUtil.extractUsername(refreshToken)))) {
            UserDetails userDetails = this.customUserDetailService.loadUserByUsername(jwtUtil.extractUsername(refreshToken));
            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }
}
