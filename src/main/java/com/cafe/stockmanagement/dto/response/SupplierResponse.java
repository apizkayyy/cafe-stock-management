package com.cafe.stockmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
    // Notice: NO products list — avoids lazy loading!
}