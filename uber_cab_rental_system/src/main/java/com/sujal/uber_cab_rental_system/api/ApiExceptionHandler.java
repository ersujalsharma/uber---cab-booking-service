package com.sujal.uber_cab_rental_system.api;

import com.sujal.uber_cab_rental_system.service.NotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class) ResponseEntity<Map<String, String>> notFound(NotFoundException error) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", error.getMessage())); }
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class}) ResponseEntity<Map<String, String>> badRequest(RuntimeException error) { return ResponseEntity.badRequest().body(Map.of("error", error.getMessage())); }
}
