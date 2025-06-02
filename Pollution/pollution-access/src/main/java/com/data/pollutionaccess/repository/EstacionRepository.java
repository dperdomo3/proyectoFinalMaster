package com.data.pollutionaccess.repository;

import com.data.pollutionaccess.model.Estacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstacionRepository extends JpaRepository<Estacion, Long> {
}
