package com.gym.servicios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gym.servicios.model.Servicio;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {}


