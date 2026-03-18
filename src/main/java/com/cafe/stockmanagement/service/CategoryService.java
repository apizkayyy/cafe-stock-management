package com.cafe.stockmanagement.service;

import com.cafe.stockmanagement.dto.request.CategoryRequest;
import com.cafe.stockmanagement.dto.response.CategoryResponse;
import com.cafe.stockmanagement.entity.Category;
import com.cafe.stockmanagement.exception.BadRequestException;
import com.cafe.stockmanagement.exception.ResourceNotFoundException;
import com.cafe.stockmanagement.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        return mapToResponse(findCategoryById(id));
    }

    // Internal method — returns raw entity for other services
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Category not found with id: " + id)
                );
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                "Category already exists: " + request.getName()
            );
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryById(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return mapToResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        categoryRepository.delete(findCategoryById(id));
    }

    // Mapper — Entity → DTO (no lazy fields touched!)
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }
}