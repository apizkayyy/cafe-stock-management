package com.cafe.stockmanagement.controller;

import com.cafe.stockmanagement.dto.request.SupplierRequest;
import com.cafe.stockmanagement.dto.response.ApiResponse;
import com.cafe.stockmanagement.dto.response.SupplierResponse;
import com.cafe.stockmanagement.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")           
@RequiredArgsConstructor
public class SupplierController {           

    private final SupplierService supplierService;  

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAllSuppliers() {
        return ResponseEntity.ok(
            ApiResponse.success("Suppliers retrieved",
                supplierService.getAllSuppliers())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success("Supplier retrieved",
                supplierService.getSupplierById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Supplier created successfully",
                supplierService.createSupplier(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success("Supplier updated",
                supplierService.updateSupplier(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateSupplier(
            @PathVariable Long id) {
        supplierService.deactivateSupplier(id);
        return ResponseEntity.ok(
            ApiResponse.success("Supplier deactivated", null)
        );
    }
}