package ru.practicum.mainservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.CategoryRequest;
import ru.practicum.mainservice.dto.CategoryResponse;
import ru.practicum.mainservice.mapper.CategoryMapper;
import ru.practicum.mainservice.model.CategoryEntity;
import ru.practicum.mainservice.repository.CategoryRepository;

import java.util.NoSuchElementException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        CategoryEntity category = categoryRepository.save(categoryMapper.entityFromNewRequest(request));
        return categoryMapper.responseFromEntity(category);
    }

    @Transactional
    public void deleteCategory(Long catId) {
        checkCategoryIsExistAndGet(catId);
        categoryRepository.deleteById(catId);
    }


    @Transactional(readOnly = true)
    public CategoryEntity checkCategoryIsExistAndGet(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NoSuchElementException("Category with id=" + catId + " was not found"));
    }

    @Transactional
    public CategoryResponse updateCategory(Long catId, CategoryRequest request) {
        CategoryEntity category = checkCategoryIsExistAndGet(catId);
        category.setName(request.getName());
        return categoryMapper.responseFromEntity(category);
    }
}
