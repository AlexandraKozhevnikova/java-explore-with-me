package ru.practicum.mainservice.dto;


import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class CategoryRequest {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name.trim();
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryRequest that = (CategoryRequest) o;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
