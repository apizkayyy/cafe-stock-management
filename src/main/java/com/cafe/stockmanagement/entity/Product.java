package com.cafe.stockmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;            // e.g. "Arabica Coffee Beans"

    private String description;

    private String sku;             // Stock Keeping Unit — unique product code

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;   // Always BigDecimal for money, NEVER double!

    private String unit;            // "kg", "liters", "pieces"

    @Column(nullable = false)
    @Builder.Default
    private Integer currentStock = 0;

    @Column(nullable = false)
    private Integer minimumStock;   // Alert threshold — "reorder when below this"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "products"})
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "products"})
    private Supplier supplier;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}