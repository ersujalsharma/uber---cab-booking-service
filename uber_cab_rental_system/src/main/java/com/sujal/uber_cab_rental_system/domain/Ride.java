package com.sujal.uber_cab_rental_system.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Ride {
    @Id private UUID id;
    private UUID riderId;
    private UUID driverId;
    @Enumerated(EnumType.STRING) private VehicleType vehicleType;
    @Enumerated(EnumType.STRING) private RideStatus status;
    @Enumerated(EnumType.STRING) private PaymentStatus paymentStatus;
    private double pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude;
    private String pickupAddress, dropoffAddress, otp;
    private BigDecimal estimatedFare, finalFare;
    private Instant createdAt, startedAt, completedAt;
    @Version private long version;

    protected Ride() { }
    public Ride(UUID id, UUID riderId, VehicleType vehicleType, double pickupLatitude, double pickupLongitude, String pickupAddress, double dropoffLatitude, double dropoffLongitude, String dropoffAddress, BigDecimal estimatedFare) {
        this.id = id; this.riderId = riderId; this.vehicleType = vehicleType; this.pickupLatitude = pickupLatitude; this.pickupLongitude = pickupLongitude; this.pickupAddress = pickupAddress; this.dropoffLatitude = dropoffLatitude; this.dropoffLongitude = dropoffLongitude; this.dropoffAddress = dropoffAddress; this.estimatedFare = estimatedFare; this.status = RideStatus.REQUESTED; this.paymentStatus = PaymentStatus.PENDING; this.createdAt = Instant.now();
    }
    public UUID getId() { return id; } public UUID getRiderId() { return riderId; } public UUID getDriverId() { return driverId; }
    public VehicleType getVehicleType() { return vehicleType; } public RideStatus getStatus() { return status; } public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public double getPickupLatitude() { return pickupLatitude; } public double getPickupLongitude() { return pickupLongitude; } public double getDropoffLatitude() { return dropoffLatitude; } public double getDropoffLongitude() { return dropoffLongitude; }
    public String getPickupAddress() { return pickupAddress; } public String getDropoffAddress() { return dropoffAddress; } public String getOtp() { return otp; }
    public BigDecimal getEstimatedFare() { return estimatedFare; } public BigDecimal getFinalFare() { return finalFare; } public Instant getCreatedAt() { return createdAt; } public Instant getStartedAt() { return startedAt; } public Instant getCompletedAt() { return completedAt; }
    public void assignDriver(UUID driverId, String otp) { this.driverId = driverId; this.otp = otp; this.status = RideStatus.DRIVER_ASSIGNED; }
    public void start() { this.status = RideStatus.IN_PROGRESS; this.startedAt = Instant.now(); }
    public void complete() { this.status = RideStatus.COMPLETED; this.finalFare = estimatedFare; this.paymentStatus = PaymentStatus.PAID; this.completedAt = Instant.now(); }
}
