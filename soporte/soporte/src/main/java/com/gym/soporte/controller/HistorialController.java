package com.gym.soporte.controller;

import com.gym.soporte.model.Historial;
import com.gym.soporte.security.RoleValidator;
import com.gym.soporte.service.HistorialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial")
public class HistorialController {
    @Autowired
    private HistorialService historialService;
    @Autowired
    private RoleValidator roleValidator;

//

    @Operation(
        summary = "Responder",
        description = "Responder reportes"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reporte respondido correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping("/{idTicket}/responder")
    public ResponseEntity<Historial> responder(
            @PathVariable Long idTicket,
            @RequestParam String mensaje,
            HttpServletRequest request) {

        String rol = request.getHeader("X-User-Roles");
        roleValidator.requireRole(request, "CLIENTE", "SOPORTE");

        String tipo = rol.contains("SOPORTE") ? "SOPORTE" : "CLIENTE";
        return ResponseEntity.ok(historialService.agregarEntrada(idTicket, tipo, mensaje));
    }

//

    @Operation(
        summary = "Historial",
        description = "Muestra el historial de reportes"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial mostrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/{idTicket}")
    public ResponseEntity<List<Historial>> historial(@PathVariable Long idTicket, HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE", "SOPORTE");
        List<Historial> historial = historialService.listarPorTicket(idTicket);
        if (historial.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(historial);
    }
}