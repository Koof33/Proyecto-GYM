package com.gym.usuarios.repository;

import com.gym.usuarios.model.EstadoUsuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoUsuarioRepository extends JpaRepository<EstadoUsuario, Long>{
    @Query(value = "SELECT * FROM estado_usuario WHERE nombre_estado = :nombreEstado", nativeQuery = true)
    
    EstadoUsuario findByNombre(@Param("nombreEstado") String nombreEstado);
}
