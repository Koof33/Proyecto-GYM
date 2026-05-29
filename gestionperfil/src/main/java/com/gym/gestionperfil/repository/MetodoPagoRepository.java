package com.gym.gestionperfil.repository;

import com.gym.gestionperfil.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    List<MetodoPago> findByIdUsuario(Long idUsuario);
}
