package com.gym.clases.controller;

import com.gym.clases.dto.CrearClaseRequest;
import com.gym.clases.security.RoleValidator;
import com.gym.clases.model.Clase;
import com.gym.clases.service.ClaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clases")
@RequiredArgsConstructor
public class ClaseController {

    private final ClaseService claseService;
    private final RoleValidator validator;

//

    @Operation(
        summary = "Crear clase",
        description = "Crea una clase"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Clase creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping
    public ResponseEntity<Clase> crearClase(@RequestBody CrearClaseRequest requestBody, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(
                claseService.crearClase(
                        requestBody.getNombre(),
                        requestBody.getDescripcion(),
                        requestBody.getIdServicio(),
                        requestBody.getIdEntrenador(),
                        requestBody.getFecha()
                )
        );
    }

//

    @Operation(
        summary = "Cambia entrenador",
        description = "Cambia el entrenador"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Entrenadir modificado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}/entrenador/{nuevoIdEntrenador}")
    public ResponseEntity<Clase> cambiarEntrenador(@PathVariable Long id, @PathVariable Long nuevoIdEntrenador, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        Clase clase = claseService.buscarPorId(id).orElseThrow();
        clase.setIdUsuario(nuevoIdEntrenador);
        return ResponseEntity.ok(claseService.actualizar(clase));
    }

//

    @Operation(
        summary = "Cambia estado",
        description = "Cambia el estado del entrenador"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estado modificado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<Clase> cambiarEstado(@PathVariable Long id, @PathVariable String nuevoEstado, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(claseService.cambiarEstado(id, nuevoEstado));
    }

//

    @Operation(
        summary = "Lista clases entrenador",
        description = "Obtiene una lista de todos las clases de los entrenadores"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/mias")
    public ResponseEntity<List<Clase>> clasesDelEntrenador(HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        Long idEntrenador = validator.getUserId(request);
        return ResponseEntity.ok(claseService.listarPorEntrenador(idEntrenador));
    }
}