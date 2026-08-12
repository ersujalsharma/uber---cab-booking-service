package com.sujal.uber_cab_rental_system.service;

import com.sujal.uber_cab_rental_system.api.ApiModels.*;
import com.sujal.uber_cab_rental_system.domain.*;
import com.sujal.uber_cab_rental_system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.security.SecureRandom;
import java.util.*;

@Service
public class RideService {
	private final RideRepository rides;
	private final RiderRepository riders;
	private final DriverRepository drivers;
	private final DriverService driverService;
	private final SecureRandom random = new SecureRandom();

	public RideService(RideRepository rides, RiderRepository riders, DriverRepository drivers,
			DriverService driverService) {
		this.rides = rides;
		this.riders = riders;
		this.drivers = drivers;
		this.driverService = driverService;
	}

	@Transactional
	public RideCreatedResponse create(CreateRideRequest request) {
		if (request.riderId() == null || request.pickup() == null || request.dropoff() == null
				|| request.vehicleType() == null)
			throw new IllegalArgumentException("riderId, pickup, dropoff, and vehicleType are required");
		if (!riders.existsById(request.riderId()))
			throw new NotFoundException("Rider not found");
		Ride ride = rides
				.save(new Ride(UUID.randomUUID(), request.riderId(), request.vehicleType(), request.pickup().latitude(),
						request.pickup().longitude(), request.pickup().address(), request.dropoff().latitude(),
						request.dropoff().longitude(), request.dropoff().address(), calculateFare(request)));
		// In production this publishes a ride-requested event to a queue. The
		// synchronous response makes the matching decision observable for this sample.
		return new RideCreatedResponse(RideResponse.from(ride),
				driverService.nearby(request.pickup().latitude(), request.pickup().longitude(), request.vehicleType()));
	}

	@Transactional
	public RideResponse accept(UUID rideId, AcceptRideRequest request) {
		Ride ride = rides.findByIdForUpdate(rideId).orElseThrow(() -> new NotFoundException("Ride not found"));
		if (ride.getStatus() != RideStatus.REQUESTED)
			throw new IllegalStateException("Ride has already been assigned or is no longer available");
		Driver driver = drivers.findByIdForUpdate(request.driverId())
				.orElseThrow(() -> new NotFoundException("Driver not found"));
		if (!driver.isAvailable())
			throw new IllegalStateException("Driver is unavailable");
		if (driver.getVehicleType() != ride.getVehicleType())
			throw new IllegalArgumentException("Driver vehicle type does not match the ride");
		driver.setAvailable(false);
		ride.assignDriver(driver.getId(), "%06d".formatted(random.nextInt(1_000_000)));
		return RideResponse.from(ride);
	}

	@Transactional
	public RideResponse start(UUID rideId, StartRideRequest request) {
		Ride ride = rides.findByIdForUpdate(rideId).orElseThrow(() -> new NotFoundException("Ride not found"));
		if (ride.getStatus() != RideStatus.DRIVER_ASSIGNED || !Objects.equals(ride.getDriverId(), request.driverId())
				|| !Objects.equals(ride.getOtp(), request.otp()))
			throw new IllegalStateException("Invalid OTP, driver, or ride state");
		ride.start();
		return RideResponse.from(ride);
	}

	@Transactional
	public RideResponse complete(UUID rideId, CompleteRideRequest request) {
		Ride ride = rides.findByIdForUpdate(rideId).orElseThrow(() -> new NotFoundException("Ride not found"));
		if (ride.getStatus() != RideStatus.IN_PROGRESS || !Objects.equals(ride.getDriverId(), request.driverId()))
			throw new IllegalStateException("Only the assigned driver can complete an active ride");
		Driver driver = drivers.findByIdForUpdate(request.driverId())
				.orElseThrow(() -> new NotFoundException("Driver not found"));
		ride.complete();
		driver.setAvailable(true);
		return RideResponse.from(ride);
	}

	@Transactional(readOnly = true)
	public RideResponse get(UUID id) {
		return RideResponse.from(rides.findById(id).orElseThrow(() -> new NotFoundException("Ride not found")));
	}

	private static BigDecimal calculateFare(CreateRideRequest request) {
		double km = haversine(request.pickup().latitude(), request.pickup().longitude(), request.dropoff().latitude(),
				request.dropoff().longitude());
		int perKm = switch (request.vehicleType()) {
		case MINI -> 12;
		case SEDAN -> 17;
		case XL -> 23;
		};
		return BigDecimal.valueOf(49 + km * perKm).setScale(2, RoundingMode.HALF_UP);
	}

	private static double haversine(double aLat, double aLon, double bLat, double bLon) {
		double lat = Math.toRadians(bLat - aLat), lon = Math.toRadians(bLon - aLon),
				h = Math.sin(lat / 2) * Math.sin(lat / 2) + Math.cos(Math.toRadians(aLat))
						* Math.cos(Math.toRadians(bLat)) * Math.sin(lon / 2) * Math.sin(lon / 2);
		return 2 * 6371 * Math.asin(Math.sqrt(h));
	}
}
