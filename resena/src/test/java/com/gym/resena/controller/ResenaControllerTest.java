package com.gym.resena.controller;

import com.gym.resena.model.Resena;
import com.gym.resena.security.RoleValidator;
import com.gym.resena.service.ResenaService;

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
 * Pruebas unitarias de ResenaController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ResenaControllerTest {

    @Mock
    private ResenaService resenaService;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ResenaController resenaController;

    private Resena resena;
    private Resena resenaEntrada;

    @BeforeEach
    void setUp() {
        resena = new Resena(1L, LocalDate.now(), "Excelente clase", 2L, 1L, 4.5);
        resenaEntrada = new Resena();
        resenaEntrada.setIdServicio(1L);
        resenaEntrada.setComentario("Excelente clase");
        resenaEntrada.setCalificacion(4.5);
    }

    // ───────────────────────── crear ─────────────────────────

    @Test
    void crear_datosValidos_retorna200() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(resenaService.crear(2L, 1L, "Excelente clase", 4.5)).thenReturn(resena);

        ResponseEntity<Resena> response = resenaController.crear(resenaEntrada, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4.5, response.getBody().getCalificacion());
        verify(validator).requireRole(request, "CLIENTE");
    }

    @Test
    void crear_calificacionFueraDeRango_lanzaIllegalArgumentException() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(resenaService.crear(2L, 1L, "Excelente clase", 4.5))
                .thenThrow(new IllegalArgumentException("La calificación debe estar entre 1.0 y 10.0"));

        assertThrows(IllegalArgumentException.class,
                () -> resenaController.crear(resenaEntrada, request));
    }

    @Test
    void crear_sinRolCliente_lanzaForbidden() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso"))
                .when(validator).requireRole(request, "CLIENTE");

        assertThrows(ResponseStatusException.class,
                () -> resenaController.crear(resenaEntrada, request));
    }

    // ───────────────────────── misResenas ─────────────────────────

    @Test
    void misResenas_devuelveResenasDelUsuarioAutenticado() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(resenaService.obtenerPorUsuario(2L)).thenReturn(List.of(resena));

        ResponseEntity<List<Resena>> response = resenaController.misResenas(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // ───────────────────────── promedioPorServicio ─────────────────────────

    @Test
    void promedioPorServicio_devuelvePromedioCalculado() {
        when(resenaService.obtenerPromedioCalificacionPorServicio(1L)).thenReturn(4.5);

        ResponseEntity<Double> response = resenaController.promedioPorServicio(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4.5, response.getBody());
        verify(validator).requireRole(request, "CLIENTE", "ENTRENADOR", "ADMINISTRADOR");
    }

    @Test
    void promedioPorServicio_sinResenas_devuelveCero() {
        when(resenaService.obtenerPromedioCalificacionPorServicio(99L)).thenReturn(0.0);

        ResponseEntity<Double> response = resenaController.promedioPorServicio(99L, request);

        assertEquals(0.0, response.getBody());
    }

    // ───────────────────────── porServicio ─────────────────────────

    @Test
    void porServicio_devuelveListaDeResenasDelServicio() {
        when(resenaService.obtenerPorServicio(1L)).thenReturn(List.of(resena));

        ResponseEntity<List<Resena>> response = resenaController.porServicio(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(validator).requireRole(request, "ENTRENADOR", "CLIENTE", "SOPORTE", "ADMINISTRADOR");
    }
}
