package com.gym.membresias.controller;

import com.gym.membresias.model.Plan;
import com.gym.membresias.security.RoleValidator;
import com.gym.membresias.service.PlanService;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PlanController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class PlanControllerTest {

    @Mock
    private RoleValidator roleValidator;

    @Mock
    private PlanService planService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PlanController planController;

    private Plan plan;

    @BeforeEach
    void setUp() {
        plan = new Plan(1L, "Plan Básico", "Acceso a sala", 19990.0, 30, null);
    }

    // ───────────────────────── listarPlanes ─────────────────────────

    @Test
    void listarPlanes_conDatos_retorna200() {
        when(planService.findAll()).thenReturn(List.of(plan));

        ResponseEntity<List<Plan>> response = planController.listarPlanes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarPlanes_sinDatos_retorna204() {
        when(planService.findAll()).thenReturn(List.of());

        ResponseEntity<List<Plan>> response = planController.listarPlanes();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // ───────────────────────── obtenerPlan ─────────────────────────

    @Test
    void obtenerPlan_existente_retorna200() {
        when(planService.findById(1L)).thenReturn(Optional.of(plan));

        ResponseEntity<Plan> response = planController.obtenerPlan(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerPlan_inexistente_retorna404() {
        when(planService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Plan> response = planController.obtenerPlan(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── crearPlan ─────────────────────────

    @Test
    void crearPlan_datosValidos_retorna201() {
        when(planService.save(any(Plan.class))).thenReturn(plan);

        ResponseEntity<Plan> response = planController.crearPlan(plan, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(roleValidator).requireRole(request, "ADMINISTRADOR");
    }

    @Test
    void crearPlan_idForzadoANull_antesDeGuardar() {
        plan.setIdPlan(99L);
        when(planService.save(any(Plan.class))).thenReturn(plan);

        planController.crearPlan(plan, request);

        assertNull(plan.getIdPlan());
    }

    @Test
    void crearPlan_errorAlGuardar_retorna404() {
        when(planService.save(any(Plan.class))).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Plan> response = planController.crearPlan(plan, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── eliminarPlan ─────────────────────────

    @Test
    void eliminarPlan_existente_retorna204() {
        doNothing().when(planService).deleteById(1L);

        ResponseEntity<Void> response = planController.eliminarPlan(1L, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roleValidator).requireRole(request, "ADMINISTRADOR");
    }
}
