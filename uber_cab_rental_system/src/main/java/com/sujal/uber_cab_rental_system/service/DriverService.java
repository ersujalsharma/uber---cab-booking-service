package com.sujal.uber_cab_rental_system.service;

import com.sujal.uber_cab_rental_system.api.ApiModels.*;
import com.sujal.uber_cab_rental_system.domain.*;
import com.sujal.uber_cab_rental_system.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class DriverService {
	private final DriverRepository drivers;

	public DriverService(DriverRepository drivers) {
		this.drivers = drivers;
	}

	@Transactional
	public DriverResponse create(CreateDriverRequest request) {
		if (request.name() == null || request.name().isBlank() || request.vehicleType() == null)
			throw new IllegalArgumentException("name and vehicleType are required");
		return DriverResponse.from(drivers.save(new Driver(UUID.randomUUID(), request.name(), request.vehicleType(),
				request.latitude(), request.longitude())), null);
	}

	@Transactional
	public DriverResponse updateLocation(UUID id, UpdateLocationRequest request) {
		Driver driver = drivers.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Driver not found"));
		driver.updateLocation(request.latitude(), request.longitude());
		return DriverResponse.from(driver, null);
	}

	@Transactional(readOnly = true)
	public List<DriverResponse> nearby(double latitude, double longitude, VehicleType type) {
		return drivers.findByAvailableTrueAndVehicleType(type).stream()
				.map(driver -> DriverResponse.from(driver,
						round(distance(latitude, longitude, driver.getLatitude(), driver.getLongitude()))))
				.filter(driver -> driver.distanceKm() <= 5).sorted(Comparator.comparing(DriverResponse::distanceKm))
				.toList();
	}

	private static double distance(double aLat, double aLon, double bLat, double bLon) {
		double earthRadiusKm = 6371;
		double lat = Math.toRadians(bLat - aLat), lon = Math.toRadians(bLon - aLon);
		double haversine = Math.sin(lat / 2) * Math.sin(lat / 2) + Math.cos(Math.toRadians(aLat))
				* Math.cos(Math.toRadians(bLat)) * Math.sin(lon / 2) * Math.sin(lon / 2);
		return 2 * earthRadiusKm * Math.asin(Math.sqrt(haversine));
	}

	private static double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
}
