package com.fintech.fintech.application.role.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.fintech.application.role.entities.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByname(String name);
}
