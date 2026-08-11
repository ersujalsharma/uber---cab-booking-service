package com.sujal.uber_cab_rental_system.repository;
import com.sujal.uber_cab_rental_system.domain.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    List<Driver> findByAvailableTrueAndVehicleType(VehicleType vehicleType);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select d from Driver d where d.id = :id") Optional<Driver> findByIdForUpdate(UUID id);
}
