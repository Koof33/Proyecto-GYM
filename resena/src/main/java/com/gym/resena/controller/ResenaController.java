package com.gym.resena.controller;

import com.gym.resena.model.Resena;
import com.gym.resena.security.RoleValidator;
import com.gym.resena.service.ResenaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;
    private final RoleValidator validator;

//

    @Operation(
        summary = "Crea una reseña",
        description = "Crea una reseña"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reseña creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping
    public ResponseEntity<Resena> crear(@RequestBody Resena resena, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(resenaService.crear(idUsuario, resena.getIdServicio(), resena.getComentario(), resena.getCalificacion()));
    }

//

    @Operation(
        summary = "Muestra mis reseña",
        description = "Muestra las reseñas del usuario logeado"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reseña obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/mis")
    public ResponseEntity<List<Resena>> misResenas(HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(idUsuario));
    }

//

    @Operation(
        summary = "Promedio reseñas",
        description = "Muestra un promedio de reseñas por calificación"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Promedio creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/servicio/{idServicio}/promedio")
    public ResponseEntity<Double> promedioPorServicio(@PathVariable Long idServicio, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE", "ENTRENADOR", "ADMINISTRADOR");
        return ResponseEntity.ok(resenaService.obtenerPromedioCalificacionPorServicio(idServicio));
    }

//

    @Operation(
        summary = "Obtiene reseñas",
        description = "Obtiene reseñas por servicio"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reseña obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/servicio/{idServicio}")
    public ResponseEntity<List<Resena>> porServicio(@PathVariable Long idServicio, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR", "CLIENTE", "SOPORTE", "ADMINISTRADOR");
        return ResponseEntity.ok(resenaService.obtenerPorServicio(idServicio));
    }
}

