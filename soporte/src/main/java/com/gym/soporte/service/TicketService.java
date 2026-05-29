package com.gym.soporte.service;

import com.gym.soporte.model.EstadoTicket;
import com.gym.soporte.model.Motivo;
import com.gym.soporte.model.Ticket;
import com.gym.soporte.repository.EstadoTicketRepository;
import com.gym.soporte.repository.MotivoRepository;
import com.gym.soporte.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepo;
    private final MotivoRepository motivoRepo;
    private final EstadoTicketRepository estadoRepo;

    public Ticket crearTicket(String descripcion, Long idUsuario, Long idMotivo) {
        Motivo motivo = motivoRepo.findById(idMotivo)
                .orElseThrow(() -> new RuntimeException("Motivo no encontrado"));

        EstadoTicket estado = estadoRepo.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        Ticket ticket = new Ticket();
        ticket.setFecha(LocalDateTime.now());
        ticket.setDescripcion(descripcion);
        ticket.setIdUsuario(idUsuario);
        ticket.setMotivo(motivo);
        ticket.setEstado(estado);
        return ticketRepo.save(ticket);
    }

    public List<Ticket> listarTicketsPorUsuario(Long idUsuario) {
        return ticketRepo.findByIdUsuarioOrderByFechaDesc(idUsuario);
    }

    public List<Ticket> listarTodos() {
        return ticketRepo.findAll(Sort.by(Sort.Direction.DESC, "fecha"));
    }

    public Ticket findById(Long id) {
        return ticketRepo.findById(id).orElseThrow(()-> new RuntimeException("Ticket no encontrado con ID: "+id));
    }

    public Ticket asignarSoporte(Long idTicket, Long idSoporte) {
        Ticket ticket = ticketRepo.findById(idTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        ticket.setIdSoporte(idSoporte);
        return ticketRepo.save(ticket);
    }

    public Ticket cambiarEstado(Long idTicket, String nuevoEstado) {
        Ticket ticket = ticketRepo.findById(idTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        EstadoTicket estado = estadoRepo.findByNombre(nuevoEstado)
                .orElseThrow(() -> new RuntimeException("Estado inválido"));
        ticket.setEstado(estado);
        return ticketRepo.save(ticket);
    }
}