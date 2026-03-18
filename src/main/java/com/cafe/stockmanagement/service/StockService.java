package com.cafe.stockmanagement.service;

import com.cafe.stockmanagement.dto.request.StockTransactionRequest;
import com.cafe.stockmanagement.dto.response.StockTransactionResponse;
import com.cafe.stockmanagement.entity.Product;
import com.cafe.stockmanagement.entity.StockTransaction;
import com.cafe.stockmanagement.entity.User;
import com.cafe.stockmanagement.enums.TransactionType;
import com.cafe.stockmanagement.exception.BadRequestException;
import com.cafe.stockmanagement.exception.ResourceNotFoundException;
import com.cafe.stockmanagement.repository.StockTransactionRepository;
import com.cafe.stockmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockTransactionRepository transactionRepository;
    private final ProductService productService;
    private final UserRepository userRepository;

    @Transactional
    public StockTransactionResponse processTransaction(
            StockTransactionRequest request) {

        Product product = productService.findProductById(request.getProductId());

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found")
                );

        int stockBefore = product.getCurrentStock();
        int newStock;

        switch (request.getType()) {
            case RESTOCK -> newStock = stockBefore + request.getQuantity();
            case USAGE, WASTE -> {
                if (stockBefore < request.getQuantity()) {
                    throw new BadRequestException(
                        "Insufficient stock. Available: " + stockBefore +
                        ", Requested: " + request.getQuantity()
                    );
                }
                newStock = stockBefore - request.getQuantity();
            }
            case ADJUSTMENT -> newStock = request.getQuantity();
            default -> throw new BadRequestException("Invalid transaction type");
        }

        product.setCurrentStock(newStock);
        productService.saveProduct(product);

        StockTransaction transaction = StockTransaction.builder()
                .product(product)
                .performedBy(currentUser)
                .type(request.getType())
                .quantity(request.getQuantity())
                .stockBefore(stockBefore)
                .stockAfter(newStock)
                .totalCost(request.getTotalCost())
                .notes(request.getNotes())
                .build();

        return mapToResponse(transactionRepository.save(transaction));
    }

    public List<StockTransactionResponse> getAllTransactions() {
    return transactionRepository.findAllByOrderByCreatedAtDesc()  // ✅ uses JOIN FETCH
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
}

public List<StockTransactionResponse> getProductTransactions(Long productId) {
    return transactionRepository.findByProductIdOrderByCreatedAtDesc(productId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
}

public List<StockTransactionResponse> getTransactionsByType(TransactionType type) {
    return transactionRepository.findByTypeOrderByCreatedAtDesc(type)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
}

    // Mapper — only accesses already-loaded fields, no lazy loading!
    private StockTransactionResponse mapToResponse(StockTransaction t) {
    return StockTransactionResponse.builder()
            .id(t.getId())
            .productId(t.getProduct() != null ? t.getProduct().getId() : null)
            .productName(t.getProduct() != null ? t.getProduct().getName() : null)
            .productUnit(t.getProduct() != null ? t.getProduct().getUnit() : null)
            
            .performedById(t.getPerformedBy() != null ? t.getPerformedBy().getId() : null)
            .performedByName(t.getPerformedBy() != null ? t.getPerformedBy().getName() : null)
            .performedByRole(t.getPerformedBy() != null ? t.getPerformedBy().getRole().name() : null)
            .type(t.getType())
            .quantity(t.getQuantity())
            .stockBefore(t.getStockBefore())
            .stockAfter(t.getStockAfter())
            .totalCost(t.getTotalCost())
            .notes(t.getNotes())
            .createdAt(t.getCreatedAt())
            .build();
}
}