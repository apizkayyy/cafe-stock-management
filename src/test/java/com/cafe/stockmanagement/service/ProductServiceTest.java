package com.cafe.stockmanagement.service;

import com.cafe.stockmanagement.dto.request.ProductRequest;
import com.cafe.stockmanagement.dto.response.ProductResponse;
import com.cafe.stockmanagement.entity.Category;
import com.cafe.stockmanagement.entity.Product;
import com.cafe.stockmanagement.exception.ResourceNotFoundException;
import com.cafe.stockmanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryService categoryService;
    @Mock private SupplierService supplierService;

    @InjectMocks
    private ProductService productService;

    private Product mockProduct;
    private Category mockCategory;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .name("Coffee Beans")
                .build();

        mockProduct = Product.builder()
                .name("Arabica Coffee")
                .sku("COFFEE-001")
                .unitPrice(new BigDecimal("25.00"))
                .unit("kg")
                .currentStock(100)
                .minimumStock(10)
                .category(mockCategory)
                .isActive(true)
                .build();

        productRequest = new ProductRequest();
        productRequest.setName("Arabica Coffee");
        productRequest.setSku("COFFEE-001");
        productRequest.setUnitPrice(new BigDecimal("25.00"));
        productRequest.setUnit("kg");
        productRequest.setMinimumStock(10);
        productRequest.setCategoryId(1L);
    }

    @Test
    @DisplayName("Should return all active products")
    void getAllProducts_ReturnsActiveProducts() {
        // ARRANGE
        when(productRepository.findByIsActiveTrue())
            .thenReturn(Arrays.asList(mockProduct));

        // ACT
        List<ProductResponse> result = productService.getAllProducts();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Arabica Coffee");
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_Success() {
        // ARRANGE
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(categoryService.findCategoryById(anyLong())).thenReturn(mockCategory);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // ACT
        ProductResponse result = productService.createProduct(productRequest);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Arabica Coffee");
        assertThat(result.getCategoryName()).isEqualTo("Coffee Beans");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should return low stock products")
    void getLowStockProducts_ReturnsLowStockItems() {
        // ARRANGE — product where currentStock(5) <= minimumStock(10)
        Product lowStockProduct = Product.builder()
                .name("Low Stock Item")
                .currentStock(5)
                .minimumStock(10)
                .category(mockCategory)
                .isActive(true)
                .build();

        when(productRepository.findLowStockProducts())
            .thenReturn(Arrays.asList(lowStockProduct));

        // ACT
        List<ProductResponse> result = productService.getLowStockProducts();

        // ASSERT
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsLowStock()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void findProductById_NotFound_ThrowsException() {
        // ARRANGE
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> productService.findProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
    }

    @Test
    @DisplayName("Should deactivate product")
    void deactivateProduct_Success() {
        // ARRANGE
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // ACT
        productService.deactivateProduct(1L);

        // ASSERT
        assertThat(mockProduct.getIsActive()).isFalse();
        verify(productRepository, times(1)).save(mockProduct);
    }
}