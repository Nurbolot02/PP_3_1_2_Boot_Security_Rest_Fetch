package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import javax.annotation.PostConstruct;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void addRole(Role role) {
        if (!getRoles().contains(role)) {
            roleRepository.save(role);
        }
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();

    }

    @Override
    public Role getRoleByName(String name) {
        Optional<Role> role = roleRepository.findByName(name);

        if (role.isEmpty()) {
            throw new NoResultException("Роли не существует");
        }

        return role.get();
    }

    @PostConstruct
    public void initRoles() {
        Optional<List<Role>> all = Optional.of(roleRepository.findAll());
        if (all.isPresent()) {
            List<Role> roles = all.get();
            if (roles.size() != 2) {
                roleRepository.save(new Role(1L,"ROLE_USER"));
                roleRepository.save(new Role(2L,"ROLE_ADMIN"));
            }
        }
    }
}
