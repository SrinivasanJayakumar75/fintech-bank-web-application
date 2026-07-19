package com.fintech.fintech.application.auth_users.service;

import com.fintech.fintech.application.auth_users.dtos.LoginRequest;
import com.fintech.fintech.application.auth_users.dtos.LoginResponse;
import com.fintech.fintech.application.auth_users.dtos.RegistrationRequest;
import com.fintech.fintech.application.auth_users.dtos.ResetPasswordRequest;
import com.fintech.fintech.application.res.Response;

public interface AuthService {

    Response<String> register(RegistrationRequest request);
    Response<LoginResponse> login(LoginRequest loginRequest);
    Response<?> forgetPassword(String email);
    Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);

    
}
