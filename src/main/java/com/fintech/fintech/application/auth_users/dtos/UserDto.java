package com.fintech.fintech.application.auth_users.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fintech.fintech.application.account.dtos.AccountDtos;
import com.fintech.fintech.application.role.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private Long id;
    
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private String email;
    @JsonInclude
    private String password;
    private String profilePictureUrl;

    private boolean active;

    private List<Role> roles;

    @JsonManagedReference
    private List<AccountDtos> accounts;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
}
