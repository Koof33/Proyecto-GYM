package com.gym.soporte.controller;

import com.gym.soporte.model.EstadoTicket;
import com.gym.soporte.model.Historial;
import com.gym.soporte.model.Motivo;
import com.gym.soporte.model.Ticket;
import com.gym.soporte.security.RoleValidator;
import com.gym.soporte.service.HistorialService;

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
 * Pruebas unitarias de HistorialController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class HistorialControllerTest {

    @Mock
    private HistorialService historialService;

    @Mock
    private RoleValidator roleValidator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private HistorialController historialController;

    private Historial historial;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        Motivo motivo = new Motivo(1L, "Problema con membresía");
        EstadoTicket pendiente = new EstadoTicket(1L, "PENDIENTE");
        ticket = new Ticket(1L, LocalDateTime.now(), "desc", 2L, null, motivo, pendiente);

        historial = new Historial(1L, "CLIENTE", "Tengo un problema con mi cuenta", LocalDateTime.now(), ticket);
    }

    // ───────────────────────── responder ─────────────────────────

    @Test
    void responder_comoCliente_etiquetaEntradaComoCliente() {
        when(request.getHeader("X-User-Roles")).thenReturn("CLIENTE");
        when(historialService.agregarEntrada(1L, "CLIENTE", "Mensaje del cliente"))
                .thenReturn(historial);

        ResponseEntity<Historial> response = historialController.responder(1L, "Mensaje del cliente", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CLIENTE", response.getBody().getTipo());
        verify(roleValidator).requireRole(request, "CLIENTE", "SOPORTE");
    }

    @Test
    void responder_comoSoporte_etiquetaEntradaComoSoporte() {
        Historial historialSoporte = new Historial(2L, "SOPORTE", "Respuesta de soporte", LocalDateTime.now(), ticket);
        when(request.getHeader("X-User-Roles")).thenReturn("SOPORTE");
        when(historialService.agregarEntrada(1L, "SOPORTE", "Respuesta de soporte"))
                .thenReturn(historialSoporte);

        ResponseEntity<Historial> response = historialController.responder(1L, "Respuesta de soporte", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SOPORTE", response.getBody().getTipo());
    }

    @Test
    void responder_ticketInexistente_lanzaRuntimeException() {
        when(request.getHeader("X-User-Roles")).thenReturn("CLIENTE");
        when(historialService.agregarEntrada(99L, "CLIENTE", "Mensaje"))
                .thenThrow(new RuntimeException("Ticket no encontrado"));

        assertThrows(RuntimeException.class,
                () -> historialController.responder(99L, "Mensaje", request));
    }

    // ───────────────────────── historial ─────────────────────────

    @Test
    void historial_conDatos_retorna200() {
        when(historialService.listarPorTicket(1L)).thenReturn(List.of(historial));

        ResponseEntity<List<Historial>> response = historialController.historial(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(roleValidator).requireRole(request, "CLIENTE", "SOPORTE");
    }

    @Test
    void historial_sinDatos_retorna204() {
        when(historialService.listarPorTicket(99L)).thenReturn(List.of());

        ResponseEntity<List<Historial>> response = historialController.historial(99L, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
