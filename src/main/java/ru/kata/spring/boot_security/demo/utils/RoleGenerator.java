package ru.kata.spring.boot_security.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.RoleService;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class RoleGenerator {
    @Autowired
    RoleService roleService;

    Set<Role> mainRoles = Set.of(
            new Role(1l,"ROLE_USER"),
            new Role(2l,"ROLE_ADMIN")
    );

    @PostConstruct
    public void addMainRoles() {
        if (roleService.getRoles().size() < 2) {
            mainRoles.forEach(role -> roleService.addRole(role));
        }
    }
}
