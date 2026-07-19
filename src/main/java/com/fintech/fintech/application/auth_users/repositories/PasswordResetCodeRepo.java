package com.fintech.fintech.application.auth_users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.fintech.application.auth_users.entities.PasswordResetCode;

public interface PasswordResetCodeRepo extends JpaRepository<PasswordResetCode, Long>{

    Optional<PasswordResetCode> findByCode(String code);
    void deleteByUserId(Long userId);


    
}
