package com.gym.reservas.controller;

import com.gym.reservas.model.EstadoReserva;
import com.gym.reservas.model.EstadoReservaHistorial;
import com.gym.reservas.model.Reserva;
import com.gym.reservas.security.RoleValidator;
import com.gym.reservas.service.ReservaService;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de ReservaController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    @Mock
    private ReservaService reservaService;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ReservaController reservaController;

    private Reserva reserva;
    private EstadoReserva pendiente;

    @BeforeEach
    void setUp() {
        pendiente = new EstadoReserva(1L, "PENDIENTE");
        reserva = new Reserva(1L, LocalDate.of(2026, 7, 10), LocalDateTime.now(),
                "Sesion personal", 2L, 1L, 4L, pendiente);
    }

    // ───────────────────────── crear ─────────────────────────

    @Test
    void crear_datosValidos_retorna200() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(reservaService.crear(LocalDate.of(2026, 7, 10), "Sesion personal", 2L, 1L, 4L))
                .thenReturn(reserva);

        ResponseEntity<Reserva> response = reservaController.crear(
                LocalDate.of(2026, 7, 10), "Sesion personal", 1L, 4L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator).requireRole(request, "CLIENTE");
    }

    @Test
    void crear_sinRolCliente_lanzaForbidden() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso"))
                .when(validator).requireRole(request, "CLIENTE");

        assertThrows(ResponseStatusException.class,
                () -> reservaController.crear(LocalDate.of(2026, 7, 10), "desc", 1L, 4L, request));
    }

    // ───────────────────────── misReservas ─────────────────────────

    @Test
    void misReservas_devuelveReservasDelUsuarioAutenticado() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(reservaService.listarPorUsuario(2L)).thenReturn(List.of(reserva));

        ResponseEntity<List<Reserva>> response = reservaController.misReservas(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // ───────────────────────── asignadas ─────────────────────────

    @Test
    void asignadas_devuelveReservasDelEntrenadorAutenticado() {
        when(validator.getUserId(request)).thenReturn(4L);
        when(reservaService.listarPorEntrenador(4L)).thenReturn(List.of(reserva));

        ResponseEntity<List<Reserva>> response = reservaController.asignadas(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(validator).requireRole(request, "ENTRENADOR");
    }

    // ───────────────────────── cancelar ─────────────────────────

    @Test
    void cancelar_reservaExistente_retorna200ConEstadoCancelada() {
        EstadoReserva cancelada = new EstadoReserva(2L, "CANCELADA");
        Reserva reservaCancelada = new Reserva(1L, LocalDate.now(), LocalDateTime.now(),
                "desc", 2L, 1L, 4L, cancelada);
        when(reservaService.cambiarEstado(1L, "CANCELADA", "Cancelado por cliente"))
                .thenReturn(reservaCancelada);

        ResponseEntity<Reserva> response = reservaController.cancelar(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CANCELADA", response.getBody().getEstado().getNombre());
        verify(validator).requireRole(request, "CLIENTE");
    }

    // ───────────────────────── confirmar ─────────────────────────

    @Test
    void confirmar_reservaExistente_retorna200ConEstadoConfirmada() {
        EstadoReserva confirmada = new EstadoReserva(3L, "CONFIRMADA");
        Reserva reservaConfirmada = new Reserva(1L, LocalDate.now(), LocalDateTime.now(),
                "desc", 2L, 1L, 4L, confirmada);
        when(reservaService.cambiarEstado(1L, "CONFIRMADA", "Confirmado por entrenador"))
                .thenReturn(reservaConfirmada);

        ResponseEntity<Reserva> response = reservaController.confirmar(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CONFIRMADA", response.getBody().getEstado().getNombre());
        verify(validator).requireRole(request, "ENTRENADOR");
    }

    // ───────────────────────── completar ─────────────────────────

    @Test
    void completar_reservaExistente_retorna200ConEstadoCompletada() {
        EstadoReserva completada = new EstadoReserva(4L, "COMPLETADA");
        Reserva reservaCompletada = new Reserva(1L, LocalDate.now(), LocalDateTime.now(),
                "desc", 2L, 1L, 4L, completada);
        when(reservaService.cambiarEstado(1L, "COMPLETADA", "Servicio completado"))
                .thenReturn(reservaCompletada);

        ResponseEntity<Reserva> response = reservaController.completar(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("COMPLETADA", response.getBody().getEstado().getNombre());
    }

    // ───────────────────────── historial ─────────────────────────

    @Test
    void historial_devuelveHistorialDeLaReserva() {
        EstadoReservaHistorial entrada = new EstadoReservaHistorial(
                1L, LocalDateTime.now(), "Reserva creada", pendiente, reserva);
        when(reservaService.historial(1L)).thenReturn(List.of(entrada));

        ResponseEntity<List<EstadoReservaHistorial>> response = reservaController.historial(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(validator).requireRole(request, "CLIENTE", "ENTRENADOR", "ADMINISTRADOR");
    }
}
