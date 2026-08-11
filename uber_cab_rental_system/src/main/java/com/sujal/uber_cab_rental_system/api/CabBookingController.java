package com.sujal.uber_cab_rental_system.api;

import com.sujal.uber_cab_rental_system.api.ApiModels.*;
import com.sujal.uber_cab_rental_system.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
public class CabBookingController {
    private final DriverService drivers; private final RideService rides; private final com.sujal.uber_cab_rental_system.repository.RiderRepository riderRepository;
    public CabBookingController(DriverService drivers, RideService rides, com.sujal.uber_cab_rental_system.repository.RiderRepository riderRepository) { this.drivers = drivers; this.rides = rides; this.riderRepository = riderRepository; }
    @PostMapping("/riders") @ResponseStatus(HttpStatus.CREATED) public RiderResponse createRider(@RequestBody CreateRiderRequest request) { if (request.name() == null || request.name().isBlank()) throw new IllegalArgumentException("name is required"); return RiderResponse.from(riderRepository.save(new com.sujal.uber_cab_rental_system.domain.Rider(UUID.randomUUID(), request.name()))); }
    @PostMapping("/drivers") @ResponseStatus(HttpStatus.CREATED) public DriverResponse createDriver(@RequestBody CreateDriverRequest request) { return drivers.create(request); }
    @PatchMapping("/drivers/{id}/location") public DriverResponse updateLocation(@PathVariable UUID id, @RequestBody UpdateLocationRequest request) { return drivers.updateLocation(id, request); }
    @PostMapping("/rides") @ResponseStatus(HttpStatus.CREATED) public RideCreatedResponse createRide(@RequestBody CreateRideRequest request) { return rides.create(request); }
    @PostMapping("/rides/{id}/accept") public RideResponse acceptRide(@PathVariable UUID id, @RequestBody AcceptRideRequest request) { return rides.accept(id, request); }
    @PostMapping("/rides/{id}/start") public RideResponse startRide(@PathVariable UUID id, @RequestBody StartRideRequest request) { return rides.start(id, request); }
    @PostMapping("/rides/{id}/complete") public RideResponse completeRide(@PathVariable UUID id, @RequestBody CompleteRideRequest request) { return rides.complete(id, request); }
    @GetMapping("/rides/{id}") public RideResponse getRide(@PathVariable UUID id) { return rides.get(id); }
    @GetMapping("/health") public java.util.Map<String, String> health() { return java.util.Map.of("status", "ok"); }
}
