package com.gym.membresias.controller;

import com.gym.membresias.model.Membresia;
import com.gym.membresias.model.Plan;
import com.gym.membresias.security.RoleValidator;
import com.gym.membresias.service.MembresiaService;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de MembresiaController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class MembresiaControllerTest {

    @Mock
    private RoleValidator roleValidator;

    @Mock
    private MembresiaService membresiaService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MembresiaController membresiaController;

    private Membresia membresia;
    private Plan plan;

    @BeforeEach
    void setUp() {
        plan = new Plan(1L, "Plan Básico", "Acceso a sala", 19990.0, 30, null);
        membresia = new Membresia(1L, LocalDate.now(), LocalDate.now().plusDays(30), 2L, 19990.0, plan);
    }

    // ───────────────────────── listarMembresias ─────────────────────────

    @Test
    void listarMembresias_conDatos_retorna200() {
        when(membresiaService.findAll()).thenReturn(List.of(membresia));

        ResponseEntity<List<Membresia>> response = membresiaController.listarMembresias(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roleValidator).requireRole(request, "ADMINISTRADOR");
    }

    @Test
    void listarMembresias_sinDatos_retorna204() {
        when(membresiaService.findAll()).thenReturn(List.of());

        ResponseEntity<List<Membresia>> response = membresiaController.listarMembresias(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // ───────────────────────── obtenerPorId ─────────────────────────

    @Test
    void obtenerPorId_existente_retorna200() {
        when(membresiaService.findById(1L)).thenReturn(Optional.of(membresia));

        ResponseEntity<Membresia> response = membresiaController.obtenerPorId(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerPorId_inexistente_retorna404() {
        when(membresiaService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Membresia> response = membresiaController.obtenerPorId(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── listarPorUsuario ─────────────────────────

    @Test
    void listarPorUsuario_conDatos_retorna200() {
        when(membresiaService.findAllByIdUser(2L)).thenReturn(List.of(membresia));

        ResponseEntity<List<Membresia>> response = membresiaController.listarPorUsuario(2L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ───────────────────────── listarMembresiasCliente ─────────────────────────

    @Test
    void listarMembresiasCliente_devuelveMembresiasDelHeaderXUserId() {
        when(request.getHeader("X-User-Id")).thenReturn("2");
        when(membresiaService.findAllByIdUser(2L)).thenReturn(List.of(membresia));

        ResponseEntity<List<Membresia>> response = membresiaController.listarMembresiasCliente(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roleValidator).requireRole(request, "CLIENTE");
    }

    // ───────────────────────── crearMembresia ─────────────────────────

    @Test
    void crearMembresia_datosValidos_retorna201ConFechasCalculadas() {
        when(membresiaService.save(any(Membresia.class))).thenReturn(membresia);

        ResponseEntity<Membresia> response = membresiaController.crearMembresia(membresia, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        // El controller debe forzar idMembresia a null y calcular fechaInicio/fechaTermino
        assertNull(membresia.getIdMembresia());
        assertEquals(LocalDate.now(), membresia.getFechaInicio());
        assertEquals(LocalDate.now().plusDays(30), membresia.getFechaTermino());
    }

    @Test
    void crearMembresia_errorAlGuardar_retorna404() {
        when(membresiaService.save(any(Membresia.class)))
                .thenThrow(new RuntimeException("Error al guardar"));

        ResponseEntity<Membresia> response = membresiaController.crearMembresia(membresia, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── renovar ─────────────────────────

    @Test
    void renovar_membresiaValida_retorna200() {
        when(request.getHeader("X-User-Id")).thenReturn("2");
        when(membresiaService.renovarMembresia(2L, 1L)).thenReturn(membresia);

        ResponseEntity<Membresia> response = membresiaController.renovar("1", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roleValidator).requireRole(request, "CLIENTE");
    }
}
