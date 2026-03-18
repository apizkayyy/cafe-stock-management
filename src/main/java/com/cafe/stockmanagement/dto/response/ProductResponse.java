package com.cafe.stockmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
private Long id;
private String name;
private String description;
private String sku;
private BigDecimal unitPrice;
private String unit;
private Integer currentStock;
private Integer minimumStock;
private Boolean isLowStock;       // ← check this exists
private Long categoryId;
private String categoryName;
private Long supplierId;
private String supplierName;
private Boolean isActive;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
}