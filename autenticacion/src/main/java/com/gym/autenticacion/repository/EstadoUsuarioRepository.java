package com.gym.autenticacion.repository;

import com.gym.autenticacion.model.EstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoUsuarioRepository extends JpaRepository<EstadoUsuario, Long> {
    Optional<EstadoUsuario> findByNombreEstado(String nombreEstado);
}
