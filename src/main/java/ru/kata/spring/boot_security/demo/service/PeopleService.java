package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.utils.PersonNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService implements UserDetailsService {
    private final UserRepository peopleRepository;

    public PeopleService(UserRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<User> findAll() {
        return peopleRepository.findAll();
    }

    public User findById(long id) {
        Optional<User> byId = peopleRepository.findById(id);
        return byId.orElseThrow(PersonNotFoundException::new);
    }

    @Transactional
    public void save(User person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(long id, User person) {
        person.setId(id);
        peopleRepository.save(person);
    }

    @Transactional
    public void delete(long id, Authentication authentication) {
        if (id != 1) {
            User orPerson = (User) authentication.getPrincipal();
            User byId = peopleRepository.getById(id);
            if (orPerson.getRoles().size() >= 2 || byId.equalsN(orPerson)) {
                byId.getRoles().clear();
                peopleRepository.deleteById(id);
            }
        }
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> byName = peopleRepository.findByUsername(username);
        if (byName.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User with email: %s not found", username));
        }
        return byName.get();
    }

    @Transactional
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        Optional<User> byName = peopleRepository.findByUsername(email);
        if (byName.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User with email: %s not found", email));
        }
        return byName.get();
    }
}
