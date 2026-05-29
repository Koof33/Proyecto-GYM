package com.gym.clases.repository;

import com.gym.clases.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByIdUser(Long idUser);
    List<Inscripcion> findByClase_IdClase(Long idClase);
    List<Inscripcion> findByIdUserAndEstado_Nombre(Long idUser, String estado);
}


