package com.gym.comentarios.repository;

import com.gym.comentarios.model.Comentarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentariosRepository extends JpaRepository<Comentarios, Long> {
    List<Comentarios> findByIdServicio(Long idServicio);
    List<Comentarios> findByIdUsuario(Long idUsuario);
}

