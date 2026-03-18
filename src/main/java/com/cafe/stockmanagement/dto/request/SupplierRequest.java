package com.cafe.stockmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    private String address;
}