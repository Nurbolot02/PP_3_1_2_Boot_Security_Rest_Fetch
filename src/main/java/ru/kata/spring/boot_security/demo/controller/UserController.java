package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "/auth/login";
    }

    @GetMapping("/user")
    public String userDetails() {
        return "user/index";
    }

    @GetMapping("/admin")
    public String printUsers() {
        return "admin/index";
    }

}
