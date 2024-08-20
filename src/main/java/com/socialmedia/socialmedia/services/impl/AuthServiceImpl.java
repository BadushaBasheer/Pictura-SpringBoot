package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.config.CustomUserDetailService;
import com.socialmedia.socialmedia.dto.LoginDTO;
import com.socialmedia.socialmedia.dto.RegisterDTO;
import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.entities.Admin;
import com.socialmedia.socialmedia.entities.Otp;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.enums.EmailTemplateName;
import com.socialmedia.socialmedia.enums.UserRole;
import com.socialmedia.socialmedia.repositories.AdminRepository;
import com.socialmedia.socialmedia.repositories.OtpRepository;
import com.socialmedia.socialmedia.repositories.UserRepository;
import com.socialmedia.socialmedia.response.LoginResponse;
import com.socialmedia.socialmedia.services.AuthService;
import com.socialmedia.socialmedia.services.EmailService;
import com.socialmedia.socialmedia.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;

    private final EmailService emailService;

    private final CustomUserDetailService customUserDetailService;

    private final AdminRepository adminRepository;

    private final UserRepository userRepository;

    private final OtpRepository otpRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Value("${profile.picture}")
    private String pictureUrl;

    private final Map<String, Integer> otpRequestCounts = new ConcurrentHashMap<>();

    private final Map<String, LocalDateTime> otpRequestTimestamps = new ConcurrentHashMap<>();

    private static final int MAX_OTP_REQUESTS = 5;

    private static final int COOLDOWN_MINUTES = 5;

    @Override
    public LoginResponse authenticate(LoginDTO loginDTO) {
        authenticateUser(loginDTO);

        Optional<User> optionalUser = userRepository.findUserByEmail(loginDTO.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            final UserDetails userDetails = customUserDetailService.loadUserByUsername(loginDTO.getEmail());
            Map<String, String> tokens = jwtUtil.createTokens(userDetails);

            return LoginResponse.builder()
                    .token(tokens.get("accessToken"))
                    .refreshToken(tokens.get("refreshToken"))
                    .userId(user.getId())
                    .userRole(user.getUserRole())
                    .expiresIn(jwtUtil.getExpirationTime())
                    .build();

        }
        Optional<Admin> optionalAdmin = adminRepository.findAdminByEmail(loginDTO.getEmail());
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            final UserDetails adminDetails = customUserDetailService.loadUserByUsername(loginDTO.getEmail());
            Map<String, String> tokens = jwtUtil.createTokens(adminDetails);

            return LoginResponse.builder()
                    .token(tokens.get("accessToken"))
                    .refreshToken(tokens.get("refreshToken"))
                    .userId(1L)
                    .userRole(admin.getRoles())
                    .expiresIn(jwtUtil.getExpirationTime())
                    .build();
        } else {
            throw new RuntimeException("User or Admin not found");
        }
    }

    private void authenticateUser(LoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password", e);
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }

    @Override
    @Transactional
    public Object registerUser(RegisterDTO registerDTO) {
        Optional<User> existingUserOptional = userRepository.findByEmail(registerDTO.getEmail());

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            if (!existingUser.isEnabled()) {
                Optional<Otp> existingTokenOptional = otpRepository.findByUser(existingUser);
                if (existingTokenOptional.isPresent()) {
                    Otp existingOtp = existingTokenOptional.get();
                    existingOtp.setExpiresAt(LocalDateTime.now());
                    otpRepository.save(existingOtp);

                    log.info("Expired old otp for user: {}", existingUser.getEmail());
                }

                String otp = generateAndSaveActivationToken(existingUser);
                log.info("Generated new activation otp for user: {}", existingUser.getEmail());

                sendValidationEmail(existingUser, otp);
                log.info("Sent activation email to user: {}", existingUser.getEmail());

                throw new RuntimeException("User already registered but not activated. A new activation token has been sent.");
            } else {
                throw new RuntimeException("User already exists and is activated");
            }
        }

        if (userRepository.existsByName(registerDTO.getName())) {
            return "Name is already taken. Please choose another one.";
        }

        // User does not exist, create a new user
        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setUserRole(UserRole.USER);
        user.setGoogleSignIn(false);
        user.setProfilePic(pictureUrl);
        user.setBackgroundImage(null);
        user.setBio(null);
        user.setCreatedDate(LocalDateTime.now());
        user.setAccountLocked(false);
        user.setEnabled(false);

        User createdUser = userRepository.save(user);
        log.info("Created new user: {}", createdUser.getEmail());

        String token = generateAndSaveActivationToken(createdUser);
        log.info("Generated activation token for new user: {}", createdUser.getEmail());

        // Convert to UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setName(createdUser.getName());
        userDTO.setId(createdUser.getId());
        userDTO.setEmail(createdUser.getEmail());
        userDTO.setGoogleSignIn(createdUser.isGoogleSignIn());
        userDTO.setCreatedDate(createdUser.getCreatedDate());
        userDTO.setUserRole(createdUser.getUserRole());
        userDTO.setEnabled(createdUser.isEnabled());

        sendValidationEmail(createdUser, token);
        log.info("Sent activation email to new user: {}", createdUser.getEmail());

        return userDTO;
    }



    @Override
    @Transactional
    public UserDTO googleSignIn(UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());
        User user;

        if (optionalUser.isPresent()) {
            // User exists, update user details
            user = optionalUser.get();
            user.setEmail(userDTO.getEmail());
            userRepository.save(user);
        } else {
            user = User.builder()
                    .email(userDTO.getEmail())
                    .name(userDTO.getName())
                    .password("")
                    .enabled(true)
                    .isGoogleSignIn(true)
                    .accountLocked(false)
                    .userRole(UserRole.USER)
                    .build();
            userRepository.save(user);
        }

        // Convert to UserDTO
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(user.getId());
        updatedUserDTO.setName(user.getName());
        updatedUserDTO.setGoogleSignIn(user.isGoogleSignIn());
        updatedUserDTO.setEmail(user.getEmail());
        updatedUserDTO.setUserRole(user.getUserRole());

        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        updatedUserDTO.setToken(token);

        return updatedUserDTO;
    }


    @Override
    public boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }


    @Override
    public void activateAccount(String otp) {
        Otp savedOtp = otpRepository.findByOtp(otp)
                .orElseThrow(() -> new RuntimeException("Invalid otp"));
        if (LocalDateTime.now().isAfter(savedOtp.getExpiresAt())) {
            // Generate and save a new otp
            var user = savedOtp.getUser();
            var newToken = generateAndSaveActivationToken(user);

            sendValidationEmail(user, newToken);
            throw new RuntimeException("Activation otp has expired. A new otp has been sent to the same email address");
        }
        var user = userRepository.findById(savedOtp.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedOtp.setValidatedAt(LocalDateTime.now());
        otpRepository.save(savedOtp);
    }

    private String generateAndSaveActivationToken(User user) {
        // Generate a token
        String generatedToken = generateActivationCode();
        var token = Otp.builder()
                .otp(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(1)) // Set the token expiry duration
                .user(user)
                .build();
        otpRepository.save(token);

        return generatedToken;
    }

    private void sendValidationEmail(User user, String newOtp) {
        emailService.sendEmail(
                user.getEmail(),
                user.getUsername(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newOtp,
                "Account activation"
        );
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }


    @Override
    public void resendActivationToken(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.isEnabled()) {
            throw new RuntimeException("Account is already activated");
        }

        // Expire the old token
//      Otp existingOtp = otpRepository.findByUser(user)  //this is only allow the user get only 2 email if beyond that it throws error 400

        Otp existingOtp = otpRepository.findFirstByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new RuntimeException("Activation token not found"));
        existingOtp.setExpiresAt(LocalDateTime.now());
        otpRepository.save(existingOtp);

        // Generate and save a new token
        var newToken = generateAndSaveActivationToken(user);

        // Send the new validation email
        sendValidationEmail(user, newToken);
    }
    //----------------------------------------
}
