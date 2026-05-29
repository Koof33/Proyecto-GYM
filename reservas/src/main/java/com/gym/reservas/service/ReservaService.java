package com.gym.reservas.service;

import com.gym.reservas.model.EstadoReserva;
import com.gym.reservas.model.EstadoReservaHistorial;
import com.gym.reservas.model.Reserva;
import com.gym.reservas.repository.EstadoReservaHistorialRepository;
import com.gym.reservas.repository.EstadoReservaRepository;
import com.gym.reservas.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepo;
    private final EstadoReservaRepository estadoRepo;
    private final EstadoReservaHistorialRepository historialRepo;

    public Reserva crear(LocalDate fecha, String descripcion, Long idUsuario, Long idServicio, Long idEntrenador) {
        EstadoReserva estado = estadoRepo.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        Reserva r = new Reserva(null, fecha, LocalDateTime.now(), descripcion, idUsuario, idServicio, idEntrenador, estado);
        r = reservaRepo.save(r);

        historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now(), "Reserva creada", estado, r));

        return r;
    }

    public List<Reserva> listarPorUsuario(Long idUsuario) {
        return reservaRepo.findByIdUsuarioOrderByFechaDesc(idUsuario);
    }

    public List<Reserva> listarPorEntrenador(Long idEntrenador) {
        return reservaRepo.findByIdEntrenadorOrderByFechaDesc(idEntrenador);
    }

    public Reserva cambiarEstado(Long idReserva, String nuevoEstado, String comentario) {
        Reserva r = reservaRepo.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        EstadoReserva estado = estadoRepo.findByNombre(nuevoEstado)
                .orElseThrow(() -> new RuntimeException("Estado no válido"));

        r.setEstado(estado);
        reservaRepo.save(r);

        historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now(), comentario, estado, r));
        return r;
    }

    public List<EstadoReservaHistorial> historial(Long idReserva) {
        return historialRepo.findByReservaIdOrderByFechaCambioDesc(idReserva);
    }

    public List<Object[]> conteoReservasPorServicio() {
        return reservaRepo.countReservasByServicio();
    }

    public List<Object[]> conteoReservasPorFecha(LocalDate fecha) {
        return reservaRepo.countReservasByServicioAndFecha(fecha);
    }

    public List<Object[]> topServicios() {
        return reservaRepo.topServicios();
    }
}

