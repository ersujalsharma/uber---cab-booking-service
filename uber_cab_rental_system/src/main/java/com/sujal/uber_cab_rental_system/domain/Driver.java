package com.sujal.uber_cab_rental_system.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Driver {
    @Id private UUID id;
    private String name;
    @Enumerated(EnumType.STRING) private VehicleType vehicleType;
    private double latitude;
    private double longitude;
    private boolean available;
    @Version private long version;

    protected Driver() { }
    public Driver(UUID id, String name, VehicleType vehicleType, double latitude, double longitude) {
        this.id = id; this.name = name; this.vehicleType = vehicleType; this.latitude = latitude; this.longitude = longitude; this.available = true;
    }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public VehicleType getVehicleType() { return vehicleType; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isAvailable() { return available; }
    public void updateLocation(double latitude, double longitude) { this.latitude = latitude; this.longitude = longitude; }
    public void setAvailable(boolean available) { this.available = available; }
}
