package com.gym.soporte.service;

import com.gym.soporte.model.Historial;
import com.gym.soporte.model.Ticket;
import com.gym.soporte.repository.HistorialRepository;
import com.gym.soporte.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class HistorialService {
    @Autowired
    private HistorialRepository historialRepo;
    @Autowired
    private TicketRepository ticketRepo;

    public Historial agregarEntrada(Long idTicket, String tipo, String mensaje) {
        Ticket ticket = ticketRepo.findById(idTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        Historial h = new Historial();
        h.setFechaMensaje(LocalDateTime.now());
        h.setMensaje(mensaje);
        h.setTipo(tipo);
        h.setTicket(ticket);

        return historialRepo.save(h);
    }

    public List<Historial> listarPorTicket(Long idTicket) {
        return historialRepo.findByTicketIdOrderByFechaMensajeAsc(idTicket);
    }
}