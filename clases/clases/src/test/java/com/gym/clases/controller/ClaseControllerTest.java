package com.gym.clases.controller;

import com.gym.clases.dto.CrearClaseRequest;
import com.gym.clases.model.Clase;
import com.gym.clases.model.EstadoClase;
import com.gym.clases.security.RoleValidator;
import com.gym.clases.service.ClaseService;

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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de ClaseController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ClaseControllerTest {

    @Mock
    private ClaseService claseService;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ClaseController claseController;

    private Clase clase;
    private EstadoClase estadoActivo;
    private CrearClaseRequest crearClaseRequest;

    @BeforeEach
    void setUp() {
        estadoActivo = new EstadoClase(1L, "ACTIVO");

        clase = new Clase();
        clase.setIdClase(1L);
        clase.setNombres("Yoga Matutino");
        clase.setDescripcion("Clase de yoga para principiantes");
        clase.setIdServicio(1L);
        clase.setIdUsuario(4L); // id entrenador
        clase.setFClase(LocalDate.of(2026, 7, 15));
        clase.setEstado(estadoActivo);

        crearClaseRequest = new CrearClaseRequest();
        crearClaseRequest.setNombre("Yoga Matutino");
        crearClaseRequest.setDescripcion("Clase de yoga para principiantes");
        crearClaseRequest.setIdServicio(1L);
        crearClaseRequest.setIdEntrenador(4L);
        crearClaseRequest.setFecha(LocalDate.of(2026, 7, 15));
    }

    // ───────────────────────── crearClase ─────────────────────────

    @Test
    void crearClase_datosValidos_retorna200ConClaseCreada() {
        when(claseService.crearClase(
                crearClaseRequest.getNombre(),
                crearClaseRequest.getDescripcion(),
                crearClaseRequest.getIdServicio(),
                crearClaseRequest.getIdEntrenador(),
                crearClaseRequest.getFecha()
        )).thenReturn(clase);

        ResponseEntity<Clase> response = claseController.crearClase(crearClaseRequest, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Yoga Matutino", response.getBody().getNombres());
        verify(validator).requireRole(request, "ENTRENADOR");
    }

    @Test
    void crearClase_sinRolEntrenador_lanzaForbidden() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso"))
                .when(validator).requireRole(request, "ENTRENADOR");

        assertThrows(ResponseStatusException.class,
                () -> claseController.crearClase(crearClaseRequest, request));

        verify(claseService, never()).crearClase(any(), any(), any(), any(), any());
    }

    // ───────────────────────── cambiarEntrenador ─────────────────────────

    @Test
    void cambiarEntrenador_claseExistente_actualizaIdUsuarioYRetorna200() {
        when(claseService.buscarPorId(1L)).thenReturn(Optional.of(clase));
        when(claseService.actualizar(any(Clase.class))).thenReturn(clase);

        ResponseEntity<Clase> response = claseController.cambiarEntrenador(1L, 7L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(7L, clase.getIdUsuario());
        verify(claseService).actualizar(clase);
    }

    @Test
    void cambiarEntrenador_claseInexistente_lanzaNoSuchElementException() {
        when(claseService.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> claseController.cambiarEntrenador(99L, 7L, request));
    }

    // ───────────────────────── cambiarEstado ─────────────────────────

    @Test
    void cambiarEstado_claseExistente_retorna200ConNuevoEstado() {
        EstadoClase cancelado = new EstadoClase(2L, "CANCELADO");
        Clase claseCancelada = new Clase(1L, "Yoga Matutino", "desc", 1L, 4L, LocalDate.now(), cancelado);
        when(claseService.cambiarEstado(1L, "CANCELADO")).thenReturn(claseCancelada);

        ResponseEntity<Clase> response = claseController.cambiarEstado(1L, "CANCELADO", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CANCELADO", response.getBody().getEstado().getNombre());
    }

    // ───────────────────────── clasesDelEntrenador ─────────────────────────

    @Test
    void clasesDelEntrenador_devuelveListaDeClasesPropias() {
        when(validator.getUserId(request)).thenReturn(4L);
        when(claseService.listarPorEntrenador(4L)).thenReturn(List.of(clase));

        ResponseEntity<List<Clase>> response = claseController.clasesDelEntrenador(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(validator).requireRole(request, "ENTRENADOR");
    }
}
