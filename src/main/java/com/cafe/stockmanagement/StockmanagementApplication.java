package com.cafe.stockmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing 				// This enables createdAt/updatedAt auto-fill
public class StockmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockmanagementApplication.class, args);

		
	}

}
