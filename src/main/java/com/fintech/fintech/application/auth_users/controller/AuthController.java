package com.fintech.fintech.application.auth_users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.fintech.application.auth_users.dtos.LoginRequest;
import com.fintech.fintech.application.auth_users.dtos.LoginResponse;
import com.fintech.fintech.application.auth_users.dtos.RegistrationRequest;
import com.fintech.fintech.application.auth_users.dtos.ResetPasswordRequest;
import com.fintech.fintech.application.auth_users.service.AuthService;
import com.fintech.fintech.application.res.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController

@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<String>> register(@RequestBody @Valid RegistrationRequest registrationRequest){
        return ResponseEntity.ok(authService.register(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<Response<?>> forgetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        return ResponseEntity.ok(authService.forgetPassword(resetPasswordRequest.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        return ResponseEntity.ok(authService.updatePasswordViaResetCode(resetPasswordRequest));
    }
    
    
}
