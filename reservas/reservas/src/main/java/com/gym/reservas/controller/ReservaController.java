package com.gym.reservas.controller;

import com.gym.reservas.model.EstadoReservaHistorial;
import com.gym.reservas.model.Reserva;
import com.gym.reservas.security.RoleValidator;
import com.gym.reservas.service.ReservaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final RoleValidator validator;

//

    @Operation(
        summary = "Crea una reserva",
        description = "Crea una reserva"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reserva creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
    @RequestParam String descripcion,
    @RequestParam Long idServicio,
    @RequestParam Long idEntrenador,
    HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(reservaService.crear(fecha, descripcion, idUsuario, idServicio, idEntrenador));
    }

//

    @Operation(
        summary = "Muestra mis reservas",
        description = "Muestra las reservas por usuario"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservas listadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/mis")
    public ResponseEntity<List<Reserva>> misReservas(HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        return ResponseEntity.ok(reservaService.listarPorUsuario(validator.getUserId(request)));
    }

//

    @Operation(
        summary = "Muestra reservas entrenador",
        description = "Muestra las reservas asignadas por entrenador"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservas listadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/asignadas")
    public ResponseEntity<List<Reserva>> asignadas(HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(reservaService.listarPorEntrenador(validator.getUserId(request)));
    }

//

    @Operation(
        summary = "Cancela reserva",
        description = "Cancela reserva"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservas cancelada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelar(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        return ResponseEntity.ok(reservaService.cambiarEstado(id, "CANCELADA", "Cancelado por cliente"));
    }

//

    @Operation(
        summary = "Confirma reserva",
        description = "Confirma reserva"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservas confirmada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Reserva> confirmar(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(reservaService.cambiarEstado(id, "CONFIRMADA", "Confirmado por entrenador"));
    }

//

    @Operation(
        summary = "Completa reserva",
        description = "Completa reserva"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservas completada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}/completar")
    public ResponseEntity<Reserva> completar(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(reservaService.cambiarEstado(id, "COMPLETADA", "Servicio completado"));
    }

//

    @Operation(
        summary = "Historial",
        description = "Muestra historial de reservas"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Historial se mostró correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<EstadoReservaHistorial>> historial(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE", "ENTRENADOR", "ADMINISTRADOR");
        return ResponseEntity.ok(reservaService.historial(id));
    }
}
