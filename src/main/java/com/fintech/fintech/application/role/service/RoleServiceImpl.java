package com.fintech.fintech.application.role.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fintech.fintech.application.exceptions.BadRequestException;
import com.fintech.fintech.application.exceptions.NotFoundExceptions;
import com.fintech.fintech.application.res.Response;
import com.fintech.fintech.application.role.entities.Role;
import com.fintech.fintech.application.role.repositories.RoleRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    private final RoleRepo roleRepo;
    @Override
    public Response<Role> createRole(Role roleRequest) {
        if(roleRepo.findByname(roleRequest.getName()).isPresent()){
            throw new BadRequestException("Role already exists");
        }
        Role savedRole = roleRepo.save(roleRequest);

        return Response.<Role>builder()
        .statusCode(HttpStatus.OK.value())
        .message("role saved successfully")
        .data(savedRole)
        .build();
    
    }

    @Override
    public Response<Role> updateRole(Role roleRequest) {
        Role role = roleRepo.findById(roleRequest.getId())
        .orElseThrow(()-> new NotFoundExceptions("Role not found"));

        role.setName(roleRequest.getName());
        Role updatedRole = roleRepo.save(role);

        return Response.<Role>builder()
        .statusCode(HttpStatus.OK.value())
        .message("role updated successfully")
        .data(updatedRole)
        .build();
    
    }

    @Override
    public Response<List<Role>> getAllRoles() {

        List<Role> roles = roleRepo.findAll();

        return Response.<List<Role>>builder()
        .statusCode(HttpStatus.OK.value())
        .message("Role retrieved successfully")
        .data(roles)
        .build();
       
    }

    @Override
    public Response<?> deleteRole(Long id) {

        if(!roleRepo.existsById(id)){
            throw new NotFoundExceptions("Role not found");
        }

        roleRepo.deleteById(id);

        return Response.builder()
        .statusCode(HttpStatus.OK.value())
        .message("Role deleted successfully")
        .build();
    }

    
}
