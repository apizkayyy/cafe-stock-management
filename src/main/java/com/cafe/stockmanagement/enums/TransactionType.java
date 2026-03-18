package com.cafe.stockmanagement.enums;

public enum TransactionType {
    RESTOCK,    // Stock coming IN (from supplier)
    USAGE,      // Stock going OUT (used in cafe)
    ADJUSTMENT, // Manual correction
    WASTE       // Spoiled/damaged goods
}