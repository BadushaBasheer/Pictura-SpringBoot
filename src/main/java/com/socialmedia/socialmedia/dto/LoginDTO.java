package com.socialmedia.socialmedia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "Email should not be blank")
    @NotNull
    private String email;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 8, max = 12, message = "Password must be between 8 to 12 characters")
    @NotNull
    private String password;

}
