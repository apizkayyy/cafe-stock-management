package com.cafe.stockmanagement.repository;

import com.cafe.stockmanagement.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByEmail(String email);

    Boolean existsByEmail(String email);

    // Find all active suppliers
    List<Supplier> findByIsActiveTrue();
}