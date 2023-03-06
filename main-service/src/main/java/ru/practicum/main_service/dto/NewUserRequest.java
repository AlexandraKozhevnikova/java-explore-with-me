package ru.practicum.main_service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

public class NewUserRequest {
    @NotBlank
    @Size(max = 255)
    private String name;
    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name.trim();
        this.name = name;
    }

    public String getEmail() {
        email = email.trim();
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewUserRequest request = (NewUserRequest) o;

        if (!Objects.equals(name, request.name)) return false;
        return Objects.equals(email, request.email);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
