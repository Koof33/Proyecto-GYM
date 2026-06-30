package com.gym.membresias.controller;

import com.gym.membresias.model.Incluido;
import com.gym.membresias.model.Plan;
import com.gym.membresias.security.RoleValidator;
import com.gym.membresias.service.IncluidoService;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de IncluidoController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class IncluidoControllerTest {

    @Mock
    private RoleValidator roleValidator;

    @Mock
    private IncluidoService incluidoService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private IncluidoController incluidoController;

    private Incluido incluido;
    private Plan plan;

    @BeforeEach
    void setUp() {
        plan = new Plan(1L, "Plan Básico", "Acceso a sala", 19990.0, 30, null);
        incluido = new Incluido(1L, 5L, plan);
    }

    // ───────────────────────── listarPorPlan ─────────────────────────

    @Test
    void listarPorPlan_conDatos_retorna200() {
        when(incluidoService.listarPorPlan(1L)).thenReturn(List.of(incluido));

        ResponseEntity<List<Incluido>> response = incluidoController.listarPorPlan(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarPorPlan_sinDatos_retorna204() {
        when(incluidoService.listarPorPlan(99L)).thenReturn(List.of());

        ResponseEntity<List<Incluido>> response = incluidoController.listarPorPlan(99L, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // ───────────────────────── crearIncluido ─────────────────────────

    @Test
    void crearIncluido_datosValidos_retorna201() {
        when(incluidoService.save(any(Incluido.class))).thenReturn(incluido);

        ResponseEntity<Incluido> response = incluidoController.crearIncluido(incluido, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(roleValidator).requireRole(request, "ADMINISTRADOR");
    }

    @Test
    void crearIncluido_idForzadoANull_antesDeGuardar() {
        incluido.setIdIncluido(50L);
        when(incluidoService.save(any(Incluido.class))).thenReturn(incluido);

        incluidoController.crearIncluido(incluido, request);

        assertNull(incluido.getIdIncluido());
    }

    @Test
    void crearIncluido_errorAlGuardar_retorna404() {
        when(incluidoService.save(any(Incluido.class)))
                .thenThrow(new RuntimeException("Plan no encontrado"));

        ResponseEntity<Incluido> response = incluidoController.crearIncluido(incluido, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── eliminarIncluido ─────────────────────────

    @Test
    void eliminarIncluido_existente_retorna204() {
        doNothing().when(incluidoService).deleteById(1L);

        ResponseEntity<Void> response = incluidoController.eliminarIncluido(1L, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void eliminarIncluido_inexistente_retorna404() {
        doThrow(new RuntimeException("No existe")).when(incluidoService).deleteById(99L);

        ResponseEntity<Void> response = incluidoController.eliminarIncluido(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
