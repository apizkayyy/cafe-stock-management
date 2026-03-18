package com.cafe.stockmanagement.service;

import com.cafe.stockmanagement.dto.request.StockTransactionRequest;
import com.cafe.stockmanagement.dto.response.StockTransactionResponse;
import com.cafe.stockmanagement.entity.Category;
import com.cafe.stockmanagement.entity.Product;
import com.cafe.stockmanagement.entity.StockTransaction;
import com.cafe.stockmanagement.entity.User;
import com.cafe.stockmanagement.enums.Role;
import com.cafe.stockmanagement.enums.TransactionType;
import com.cafe.stockmanagement.exception.BadRequestException;
import com.cafe.stockmanagement.repository.StockTransactionRepository;
import com.cafe.stockmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockService Tests")
class StockServiceTest {

    @Mock private StockTransactionRepository transactionRepository;
    @Mock private ProductService productService;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private StockService stockService;

    private Product mockProduct;
    private User mockUser;
    private StockTransactionRequest restockRequest;
    private StockTransactionRequest usageRequest;

    @BeforeEach
    void setUp() {
        // Setup mock product
        mockProduct = Product.builder()
                .name("Arabica Coffee")
                .currentStock(50)
                .minimumStock(10)
                .category(Category.builder().name("Coffee").build())
                .isActive(true)
                .build();

        // Setup mock user
        mockUser = User.builder()
                .name("Hafiz")
                .email("hafiz@cafe.com")
                .role(Role.ROLE_ADMIN)
                .build();

        // Setup restock request
        restockRequest = new StockTransactionRequest();
        restockRequest.setProductId(1L);
        restockRequest.setType(TransactionType.RESTOCK);
        restockRequest.setQuantity(20);
        restockRequest.setNotes("Restock from supplier");

        // Setup usage request
        usageRequest = new StockTransactionRequest();
        usageRequest.setProductId(1L);
        usageRequest.setType(TransactionType.USAGE);
        usageRequest.setQuantity(10);

        // Mock SecurityContextHolder (simulates logged-in user)
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("hafiz@cafe.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should process RESTOCK transaction correctly")
    void processTransaction_Restock_IncreasesStock() {
        // ARRANGE
        when(productService.findProductById(anyLong())).thenReturn(mockProduct);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(productService.saveProduct(any(Product.class))).thenReturn(mockProduct);

        StockTransaction savedTransaction = StockTransaction.builder()
                .product(mockProduct)
                .performedBy(mockUser)
                .type(TransactionType.RESTOCK)
                .quantity(20)
                .stockBefore(50)
                .stockAfter(70)
                .build();
        when(transactionRepository.save(any(StockTransaction.class)))
            .thenReturn(savedTransaction);

        // ACT
        StockTransactionResponse response =
            stockService.processTransaction(restockRequest);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getStockAfter()).isEqualTo(70);  // 50 + 20
        assertThat(response.getStockBefore()).isEqualTo(50);
        assertThat(mockProduct.getCurrentStock()).isEqualTo(70);
    }

    @Test
    @DisplayName("Should process USAGE transaction correctly")
    void processTransaction_Usage_DecreasesStock() {
        // ARRANGE
        when(productService.findProductById(anyLong())).thenReturn(mockProduct);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(productService.saveProduct(any(Product.class))).thenReturn(mockProduct);

        StockTransaction savedTransaction = StockTransaction.builder()
                .product(mockProduct)
                .performedBy(mockUser)
                .type(TransactionType.USAGE)
                .quantity(10)
                .stockBefore(50)
                .stockAfter(40)
                .build();
        when(transactionRepository.save(any(StockTransaction.class)))
            .thenReturn(savedTransaction);

        // ACT
        stockService.processTransaction(usageRequest);

        // ASSERT
        assertThat(mockProduct.getCurrentStock()).isEqualTo(40);  // 50 - 10
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock")
    void processTransaction_InsufficientStock_ThrowsException() {
        // ARRANGE — request more than available
        usageRequest.setQuantity(100);  // Only 50 available!
        when(productService.findProductById(anyLong())).thenReturn(mockProduct);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        // ACT & ASSERT
        assertThatThrownBy(() -> stockService.processTransaction(usageRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Insufficient stock")
                .hasMessageContaining("Available: 50");
    }

    @Test
    @DisplayName("Should process ADJUSTMENT transaction correctly")
    void processTransaction_Adjustment_SetsExactStock() {
        // ARRANGE
        StockTransactionRequest adjustRequest = new StockTransactionRequest();
        adjustRequest.setProductId(1L);
        adjustRequest.setType(TransactionType.ADJUSTMENT);
        adjustRequest.setQuantity(30);  // Set stock directly to 30

        when(productService.findProductById(anyLong())).thenReturn(mockProduct);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(productService.saveProduct(any(Product.class))).thenReturn(mockProduct);

        StockTransaction savedTransaction = StockTransaction.builder()
                .product(mockProduct)
                .performedBy(mockUser)
                .type(TransactionType.ADJUSTMENT)
                .quantity(30)
                .stockBefore(50)
                .stockAfter(30)
                .build();
        when(transactionRepository.save(any(StockTransaction.class)))
            .thenReturn(savedTransaction);

        // ACT
        stockService.processTransaction(adjustRequest);

        // ASSERT — stock set to exactly 30, not 50+30 or 50-30
        assertThat(mockProduct.getCurrentStock()).isEqualTo(30);
    }
}