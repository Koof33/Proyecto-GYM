package com.gym.clases.repository;

import com.gym.clases.model.Clase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {
    List<Clase> findByIdUsuario(Long idUsuario);
}


