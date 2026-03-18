package com.cafe.stockmanagement.repository;

import com.cafe.stockmanagement.entity.StockTransaction;
import com.cafe.stockmanagement.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    @Query("SELECT t FROM StockTransaction t LEFT JOIN FETCH t.product LEFT JOIN FETCH t.performedBy ORDER BY t.createdAt DESC")
    List<StockTransaction> findAllByOrderByCreatedAtDesc();

    @Query("SELECT t FROM StockTransaction t LEFT JOIN FETCH t.product LEFT JOIN FETCH t.performedBy WHERE t.product.id = :productId ORDER BY t.createdAt DESC")
    List<StockTransaction> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);

    @Query("SELECT t FROM StockTransaction t LEFT JOIN FETCH t.product LEFT JOIN FETCH t.performedBy WHERE t.type = :type ORDER BY t.createdAt DESC")
    List<StockTransaction> findByTypeOrderByCreatedAtDesc(@Param("type") TransactionType type);
}