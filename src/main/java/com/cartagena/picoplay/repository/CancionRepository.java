package com.cartagena.picoplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cartagena.picoplay.model.Cancion;

public interface CancionRepository extends JpaRepository<Cancion, Long> {
}