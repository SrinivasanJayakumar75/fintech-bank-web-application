package com.fintech.fintech.application.auth_users.dtos;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotBlank(message = "FirstName is required")
    private String firstname;

    private String lastname;

    private String phoneNumber;

    @NotBlank(message = "Email is Required")
    @Email
    private String email;

    private List<String> roles;

    @NotBlank(message = "Password is required")
    private String password;


    
}
