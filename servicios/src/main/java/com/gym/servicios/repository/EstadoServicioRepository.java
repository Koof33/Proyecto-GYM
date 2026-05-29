package com.gym.servicios.repository;

import com.gym.servicios.model.EstadoServicio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoServicioRepository extends JpaRepository<EstadoServicio, Long> {
    Optional<EstadoServicio> findByNombre(String nombre);
}
