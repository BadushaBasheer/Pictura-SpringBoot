package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.dto.LoginDTO;
import com.socialmedia.socialmedia.dto.RegisterDTO;
import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.response.LoginResponse;

public interface AuthService {

    Object registerUser(RegisterDTO registerDTO);

    boolean hasUserWithEmail(String email);

    LoginResponse authenticate(LoginDTO loginDTO);

    void activateAccount(String token);

    UserDTO googleSignIn(UserDTO userDTO);

    void resendActivationToken(String email);

}
