package com.gym.gestionperfil.controller;

import com.gym.gestionperfil.model.BloqueDisponibilidad;
import com.gym.gestionperfil.model.Enum.DiaSemana;
import com.gym.gestionperfil.security.RoleValidator;
import com.gym.gestionperfil.service.BloqueDisponibilidadService;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de BloqueDisponibilidadController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class BloqueDisponibilidadControllerTest {

    @Mock
    private BloqueDisponibilidadService service;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private BloqueDisponibilidadController bloqueController;

    private BloqueDisponibilidad bloque;

    @BeforeEach
    void setUp() {
        bloque = new BloqueDisponibilidad(
                1L, DiaSemana.LUNES, LocalTime.of(9, 0), LocalTime.of(12, 0), 4L
        );
    }

    // ───────────────────────── listar ─────────────────────────

    @Test
    void listar_devuelveBloquesDelEntrenadorAutenticado() {
        when(validator.getUserId(request)).thenReturn(4L);
        when(service.listarPorEntrenador(4L)).thenReturn(List.of(bloque));

        ResponseEntity<List<BloqueDisponibilidad>> response = bloqueController.listar(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(validator).requireRole(request, "ENTRENADOR");
    }

    // ───────────────────────── crear ─────────────────────────

    @Test
    void crear_bloqueValido_retorna200() {
        when(validator.getUserId(request)).thenReturn(4L);
        when(service.agregar(4L, bloque)).thenReturn(bloque);

        ResponseEntity<BloqueDisponibilidad> response = bloqueController.crear(bloque, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(DiaSemana.LUNES, response.getBody().getDia());
    }

    // ───────────────────────── actualizar ─────────────────────────

    @Test
    void actualizar_bloqueExistenteYPropio_retorna200() {
        when(validator.getUserId(request)).thenReturn(4L);
        when(service.actualizar(4L, 1L, bloque)).thenReturn(Optional.of(bloque));

        ResponseEntity<BloqueDisponibilidad> response = bloqueController.actualizar(1L, bloque, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void actualizar_bloqueInexistenteOAjeno_retorna404() {
        when(validator.getUserId(request)).thenReturn(4L);
        when(service.actualizar(4L, 99L, bloque)).thenReturn(Optional.empty());

        ResponseEntity<BloqueDisponibilidad> response = bloqueController.actualizar(99L, bloque, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── eliminar ─────────────────────────

    @Test
    void eliminar_bloqueExistenteYPropio_retorna204() {
        when(validator.getUserId(request)).thenReturn(4L);
        when(service.eliminar(4L, 1L)).thenReturn(true);

        ResponseEntity<Void> response = bloqueController.eliminar(1L, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void eliminar_bloqueInexistenteOAjeno_retorna404() {
        when(validator.getUserId(request)).thenReturn(4L);
        when(service.eliminar(4L, 99L)).thenReturn(false);

        ResponseEntity<Void> response = bloqueController.eliminar(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── obtenerDisponibilidadEntrenador ─────────────────────────

    @Test
    void obtenerDisponibilidadEntrenador_devuelveBloquesDelEntrenadorIndicado() {
        when(service.listarPorEntrenador(4L)).thenReturn(List.of(bloque));

        ResponseEntity<List<BloqueDisponibilidad>> response =
                bloqueController.obtenerDisponibilidadEntrenador(4L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(validator).requireRole(request, "ENTRENADOR");
    }
}
