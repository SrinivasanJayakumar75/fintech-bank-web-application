package com.fintech.fintech.application.auth_users.service.Impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fintech.fintech.application.auth_users.dtos.UpdatePasswordRequest;
import com.fintech.fintech.application.auth_users.dtos.UserDto;
import com.fintech.fintech.application.auth_users.entities.User;
import com.fintech.fintech.application.auth_users.repositories.UserRepo;
import com.fintech.fintech.application.exceptions.BadRequestException;
import com.fintech.fintech.application.exceptions.NotFoundExceptions;
import com.fintech.fintech.application.notification.dtos.NotificationDto;
import com.fintech.fintech.application.notification.service.NotificationService;
import com.fintech.fintech.application.res.Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepo userRepo;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private final String uploadDir = "uploads/profile-pictures";
    
    
    
    
    @Override
    public User getCurrentLoggedInUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null){
            throw new NotFoundExceptions("User is not authenticated");
        }
        String email=authentication.getName();
        return userRepo.findByEmail(email).orElseThrow(()-> new NotFoundExceptions("user not found"));
    }

    @Override
    public Response<UserDto> getMyProfile() {

        User user = getCurrentLoggedInUser();
        UserDto userDto = modelMapper.map(user, UserDto.class);

        return Response.<UserDto>builder()
        .statusCode(HttpStatus.OK.value())
        .message("User retrieved")
        .data(userDto)
        .build();
    }

    @Override
    public Response<Page<UserDto>> getAllusers(int page, int size) {
        Page<User> users = userRepo.findAll(PageRequest.of(page, size));

        Page<UserDto> userDtos = users.map(user -> modelMapper.map(user,UserDto.class));

        return Response.<Page<UserDto>>builder()
        .statusCode(HttpStatus.OK.value())
        .message("Users retrieved")
        .data(userDtos)
        .build();
    }

    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User user = getCurrentLoggedInUser();

        String newPassword = updatePasswordRequest.getNewPassword();
        String oldPassword = updatePasswordRequest.getOldPassword();

        if(oldPassword == null || newPassword == null){
            throw new BadRequestException("old and new password required");
        }

        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new BadRequestException("old password not correct");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepo.save(user);

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());

        NotificationDto notificationDto = NotificationDto.builder()
        .recipient(user.getEmail())
        .subject("Your password was successfully changed")
        .templateName("password-change")
        .templateVariables(templateVariables)
        .build();

        notificationService.sendEmail(notificationDto, user);

        return Response.builder()
        .statusCode(HttpStatus.OK.value())
        .message("Password changed successfully")
        .build();


    }

    @Override
    public Response<?> uploadProfilPicture(MultipartFile file) {
        
        User user = getCurrentLoggedInUser();

        try{
            Path uploadPath = Paths.get(uploadDir);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            if(user.getProfilePictureUrl() !=null && !user.getProfilePictureUrl().isEmpty()){
                Path oldFile = Paths.get(user.getProfilePictureUrl());
                if(Files.exists(oldFile)){
                    Files.delete(oldFile);
                }
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";

            if(originalFileName != null && originalFileName.contains(".")){
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String newFileName = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath);
            String fileUrl = uploadDir + newFileName;

            user.setProfilePictureUrl(fileUrl);
            userRepo.save(user);

            return Response.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Profile picture uploaded successfully")
            .data(fileUrl)
            .build();

        } catch(IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

     
    
}
