package ru.practicum.mainservice.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class NewUserRequest {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
