package com.cafe.stockmanagement.service;

import com.cafe.stockmanagement.dto.request.SupplierRequest;
import com.cafe.stockmanagement.dto.response.SupplierResponse;
import com.cafe.stockmanagement.entity.Supplier;
import com.cafe.stockmanagement.exception.BadRequestException;
import com.cafe.stockmanagement.exception.ResourceNotFoundException;
import com.cafe.stockmanagement.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SupplierResponse getSupplierById(Long id) {
        return mapToResponse(findSupplierById(id));
    }

    // Internal method — returns raw entity for other services
    public Supplier findSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Supplier not found with id: " + id)
                );
    }

    public SupplierResponse createSupplier(SupplierRequest request) {
        if (supplierRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                "Supplier with this email already exists"
            );
        }

        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .isActive(true)
                .build();

        return mapToResponse(supplierRepository.save(supplier));
    }

    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = findSupplierById(id);
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        return mapToResponse(supplierRepository.save(supplier));
    }

    public void deactivateSupplier(Long id) {
        Supplier supplier = findSupplierById(id);
        supplier.setIsActive(false);
        supplierRepository.save(supplier);
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .isActive(supplier.getIsActive())
                .createdAt(supplier.getCreatedAt())
                .build();
    }
}