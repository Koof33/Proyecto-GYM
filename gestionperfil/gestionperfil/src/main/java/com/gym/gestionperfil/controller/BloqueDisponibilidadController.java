package com.gym.gestionperfil.controller;

import com.gym.gestionperfil.model.BloqueDisponibilidad;
import com.gym.gestionperfil.security.RoleValidator;
import com.gym.gestionperfil.service.BloqueDisponibilidadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perfil/disponibilidad")
@RequiredArgsConstructor
public class BloqueDisponibilidadController {

    private final BloqueDisponibilidadService service;
    private final RoleValidator validator;

//

    @Operation(
        summary = "Listar perfil",
        description = "Lista todas los planes disponibles"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping
    public ResponseEntity<List<BloqueDisponibilidad>> listar(HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(service.listarPorEntrenador(idUsuario));
    }

//

    @Operation(
        summary = "Crear dispo",
        description = "Crear dispo"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dispo creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping
    public ResponseEntity<BloqueDisponibilidad> crear(@RequestBody BloqueDisponibilidad bloque, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(service.agregar(idUsuario, bloque));
    }

//

    @Operation(
        summary = "Actualizar dispo",
        description = "Actualizar dispo"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "dispo actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}")
    public ResponseEntity<BloqueDisponibilidad> actualizar(@PathVariable Long id, @RequestBody BloqueDisponibilidad bloque, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "ENTRENADOR");

        return service.actualizar(idUsuario, id, bloque)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//

    @Operation(
        summary = "Eliminar dispo",
        description = "Eliminar dispo"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "dispo eliminada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "ENTRENADOR");

        boolean eliminado = service.eliminar(idUsuario, id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

//

    @Operation(
        summary = "Obtener dispo",
        description = "Obtiene la disponibilidad de un entrenador"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dispo obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/entrenador/{idEntrenador}")
    public ResponseEntity<List<BloqueDisponibilidad>> obtenerDisponibilidadEntrenador(@PathVariable Long idEntrenador, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        List<BloqueDisponibilidad> disponibilidad = service.listarPorEntrenador(idEntrenador);
        return ResponseEntity.ok(disponibilidad);
    }

}
