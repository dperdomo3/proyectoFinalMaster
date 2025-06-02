package com.data.access.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.data.access.domain.Parking;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
