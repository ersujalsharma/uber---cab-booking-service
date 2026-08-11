package com.sujal.uber_cab_rental_system.repository;
import com.sujal.uber_cab_rental_system.domain.Ride;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface RideRepository extends JpaRepository<Ride, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select r from Ride r where r.id = :id") Optional<Ride> findByIdForUpdate(UUID id);
}
