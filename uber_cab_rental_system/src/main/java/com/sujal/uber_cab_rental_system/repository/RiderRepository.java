package com.sujal.uber_cab_rental_system.repository;
import com.sujal.uber_cab_rental_system.domain.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface RiderRepository extends JpaRepository<Rider, UUID> { }
