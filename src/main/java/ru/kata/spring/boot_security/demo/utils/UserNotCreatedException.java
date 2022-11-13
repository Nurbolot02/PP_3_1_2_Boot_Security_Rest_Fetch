package ru.kata.spring.boot_security.demo.utils;

public class UserNotCreatedException  extends RuntimeException{
    public UserNotCreatedException(String message) {
        super(message);
    }
}
