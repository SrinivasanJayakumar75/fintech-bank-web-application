package com.fintech.fintech.application.auth_users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.fintech.application.auth_users.entities.User;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
}
