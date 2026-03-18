package com.cafe.stockmanagement.dto.response;

import com.cafe.stockmanagement.enums.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransactionResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productUnit;

    
    private Long performedById;
    private String performedByName;
    private String performedByRole;     

    private TransactionType type;
    private Integer quantity;
    private Integer stockBefore;
    private Integer stockAfter;
    private BigDecimal totalCost;
    private String notes;
    private LocalDateTime createdAt;
}