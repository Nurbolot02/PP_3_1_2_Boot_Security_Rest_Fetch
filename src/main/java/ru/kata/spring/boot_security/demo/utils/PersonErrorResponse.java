package ru.kata.spring.boot_security.demo.utils;



import java.time.LocalDateTime;

public class PersonErrorResponse {
    private String massage;
    private LocalDateTime localDateTime;

    public PersonErrorResponse(String massage, LocalDateTime localDateTime) {
        this.massage = massage;
        this.localDateTime = localDateTime;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
