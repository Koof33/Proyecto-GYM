package com.gym.reservas.repository;

import com.gym.reservas.model.EstadoReservaHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoReservaHistorialRepository extends JpaRepository<EstadoReservaHistorial, Long> {
    List<EstadoReservaHistorial> findByReservaIdOrderByFechaCambioDesc(Long idReserva);
}
