package com.gym.membresias.repository;

import com.gym.membresias.model.Membresia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findByIdUsuarioOrderByFechaInicioDesc(Long idUsuario);

    Optional<Membresia> findTopByIdUsuarioOrderByFechaInicioDesc(Long idUsuario);
}

