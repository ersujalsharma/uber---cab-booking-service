package com.sujal.uber_cab_rental_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	@Bean
	OpenAPI cabBookingOpenAPI() {
		return new OpenAPI().info(new Info()
				.title("Cab Booking Service API")
				.version("v1")
				.description("API for riders, drivers, ride matching, OTP verification, and ride completion."));
	}
}
