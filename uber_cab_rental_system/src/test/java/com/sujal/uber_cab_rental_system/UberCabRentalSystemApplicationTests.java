package com.sujal.uber_cab_rental_system;

import com.sujal.uber_cab_rental_system.api.ApiModels.*;
import com.sujal.uber_cab_rental_system.domain.*;
import com.sujal.uber_cab_rental_system.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UberCabRentalSystemApplicationTests {
    @Autowired DriverService drivers;
    @Autowired RideService rides;
    @Autowired com.sujal.uber_cab_rental_system.repository.RiderRepository riderRepository;

    @Test void contextLoads() { }

    @Test @Transactional
    void completesRideThroughTheDesignedWorkflow() {
        var rider = riderRepository.save(new Rider(UUID.randomUUID(), "Riya"));
        var driver = drivers.create(new CreateDriverRequest("Aman", VehicleType.MINI, 19.0760, 72.8777));
        var created = rides.create(new CreateRideRequest(rider.getId(), new Location(19.0760, 72.8777, "Bandra West"), new Location(19.1000, 72.9000, "Andheri East"), VehicleType.MINI));
        assertEquals(1, created.nearbyDrivers().size());
        var assigned = rides.accept(created.ride().id(), new AcceptRideRequest(driver.id()));
        assertEquals(RideStatus.DRIVER_ASSIGNED, assigned.status());
        var started = rides.start(assigned.id(), new StartRideRequest(driver.id(), assigned.otp()));
        assertEquals(RideStatus.IN_PROGRESS, started.status());
        var completed = rides.complete(started.id(), new CompleteRideRequest(driver.id()));
        assertEquals(RideStatus.COMPLETED, completed.status());
        assertEquals(PaymentStatus.PAID, completed.paymentStatus());
        assertEquals(completed.estimatedFare(), completed.finalFare());
    }

    @Test @Transactional
    void driversOnlySeeNearbyCompatibleRequestedRides() {
        var rider = riderRepository.save(new Rider(UUID.randomUUID(), "Riya"));
        var miniDriver = drivers.create(new CreateDriverRequest("Aman", VehicleType.MINI, 19.0760, 72.8777));
        drivers.create(new CreateDriverRequest("Nikhil", VehicleType.SEDAN, 19.0760, 72.8777));
        var nearbyMini = rides.create(new CreateRideRequest(rider.getId(), new Location(19.0770, 72.8780, "Bandra"), new Location(19.1000, 72.9000, "Andheri"), VehicleType.MINI));
        rides.create(new CreateRideRequest(rider.getId(), new Location(19.0770, 72.8780, "Bandra"), new Location(19.1000, 72.9000, "Andheri"), VehicleType.SEDAN));
        rides.create(new CreateRideRequest(rider.getId(), new Location(19.2760, 72.8777, "Far away"), new Location(19.3000, 72.9000, "Further away"), VehicleType.MINI));

        var offers = rides.availableForDriver(miniDriver.id());

        assertEquals(1, offers.size());
        assertEquals(nearbyMini.ride().id(), offers.getFirst().ride().id());
    }
}
