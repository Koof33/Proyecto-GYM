package com.gym.autenticacion.repository;

import com.gym.autenticacion.model.Enum.NombreRol;
import com.gym.autenticacion.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombreRol(NombreRol nombreRol);
}
