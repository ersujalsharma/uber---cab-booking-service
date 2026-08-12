package com.sujal.uber_cab_rental_system.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class Rider {
	@Id
	private UUID id;
	private String name;

	protected Rider() {
	}

	public Rider(UUID id, String name) {
		this.id = id;
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
