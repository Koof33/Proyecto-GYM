package com.gym.comentarios.controller;

import com.gym.comentarios.model.Comentarios;
import com.gym.comentarios.security.RoleValidator;
import com.gym.comentarios.service.ComentariosService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentariosController {

    private final ComentariosService resenaService;
    private final RoleValidator validator;

    @PostMapping
    public ResponseEntity<Comentarios> crear(@RequestBody Comentarios resena, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(resenaService.crear(idUsuario, resena.getIdServicio(), resena.getComentario(), resena.getCalificacion()));
    }

    @GetMapping("/mis")
    public ResponseEntity<List<Comentarios>> misResenas(HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(idUsuario));
    }

    @GetMapping("/servicio/{idServicio}/promedio")
    public ResponseEntity<Double> promedioPorServicio(@PathVariable Long idServicio, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");
        return ResponseEntity.ok(resenaService.obtenerPromedioCalificacionPorServicio(idServicio));
    }

    @GetMapping("/servicio/{idServicio}")
    public ResponseEntity<List<Comentarios>> porServicio(@PathVariable Long idServicio, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR", "COORDINADOR", "CLIENTE", "SOPORTE", "ADMINISTRADOR");
        return ResponseEntity.ok(resenaService.obtenerPorServicio(idServicio));
    }
}

