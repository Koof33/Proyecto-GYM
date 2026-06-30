package com.gym.soporte.controller;

import com.gym.soporte.model.EstadoTicket;
import com.gym.soporte.model.Motivo;
import com.gym.soporte.model.Ticket;
import com.gym.soporte.security.RoleValidator;
import com.gym.soporte.service.TicketService;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de TicketController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private RoleValidator roleValidator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TicketController ticketController;

    private Ticket ticket;
    private Motivo motivo;
    private EstadoTicket pendiente;

    @BeforeEach
    void setUp() {
        motivo = new Motivo(1L, "Problema con membresía");
        pendiente = new EstadoTicket(1L, "PENDIENTE");

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setFecha(LocalDateTime.now());
        ticket.setDescripcion("No puedo acceder a mi cuenta");
        ticket.setIdUsuario(2L);
        ticket.setMotivo(motivo);
        ticket.setEstado(pendiente);
    }

    // ───────────────────────── crearTicket ─────────────────────────

    @Test
    void crearTicket_datosValidos_retorna201() {
        when(roleValidator.getUserId(request)).thenReturn(2L);
        when(ticketService.crearTicket("No puedo acceder a mi cuenta", 2L, 1L)).thenReturn(ticket);

        ResponseEntity<Ticket> response = ticketController.crearTicket(ticket, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(roleValidator).requireRole(request, "CLIENTE");
    }

    @Test
    void crearTicket_motivoInexistente_retorna404() {
        when(roleValidator.getUserId(request)).thenReturn(2L);
        when(ticketService.crearTicket("No puedo acceder a mi cuenta", 2L, 1L))
                .thenThrow(new RuntimeException("Motivo no encontrado"));

        ResponseEntity<Ticket> response = ticketController.crearTicket(ticket, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── misTickets ─────────────────────────

    @Test
    void misTickets_conDatos_retorna200() {
        when(roleValidator.getUserId(request)).thenReturn(2L);
        when(ticketService.listarTicketsPorUsuario(2L)).thenReturn(List.of(ticket));

        ResponseEntity<List<Ticket>> response = ticketController.misTickets(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void misTickets_sinDatos_retorna204() {
        when(roleValidator.getUserId(request)).thenReturn(2L);
        when(ticketService.listarTicketsPorUsuario(2L)).thenReturn(List.of());

        ResponseEntity<List<Ticket>> response = ticketController.misTickets(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // ───────────────────────── listarTodos ─────────────────────────

    @Test
    void listarTodos_conDatos_retorna200() {
        when(ticketService.listarTodos()).thenReturn(List.of(ticket));

        ResponseEntity<List<Ticket>> response = ticketController.listarTodos(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roleValidator).requireRole(request, "SOPORTE");
    }

    // ───────────────────────── cambiarEstado ─────────────────────────

    @Test
    void cambiarEstado_ticketExistente_normalizaAMayusculasYRetorna200() {
        EstadoTicket resuelto = new EstadoTicket(2L, "RESUELTO");
        Ticket ticketResuelto = new Ticket(1L, LocalDateTime.now(), "desc", 2L, null, motivo, resuelto);
        when(ticketService.cambiarEstado(1L, "RESUELTO")).thenReturn(ticketResuelto);

        ResponseEntity<Ticket> response = ticketController.cambiarEstado(1L, "resuelto", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("RESUELTO", response.getBody().getEstado().getNombre());
        verify(ticketService).cambiarEstado(1L, "RESUELTO");
    }

    @Test
    void cambiarEstado_ticketInexistente_retorna404() {
        when(ticketService.cambiarEstado(99L, "RESUELTO"))
                .thenThrow(new RuntimeException("Ticket no encontrado"));

        ResponseEntity<Ticket> response = ticketController.cambiarEstado(99L, "resuelto", request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── asignar ─────────────────────────

    @Test
    void asignar_ticketExistente_retorna200() {
        when(roleValidator.getUserId(request)).thenReturn(5L);
        when(ticketService.asignarSoporte(1L, 5L)).thenReturn(ticket);

        ResponseEntity<Ticket> response = ticketController.asignar(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roleValidator).requireRole(request, "SOPORTE");
    }

    @Test
    void asignar_ticketInexistente_retorna404() {
        when(roleValidator.getUserId(request)).thenReturn(5L);
        when(ticketService.asignarSoporte(99L, 5L))
                .thenThrow(new RuntimeException("Ticket no encontrado"));

        ResponseEntity<Ticket> response = ticketController.asignar(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
