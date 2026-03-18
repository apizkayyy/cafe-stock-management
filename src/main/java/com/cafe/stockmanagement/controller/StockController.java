package com.cafe.stockmanagement.controller;

import com.cafe.stockmanagement.dto.request.StockTransactionRequest;
import com.cafe.stockmanagement.dto.response.ApiResponse;
import com.cafe.stockmanagement.dto.response.StockTransactionResponse;
import com.cafe.stockmanagement.enums.TransactionType;
import com.cafe.stockmanagement.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/transaction")
    public ResponseEntity<ApiResponse<StockTransactionResponse>> processTransaction(
            @Valid @RequestBody StockTransactionRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Transaction processed successfully",
                stockService.processTransaction(request)));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<StockTransactionResponse>>> getAllTransactions() {
        return ResponseEntity.ok(
            ApiResponse.success("Transactions retrieved successfully",
                stockService.getAllTransactions())
        );
    }

    @GetMapping("/transactions/product/{productId}")
    public ResponseEntity<ApiResponse<List<StockTransactionResponse>>> getProductTransactions(
            @PathVariable Long productId) {
        return ResponseEntity.ok(
            ApiResponse.success("Product transactions retrieved",
                stockService.getProductTransactions(productId))
        );
    }

    @GetMapping("/transactions/type/{type}")
    public ResponseEntity<ApiResponse<List<StockTransactionResponse>>> getTransactionsByType(
            @PathVariable TransactionType type) {
        return ResponseEntity.ok(
            ApiResponse.success("Transactions retrieved by type",
                stockService.getTransactionsByType(type))
        );
    }
}