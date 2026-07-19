package com.fintech.fintech.application.auth_users.service.Impl;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fintech.fintech.application.account.entities.Account;
import com.fintech.fintech.application.account.service.AccountService;
import com.fintech.fintech.application.auth_users.dtos.LoginRequest;
import com.fintech.fintech.application.auth_users.dtos.LoginResponse;
import com.fintech.fintech.application.auth_users.dtos.RegistrationRequest;
import com.fintech.fintech.application.auth_users.dtos.ResetPasswordRequest;
import com.fintech.fintech.application.auth_users.entities.PasswordResetCode;
import com.fintech.fintech.application.auth_users.entities.User;
import com.fintech.fintech.application.auth_users.repositories.PasswordResetCodeRepo;
import com.fintech.fintech.application.auth_users.repositories.UserRepo;
import com.fintech.fintech.application.auth_users.service.AuthService;
import com.fintech.fintech.application.auth_users.service.CodeGenerator;
import com.fintech.fintech.application.enums.AccountType;
import com.fintech.fintech.application.enums.Currency;
import com.fintech.fintech.application.exceptions.BadRequestException;
import com.fintech.fintech.application.exceptions.NotFoundExceptions;
import com.fintech.fintech.application.notification.dtos.NotificationDto;
import com.fintech.fintech.application.notification.service.NotificationService;
import com.fintech.fintech.application.res.Response;
import com.fintech.fintech.application.role.entities.Role;
import com.fintech.fintech.application.role.repositories.RoleRepo;
import com.fintech.fintech.application.security.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImplementation implements AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final NotificationService notificationService;
    private final CodeGenerator codeGenerator;
    private final PasswordResetCodeRepo passwordResetCodeRepo;
    private final AccountService accountService;

    @Value("${password.reset.link}")
    private String resetLink;
    

    @Override
    public Response<String> register(RegistrationRequest request) {
        List<Role> roles;

        if(request.getRoles()==null || request.getRoles().isEmpty()){
            Role defaultRole = roleRepo.findByname("CUSTOMER")
            .orElseThrow(()-> new NotFoundExceptions("CUSTOMER ROLE NOT FOUND"));

            roles = Collections.singletonList(defaultRole);
        } else{
            roles = request.getRoles().stream()
            .map(roleName->roleRepo.findByname(roleName)
        .orElseThrow(()->new NotFoundExceptions("ROLE NOT FOUND" + roleName)))
        .toList();
        }

        if(userRepo.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestException("Email Already Present");
        }

        User user = User.builder()
        .firstName(request.getFirstname())
        .lastName(request.getLastname())
        .email(request.getEmail())
        .phoneNumber(request.getPhoneNumber())
        .password(passwordEncoder.encode(request.getPassword()))
        .roles(roles)
        .active(true)
        .build();

        User savedUser = userRepo.save(user);

        Account savedAccount = accountService.createAccount(AccountType.SAVINGS, savedUser);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", savedUser.getFirstName());

        NotificationDto notificationDto = NotificationDto.builder()
        .recipient(savedUser.getEmail())
        .subject("Welcome to fintech bank")
        .templateName("welcome")
        .templateVariables(vars)
        .build();

        notificationService.sendEmail(notificationDto, savedUser);

        
          Map<String, Object> accountVars = new HashMap<>();
          accountVars.put("name", savedUser.getFirstName());
          accountVars.put("accountNumber", savedAccount.getAccountNumber());
          accountVars.put("accountType", AccountType.SAVINGS.name());
          accountVars.put("currency",Currency.USD);

        NotificationDto accountCreatedEmail = NotificationDto.builder()
        .recipient(savedUser.getEmail())
        .subject("Your new bank account has been created 😊")
        .templateName("account-created")
        .templateVariables(accountVars)
        .build();

        notificationService.sendEmail(accountCreatedEmail, savedUser);

        return Response.<String>builder()
        .statusCode(HttpStatus.OK.value())
        .message("your account has been created successfully")
        .data("Email of your account details has been sent to you. Your account number is: "+ savedAccount.getAccountNumber())
        .build();
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = userRepo.findByEmail(email).orElseThrow(()->new NotFoundExceptions("Email not found"));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadRequestException("Password doesn't match");
        }

        String token = tokenService.generateToken(user.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
        .roles(user.getRoles().stream().map(Role::getName).toList())
        .token(token)
        .build();

        return Response.<LoginResponse>builder()
        .statusCode(HttpStatus.OK.value())
        .message("Login Successfull")
        .data(loginResponse)
        .build();

        
    }

    @Override
    @Transactional
    public Response<?> forgetPassword(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(()-> new NotFoundExceptions("User not found"));
        passwordResetCodeRepo.deleteByUserId(user.getId());

        String code = codeGenerator.generateUniqueCode();

        PasswordResetCode resetCode = PasswordResetCode.builder()
        .user(user)
        .code(code)
        .expiryDate(calculateExpiryDate())
        .used(false)
        .build();

        passwordResetCodeRepo.save(resetCode);

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());
        templateVariables.put("resetLink", resetLink + code);

        NotificationDto notificationDto = NotificationDto.builder()
        .recipient(user.getEmail())
        .subject("Password Reset Code")
        .templateName("password-reset")
        .templateVariables(templateVariables)
        .build();

        notificationService.sendEmail(notificationDto, user);

        return Response.builder()
        .statusCode(HttpStatus.OK.value())
        .message("Password reset code sent to your email")
        .build();
        
    }

    @Override
    @Transactional
    public Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {
        String code = resetPasswordRequest.getCode();
        String newPassword = resetPasswordRequest.getNewPassword();

        PasswordResetCode resetCode = passwordResetCodeRepo.findByCode(code)
        .orElseThrow(()-> new BadRequestException("Invalid reset code"));

        if(resetCode.getExpiryDate().isBefore(LocalDateTime.now())){
            passwordResetCodeRepo.delete(resetCode);
            throw new BadRequestException("Reset code has expired");
        }

        User user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        passwordResetCodeRepo.delete(resetCode);

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());

        NotificationDto confirmationEmail = NotificationDto.builder()
        .recipient(user.getEmail())
        .subject("Password updated successfully")
        .templateName("Password-update-confirmation")
        .templateVariables(templateVariables)
        .build();

        notificationService.sendEmail(confirmationEmail, user);

        return Response.builder()
        .statusCode(HttpStatus.OK.value())
        .message("password updated successfully")
        .build();
    }

    private LocalDateTime calculateExpiryDate(){
        return LocalDateTime.now().plusHours(5);
    }

    
    
}
