package com.gym.servicios.controller;

import com.gym.servicios.model.EstadoServicio;
import com.gym.servicios.model.Servicio;
import com.gym.servicios.security.RoleValidator;
import com.gym.servicios.service.ServicioService;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de ServicioController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ServicioControllerTest {

    @Mock
    private ServicioService servicioService;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ServicioController servicioController;

    private Servicio servicio;
    private EstadoServicio activo;

    @BeforeEach
    void setUp() {
        activo = new EstadoServicio(1L, "ACTIVO");
        servicio = new Servicio(1L, "Yoga", "Clase de yoga para todos los niveles", activo);
    }

    // ───────────────────────── crear ─────────────────────────

    @Test
    void crear_datosValidos_retorna200() {
        when(servicioService.crear("Yoga", "Clase de yoga para todos los niveles")).thenReturn(servicio);

        ResponseEntity<Servicio> response = servicioController.crear(servicio, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Yoga", response.getBody().getNombre());
        verify(validator).requireRole(request, "ADMINISTRADOR");
    }

    @Test
    void crear_sinRolAdministrador_lanzaForbidden() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso"))
                .when(validator).requireRole(request, "ADMINISTRADOR");

        assertThrows(ResponseStatusException.class,
                () -> servicioController.crear(servicio, request));
    }

    // ───────────────────────── listar ─────────────────────────

    @Test
    void listar_devuelveTodosLosServicios() {
        when(servicioService.listarTodos()).thenReturn(List.of(servicio));

        ResponseEntity<List<Servicio>> response = servicioController.listar(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // ───────────────────────── listarActivos ─────────────────────────

    @Test
    void listarActivos_devuelveSoloServiciosActivos_sinValidarRol() {
        when(servicioService.listarActivos()).thenReturn(List.of(servicio));

        ResponseEntity<List<Servicio>> response = servicioController.listarActivos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        // Este endpoint es público, no debe llamar al RoleValidator
        verifyNoInteractions(validator);
    }

    // ───────────────────────── actualizar ─────────────────────────

    @Test
    void actualizar_servicioExistente_retorna200() {
        when(servicioService.actualizar(1L, "Yoga", "Clase de yoga para todos los niveles")).thenReturn(servicio);

        ResponseEntity<Servicio> response = servicioController.actualizar(1L, servicio, request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(validator).requireRole(request, "ADMINISTRADOR");
    }

    // ───────────────────────── desactivarServicio ─────────────────────────

    @Test
    void desactivarServicio_existente_retorna200ConEstadoInactivo() {
        EstadoServicio inactivo = new EstadoServicio(2L, "INACTIVO");
        Servicio servicioInactivo = new Servicio(1L, "Yoga", "desc", inactivo);
        when(servicioService.desactivarServicio(1L)).thenReturn(servicioInactivo);

        ResponseEntity<Servicio> response = servicioController.desactivarServicio(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("INACTIVO", response.getBody().getEstado().getNombre());
    }

    @Test
    void desactivarServicio_inexistente_retorna404() {
        when(servicioService.desactivarServicio(99L))
                .thenThrow(new RuntimeException("Servicio no encontrado"));

        ResponseEntity<Servicio> response = servicioController.desactivarServicio(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── activarServicio ─────────────────────────

    @Test
    void activarServicio_existente_retorna200ConEstadoActivo() {
        when(servicioService.activarServicio(1L)).thenReturn(servicio);

        ResponseEntity<Servicio> response = servicioController.activarServicio(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ACTIVO", response.getBody().getEstado().getNombre());
    }

    @Test
    void activarServicio_inexistente_retorna404() {
        when(servicioService.activarServicio(99L))
                .thenThrow(new RuntimeException("Servicio no encontrado"));

        ResponseEntity<Servicio> response = servicioController.activarServicio(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── obtener ─────────────────────────

    @Test
    void obtener_existente_retorna200() {
        when(servicioService.obtenerPorId(1L)).thenReturn(Optional.of(servicio));

        ResponseEntity<Servicio> response = servicioController.obtener(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtener_inexistente_retorna404() {
        when(servicioService.obtenerPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Servicio> response = servicioController.obtener(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
