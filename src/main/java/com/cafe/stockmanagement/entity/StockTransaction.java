package com.cafe.stockmanagement.entity;

import com.cafe.stockmanagement.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "stock_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "category", "supplier"})
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User performedBy;        // Who did this transaction?

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;       // RESTOCK, USAGE, ADJUSTMENT, WASTE

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer stockBefore;        // Audit trail — what was stock before?

    @Column(nullable = false)
    private Integer stockAfter;         // What was stock after?

    @Column(precision = 10, scale = 2)
    private BigDecimal totalCost;       // Optional: cost of this transaction

    private String notes;               // Optional remarks
}