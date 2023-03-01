package ru.practicum.mainservice.controller.allUser;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.CategoryResponse;
import ru.practicum.mainservice.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryPublicController {

    private final CategoryService categoryService;

    public CategoryPublicController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("{catId}")
    public CategoryResponse getCategory(@PathVariable Long catId) {
        return categoryService.getCategory(catId);
    }
}
