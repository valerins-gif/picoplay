package com.cartagena.picoplay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cartagena.picoplay.model.HistorialReproduccion;

public interface HistorialRepository extends JpaRepository<HistorialReproduccion, Long> {
    List<HistorialReproduccion> findAllByOrderByFechaHoraDesc();
}