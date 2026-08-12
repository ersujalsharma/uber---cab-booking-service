package com.sujal.uber_cab_rental_system.api;

import com.sujal.uber_cab_rental_system.domain.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public final class ApiModels {
	private ApiModels() {
	}

	public record Location(double latitude, double longitude, String address) {
	}

	public record CreateRiderRequest(String name) {
	}

	public record CreateDriverRequest(String name, VehicleType vehicleType, double latitude, double longitude) {
	}

	public record UpdateLocationRequest(double latitude, double longitude) {
	}

	public record CreateRideRequest(UUID riderId, Location pickup, Location dropoff, VehicleType vehicleType) {
	}

	public record AcceptRideRequest(UUID driverId) {
	}

	public record StartRideRequest(UUID driverId, String otp) {
	}

	public record CompleteRideRequest(UUID driverId) {
	}

	public record DriverResponse(UUID id, String name, VehicleType vehicleType, double latitude, double longitude,
			boolean available, Double distanceKm) {
		public static DriverResponse from(Driver driver, Double distanceKm) {
			return new DriverResponse(driver.getId(), driver.getName(), driver.getVehicleType(), driver.getLatitude(),
					driver.getLongitude(), driver.isAvailable(), distanceKm);
		}
	}

	public record RiderResponse(UUID id, String name) {
		public static RiderResponse from(Rider rider) {
			return new RiderResponse(rider.getId(), rider.getName());
		}
	}

	public record RideResponse(UUID id, UUID riderId, UUID driverId, VehicleType vehicleType, RideStatus status,
			PaymentStatus paymentStatus, Location pickup, Location dropoff, BigDecimal estimatedFare,
			BigDecimal finalFare, String otp, Instant createdAt, Instant startedAt, Instant completedAt) {
		public static RideResponse from(Ride ride) {
			return new RideResponse(ride.getId(), ride.getRiderId(), ride.getDriverId(), ride.getVehicleType(),
					ride.getStatus(), ride.getPaymentStatus(),
					new Location(ride.getPickupLatitude(), ride.getPickupLongitude(), ride.getPickupAddress()),
					new Location(ride.getDropoffLatitude(), ride.getDropoffLongitude(), ride.getDropoffAddress()),
					ride.getEstimatedFare(), ride.getFinalFare(), ride.getOtp(), ride.getCreatedAt(),
					ride.getStartedAt(), ride.getCompletedAt());
		}
	}

	public record RideCreatedResponse(RideResponse ride, List<DriverResponse> nearbyDrivers) {
	}
}
