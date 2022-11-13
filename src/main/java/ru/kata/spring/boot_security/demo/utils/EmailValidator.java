package ru.kata.spring.boot_security.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.PeopleService;

@Component
public class EmailValidator implements Validator {
    private final PeopleService peopleService;

    @Autowired
    public EmailValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User person = (User) o;

        try {
            peopleService.loadUserByEmail(person.getUsername());
        } catch (Exception ignored) {
            return; // все ок, пользователь не найден
        }

        errors.rejectValue("username", "", "Человек с такой почтой уже существует");
    }
}
