package com.fintech.fintech.application.auth_users.service.Impl;



import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.fintech.fintech.application.auth_users.dtos.UpdatePasswordRequest;
import com.fintech.fintech.application.auth_users.dtos.UserDto;
import com.fintech.fintech.application.auth_users.entities.User;
import com.fintech.fintech.application.res.Response;

public interface UserService {

    User getCurrentLoggedInUser();

    Response<UserDto> getMyProfile();

    Response<Page<UserDto>> getAllusers(int page, int size);

    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest); 
    
    Response<?> uploadProfilPicture(MultipartFile file);
    
}
