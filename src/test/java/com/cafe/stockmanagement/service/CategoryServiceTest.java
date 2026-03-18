package com.cafe.stockmanagement.service;

import com.cafe.stockmanagement.dto.request.CategoryRequest;
import com.cafe.stockmanagement.dto.response.CategoryResponse;
import com.cafe.stockmanagement.entity.Category;
import com.cafe.stockmanagement.exception.BadRequestException;
import com.cafe.stockmanagement.exception.ResourceNotFoundException;
import com.cafe.stockmanagement.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category mockCategory;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .name("Coffee Beans")
                .description("All types of coffee beans")
                .build();

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Coffee Beans");
        categoryRequest.setDescription("All types of coffee beans");
    }

    @Test
    @DisplayName("Should return all categories")
    void getAllCategories_ReturnsAllCategories() {
        // ARRANGE
        List<Category> categories = Arrays.asList(
            mockCategory,
            Category.builder().name("Dairy").description("Milk products").build()
        );
        when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(categories);

        // ACT
        List<CategoryResponse> result = categoryService.getAllCategories();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Coffee Beans");
    }

    @Test
    @DisplayName("Should create category successfully")
    void createCategory_Success() {
        // ARRANGE
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(mockCategory);

        // ACT
        CategoryResponse result = categoryService.createCategory(categoryRequest);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Coffee Beans");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when category name already exists")
    void createCategory_DuplicateName_ThrowsException() {
        // ARRANGE
        when(categoryRepository.existsByName(anyString())).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Category already exists");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void getCategoryById_NotFound_ThrowsException() {
        // ARRANGE
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> categoryService.findCategoryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: 999");
    }

    @Test
    @DisplayName("Should update category successfully")
    void updateCategory_Success() {
        // ARRANGE
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(mockCategory);

        categoryRequest.setName("Updated Name");
        categoryRequest.setDescription("Updated Description");

        // ACT
        CategoryResponse result = categoryService.updateCategory(1L, categoryRequest);

        // ASSERT
        assertThat(result).isNotNull();
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}