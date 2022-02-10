package com.ezez.pastery.service.Impl;

import com.ezez.pastery.model.role.Role;
import com.ezez.pastery.model.role.RoleName;
import com.ezez.pastery.repository.RoleRepository;
import com.ezez.pastery.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);


    @Autowired
    RoleRepository roleRepository;

    @Override
    public Boolean initializeRoles() {
        List<Role> roles = new ArrayList<>();

        List<RoleName> roleNames = new ArrayList<>();
        roleNames.add(RoleName.ROLE_ADMIN);
        roleNames.add(RoleName.ROLE_USER);

        long numberOfRoles = roleRepository.count();
        if(numberOfRoles == 0){
            roleNames.forEach(roleName -> {
                Role role = new Role();
                role.setName(roleName);
                roles.add(role);
            });
            LOGGER.info("Setting Roles");
            LOGGER.info("Roles => "+ roles);
            roleRepository.saveAll(roles);
        }else {
            LOGGER.info("Roles already exists");
        }
        return true;
    }
}
