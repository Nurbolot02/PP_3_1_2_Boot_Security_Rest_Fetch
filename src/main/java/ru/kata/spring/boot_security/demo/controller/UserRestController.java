package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.utils.EmailValidator;
import ru.kata.spring.boot_security.demo.utils.UserNotCreatedException;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserRestController {
    private EmailValidator emailValidator;
    private UserService userService;


    public UserRestController(UserService userService, EmailValidator emailValidator) {
        this.userService = userService;
        this.emailValidator = emailValidator;
    }

    @GetMapping("/currentUser")
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserById(((User) authentication.getPrincipal()).getId());
    }

    @GetMapping("/getUser/{id}")
    public User getUser(@PathVariable(value = "id") long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/getUsers")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/api/admin/delete/")
    public ResponseEntity<HttpStatus> deleteUser(@RequestBody User user) {
        System.out.println(user.toString());
        userService.deleteUser(user.getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/api/admin/adduser")
    public ResponseEntity<HttpStatus> addUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        emailValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder str = new StringBuilder();

            bindingResult.getFieldErrors().forEach(error -> {
                str.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";\n");
            });

            throw new UserNotCreatedException(str.toString());
        }
        userService.addUser(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/api/admin/edit/")
    public ResponseEntity<HttpStatus> editUser(@RequestBody @Valid User user, BindingResult bindingResult, Authentication authentication) {
        User userById = userService.getUserById(user.getId());
        if (userById.getId() == user.getId() && userById.getUsername().equals(user.getUsername())) {

        } else {
            emailValidator.validate(user, bindingResult);
        }

        if (bindingResult.hasErrors()) {
            StringBuilder str = new StringBuilder();

            bindingResult.getFieldErrors().forEach(error -> {
                str.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";\n");
            });

            throw new UserNotCreatedException(str.toString());
        }
        userService.updateUser(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<String> handlerException(UserNotCreatedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
