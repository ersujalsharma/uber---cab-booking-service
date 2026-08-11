# Cab booking service

This Spring Boot API implements the lifecycle from the system-design diagram: rider creates a ride request, matching finds nearby compatible drivers, a driver accepts the ride, the rider OTP starts it, and completion settles the fare and releases the driver.

## Run

```bash
./mvnw spring-boot:run
```

H2 is the default local database, so no infrastructure is needed for development.

## API

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/riders` | Create a rider |
| `POST` | `/drivers` | Register an available driver and vehicle type |
| `PATCH` | `/drivers/{id}/location` | Update a driver's live location |
| `POST` | `/rides` | Request a ride and find drivers within 5 km |
| `POST` | `/rides/{id}/accept` | Atomically assign a driver and issue OTP |
| `POST` | `/rides/{id}/start` | Verify OTP and start ride |
| `POST` | `/rides/{id}/complete` | Mark fare paid and release driver |
| `GET` | `/rides/{id}` | Inspect ride state |

### Create a driver

```json
POST /drivers
{"name":"Aman","vehicleType":"MINI","latitude":19.076,"longitude":72.8777}
```

### Request a ride

```json
POST /rides
{
  "riderId":"<rider-id>",
  "vehicleType":"MINI",
  "pickup":{"latitude":19.076,"longitude":72.8777,"address":"Bandra West"},
  "dropoff":{"latitude":19.100,"longitude":72.900,"address":"Andheri East"}
}
```

## Production extensions

The local Haversine lookup represents the matching service. Replace it with Redis GEO plus an event queue for asynchronous driver notifications; retain the transaction locks around assignment to prevent a duplicate ride assignment.
