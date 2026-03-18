package com.cafe.stockmanagement.dto.request;

import com.cafe.stockmanagement.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockTransactionRequest {

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal totalCost;

    private String notes;
}