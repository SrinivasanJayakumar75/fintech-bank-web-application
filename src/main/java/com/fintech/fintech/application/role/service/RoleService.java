package com.fintech.fintech.application.role.service;

import java.util.List;

import com.fintech.fintech.application.res.Response;
import com.fintech.fintech.application.role.entities.Role;

public interface RoleService {

    Response<Role> createRole(Role roleRequest);
    Response<Role> updateRole(Role roleRequest);
    Response<List<Role>> getAllRoles();
    Response<?> deleteRole(Long id);
}
