package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Optional;

/**
 * @author Neil Alishev
 */
@Service
public class RegistrationService {

    private final UserRepository peopleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository peopleRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(User person) {

        person.setPassword(passwordEncoder.encode(person.getPassword()));

        person.addRole(roleRepository.findByName("ROLE_USER").get());
        if ( person.getRole() != null && person.getRole().equals("ROLE_ADMIN")) {
            person.addRole(roleRepository.findByName(person.getRole()).get());
        }
        peopleRepository.save(person);
    }

    @Transactional
    public void update(long id, User person) {
        Optional<User> byId1 = peopleRepository.findById(id);
        if (byId1.isPresent()) {
            if (person.getId() != 1) {
                person.setPassword(passwordEncoder.encode(person.getPassword()));
                person.addRole(roleRepository.findByName("ROLE_USER").get());
                if (person.getRole().equals("ROLE_ADMIN")) {
                    person.addRole(roleRepository.findByName(person.getRole()).get());
                }
                peopleRepository.save(person);
            }
        }
    }
}
