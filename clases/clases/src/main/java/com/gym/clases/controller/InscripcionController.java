package com.gym.clases.controller;

import com.gym.clases.model.Inscripcion;
import com.gym.clases.security.RoleValidator;
import com.gym.clases.service.InscripcionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final RoleValidator validator;

//

    @Operation(
        summary = "Inscribe clase",
        description = "Inscribe un cliente a una clase"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente inscrito correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    // CLIENTE: Inscribirse a una clase
    @PostMapping("/clase/{idClase}")
    public ResponseEntity<Inscripcion> inscribirse(@PathVariable Long idClase,
        HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(inscripcionService.inscribirse(idUsuario, idClase));
    }

//

    @Operation(
        summary = "Listar mis inscripciones",
        description = "Lista las inscripciones del cliente logueado"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripciones listadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    // CLIENTE: Ver mis inscripciones activas
    @GetMapping("/mis")
    public ResponseEntity<List<Inscripcion>> misInscripciones(HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(inscripcionService.inscripcionesDeUsuario(idUsuario));
    }

//

    @Operation(
        summary = "Cancela inscripciones",
        description = "Cancela una inscripción del cliente logueado"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inscripción cancelada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    // CLIENTE: Cancelar una inscripción
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Inscripcion> cancelar(@PathVariable Long id,
                                                HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        return ResponseEntity.ok(inscripcionService.cancelarInscripcion(id));
    }

//

    @Operation(
        summary = "Listar inscritos",
        description = "Lista los users inscritos en una clase"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripciones listadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    // ENTRENADOR / COORDINADOR: Ver inscritos en una clase
    @GetMapping("/clase/{idClase}/inscritos")
    public ResponseEntity<List<Inscripcion>> inscritos(@PathVariable Long idClase,
        HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR", "COORDINADOR");
        return ResponseEntity.ok(inscripcionService.inscritosEnClase(idClase));
    }

//

    @Operation(
        summary = "Historial clases",
        description = "Lista las clases finalizadas"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clases listadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    // CLIENTE: Ver historial (clases finalizadas)
    @GetMapping("/historial")
    public ResponseEntity<List<Inscripcion>> historial(HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(inscripcionService.historialFinalizadas(idUsuario));
    }
}

