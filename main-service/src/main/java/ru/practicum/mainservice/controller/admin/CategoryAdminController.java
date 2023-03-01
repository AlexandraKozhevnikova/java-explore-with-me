package ru.practicum.mainservice.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.CategoryRequest;
import ru.practicum.mainservice.dto.CategoryResponse;
import ru.practicum.mainservice.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@RequestBody @Valid CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @DeleteMapping("{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("{catId}")
    public CategoryResponse updateCategory(@PathVariable Long catId,
                                           @RequestBody @Valid CategoryRequest request) {
        return categoryService.updateCategory(catId, request);
    }
}
