package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.addRole(roleService.getRoleByName("ROLE_USER"));
        if (user.getRole() != null && !user.getRoles().contains(roleService.getRoleByName(user.getRole()))) {
            user.addRole(roleService.getRoleByName(user.getRole()));
        }
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found!");
        }

        return user.get();
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void updateUser(User newUser) {
        long id = newUser.getId();
        Role newRole = roleService.getRoleByName(newUser.getRole());
        User currentUser = getUserById(id);
        Set<Role> currentUserRoles = currentUser.getRoles();

        if (newUser.getPassword() == null || newUser.getPassword().isEmpty()) {
            newUser.setPassword(currentUser.getPassword());
        } else {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }

        if (!currentUserRoles.contains(newRole)) {
            currentUserRoles.add(newRole);
            newUser.setRoles(currentUserRoles);
        } else if (newRole == roleService.getRoleByName("ROLE_USER")) {
            currentUserRoles.clear();
            currentUserRoles.add(newRole);
            newUser.setRoles(currentUserRoles);
        } else {
            newUser.setRoles(currentUserRoles);
        }

        userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
