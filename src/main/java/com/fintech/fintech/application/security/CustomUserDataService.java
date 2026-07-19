package com.fintech.fintech.application.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fintech.fintech.application.auth_users.entities.User;
import com.fintech.fintech.application.auth_users.repositories.UserRepo;
import com.fintech.fintech.application.exceptions.NotFoundExceptions;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Service
public class CustomUserDataService implements UserDetailsService{

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(username)
                .orElseThrow(()-> new NotFoundExceptions("Email Not Found"));

        return AuthUser.builder()
        .user(user)
        .build();              
        
    }
    
}
