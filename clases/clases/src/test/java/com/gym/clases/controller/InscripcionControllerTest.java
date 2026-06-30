package com.gym.clases.controller;

import com.gym.clases.model.Clase;
import com.gym.clases.model.EstadoClase;
import com.gym.clases.model.EstadoInscripcion;
import com.gym.clases.model.Inscripcion;
import com.gym.clases.security.RoleValidator;
import com.gym.clases.service.InscripcionService;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de InscripcionController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class InscripcionControllerTest {

    @Mock
    private InscripcionService inscripcionService;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private InscripcionController inscripcionController;

    private Inscripcion inscripcion;

    @BeforeEach
    void setUp() {
        EstadoClase estadoClase = new EstadoClase(1L, "ACTIVO");
        Clase clase = new Clase(1L, "Yoga", "desc", 1L, 4L, LocalDate.now(), estadoClase);
        EstadoInscripcion estadoInscrito = new EstadoInscripcion(1L, "INSCRITO");

        inscripcion = new Inscripcion();
        inscripcion.setIdInscripcion(1L);
        inscripcion.setFInscripcion(LocalDate.now());
        inscripcion.setIdUser(2L);
        inscripcion.setClase(clase);
        inscripcion.setEstado(estadoInscrito);
    }

    // ───────────────────────── inscribirse ─────────────────────────

    @Test
    void inscribirse_clienteValido_retorna200() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(inscripcionService.inscribirse(2L, 1L)).thenReturn(inscripcion);

        ResponseEntity<Inscripcion> response = inscripcionController.inscribirse(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2L, response.getBody().getIdUser());
        verify(validator).requireRole(request, "CLIENTE");
    }

    @Test
    void inscribirse_sinRolCliente_lanzaForbidden() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso"))
                .when(validator).requireRole(request, "CLIENTE");

        assertThrows(ResponseStatusException.class,
                () -> inscripcionController.inscribirse(1L, request));
    }

    // ───────────────────────── misInscripciones ─────────────────────────

    @Test
    void misInscripciones_devuelveListaDelUsuarioAutenticado() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(inscripcionService.inscripcionesDeUsuario(2L)).thenReturn(List.of(inscripcion));

        ResponseEntity<List<Inscripcion>> response = inscripcionController.misInscripciones(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // ───────────────────────── cancelar ─────────────────────────

    @Test
    void cancelar_inscripcionExistente_cambiaEstadoARetorna200() {
        EstadoInscripcion cancelado = new EstadoInscripcion(2L, "CANCELADO");
        inscripcion.setEstado(cancelado);
        when(inscripcionService.cancelarInscripcion(1L)).thenReturn(inscripcion);

        ResponseEntity<Inscripcion> response = inscripcionController.cancelar(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CANCELADO", response.getBody().getEstado().getNombre());
        verify(validator).requireRole(request, "CLIENTE");
    }

    // ───────────────────────── inscritos ─────────────────────────

    @Test
    void inscritos_devuelveListaDeInscritosEnClase() {
        when(inscripcionService.inscritosEnClase(1L)).thenReturn(List.of(inscripcion));

        ResponseEntity<List<Inscripcion>> response = inscripcionController.inscritos(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(validator).requireRole(request, "ENTRENADOR", "COORDINADOR");
    }

    // ───────────────────────── historial ─────────────────────────

    @Test
    void historial_devuelveClasesFinalizadasDelUsuario() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(inscripcionService.historialFinalizadas(2L)).thenReturn(List.of(inscripcion));

        ResponseEntity<List<Inscripcion>> response = inscripcionController.historial(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
