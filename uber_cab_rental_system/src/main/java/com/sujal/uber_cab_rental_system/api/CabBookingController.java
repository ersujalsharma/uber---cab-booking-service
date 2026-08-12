package com.sujal.uber_cab_rental_system.api;

import com.sujal.uber_cab_rental_system.api.ApiModels.*;
import com.sujal.uber_cab_rental_system.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;

@RestController
@Tag(name = "Cab booking", description = "Rider, driver, matching, and ride lifecycle operations")
public class CabBookingController {
	private final DriverService drivers;
	private final RideService rides;
	private final com.sujal.uber_cab_rental_system.repository.RiderRepository riderRepository;

	public CabBookingController(DriverService drivers, RideService rides,
			com.sujal.uber_cab_rental_system.repository.RiderRepository riderRepository) {
		this.drivers = drivers;
		this.rides = rides;
		this.riderRepository = riderRepository;
	}

	@PostMapping("/riders")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create rider")
	public RiderResponse createRider(@RequestBody CreateRiderRequest request) {
		if (request.name() == null || request.name().isBlank())
			throw new IllegalArgumentException("name is required");
		return RiderResponse.from(riderRepository
				.save(new com.sujal.uber_cab_rental_system.domain.Rider(UUID.randomUUID(), request.name())));
	}

	@PostMapping("/drivers")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register available driver")
	public DriverResponse createDriver(@RequestBody CreateDriverRequest request) {
		return drivers.create(request);
	}

	@PatchMapping("/drivers/{id}/location")
	@Operation(summary = "Update driver's live location")
	public DriverResponse updateLocation(@PathVariable UUID id, @RequestBody UpdateLocationRequest request) {
		return drivers.updateLocation(id, request);
	}

	@GetMapping("/drivers/{id}/ride-requests")
	@Operation(summary = "View nearby ride requests available to a driver")
	public java.util.List<RideOfferResponse> driverRideRequests(@PathVariable UUID id) {
		return rides.availableForDriver(id);
	}

	@PostMapping("/rides")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Request a ride and find nearby drivers")
	public RideCreatedResponse createRide(@RequestBody CreateRideRequest request) {
		return rides.create(request);
	}

	@PostMapping("/rides/{id}/accept")
	@Operation(summary = "Accept a requested ride and receive OTP")
	public RideResponse acceptRide(@PathVariable UUID id, @RequestBody AcceptRideRequest request) {
		return rides.accept(id, request);
	}

	@PostMapping("/rides/{id}/start")
	@Operation(summary = "Verify OTP and start ride")
	public RideResponse startRide(@PathVariable UUID id, @RequestBody StartRideRequest request) {
		return rides.start(id, request);
	}

	@PostMapping("/rides/{id}/complete")
	@Operation(summary = "Complete ride and settle fare")
	public RideResponse completeRide(@PathVariable UUID id, @RequestBody CompleteRideRequest request) {
		return rides.complete(id, request);
	}

	@GetMapping("/rides/{id}")
	@Operation(summary = "Get ride status and tracking data")
	public RideResponse getRide(@PathVariable UUID id) {
		return rides.get(id);
	}

	@GetMapping("/health")
	public java.util.Map<String, String> health() {
		return java.util.Map.of("status", "ok");
	}
}
