package com.cafe.stockmanagement.service;

import com.cafe.stockmanagement.dto.request.ProductRequest;
import com.cafe.stockmanagement.dto.response.ProductResponse;
import com.cafe.stockmanagement.entity.Category;
import com.cafe.stockmanagement.entity.Product;
import com.cafe.stockmanagement.entity.Supplier;
import com.cafe.stockmanagement.exception.BadRequestException;
import com.cafe.stockmanagement.exception.ResourceNotFoundException;
import com.cafe.stockmanagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        return mapToResponse(findProductById(id));
    }

    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse createProduct(ProductRequest request) {
        if (request.getSku() != null &&
                productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException(
                "Product with SKU already exists: " + request.getSku()
            );
        }

        Category category = categoryService.findCategoryById(request.getCategoryId());
        Supplier supplier = request.getSupplierId() != null
                ? supplierService.findSupplierById(request.getSupplierId())
                : null;

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .unitPrice(request.getUnitPrice())
                .unit(request.getUnit())
                .currentStock(0)
                .minimumStock(request.getMinimumStock())
                .category(category)
                .supplier(supplier)
                .isActive(true)
                .build();

        return mapToResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductById(id);

        Category category = categoryService.findCategoryById(request.getCategoryId());
        Supplier supplier = request.getSupplierId() != null
                ? supplierService.findSupplierById(request.getSupplierId())
                : null;

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setUnit(request.getUnit());
        product.setMinimumStock(request.getMinimumStock());
        product.setCategory(category);
        product.setSupplier(supplier);

        return mapToResponse(productRepository.save(product));
    }

    public void deactivateProduct(Long id) {
        Product product = findProductById(id);
        product.setIsActive(false);
        productRepository.save(product);
    }

    // ✅ Use findByIdWithJoins instead of findById
public Product findProductById(Long id) {
    return productRepository.findByIdWithJoins(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Product not found with id: " + id)
            );
}
    // Internal use — saves product (used by StockService)
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    private ProductResponse mapToResponse(Product p) {
    return ProductResponse.builder()
            .id(p.getId())
            .name(p.getName())
            .description(p.getDescription())
            .sku(p.getSku())
            .unitPrice(p.getUnitPrice())
            .unit(p.getUnit())
            .currentStock(p.getCurrentStock())
            .minimumStock(p.getMinimumStock())
            .isLowStock(p.getCurrentStock() <= p.getMinimumStock())
            .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
            .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
            .supplierId(p.getSupplier() != null ? p.getSupplier().getId() : null)
            .supplierName(p.getSupplier() != null ? p.getSupplier().getName() : null)
            .isActive(p.getIsActive())
            .createdAt(p.getCreatedAt())
            .updatedAt(p.getUpdatedAt())
            .build();
}   
}
