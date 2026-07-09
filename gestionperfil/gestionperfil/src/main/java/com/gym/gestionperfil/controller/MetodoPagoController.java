package com.gym.gestionperfil.controller;

import com.gym.gestionperfil.model.MetodoPago;
import com.gym.gestionperfil.security.RoleValidator;
import com.gym.gestionperfil.service.MetodoPagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perfil/metodos-pago")
@RequiredArgsConstructor
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;
    private final RoleValidator validator;

//

    @Operation(
        summary = "Listar metodos",
        description = "Obtiene metodos de pago"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metodo obtenido correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping
    public ResponseEntity<List<MetodoPago>> listar(HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        List<MetodoPago> metodos = metodoPagoService.listarPorUsuario(idUsuario);
        return ResponseEntity.ok(metodos);
    }

//

    @Operation(
        summary = "Agregar metodo de pago",
        description = "Agrega un metodo de pago"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metodo agregado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping
    public ResponseEntity<MetodoPago> agregar(@RequestBody MetodoPago metodo, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        MetodoPago guardado = metodoPagoService.agregar(idUsuario, metodo);
        return ResponseEntity.ok(guardado);
    }

//

    @Operation(
        summary = "Actualizar metodo de pago",
        description = "Actualiza el metodo de pago"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metodo de pago actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPago> actualizar(@PathVariable Long id, @RequestBody MetodoPago metodo, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        return metodoPagoService.actualizar(idUsuario, id, metodo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//

    @Operation(
        summary = "Elimina metodo de pago",
        description = "Elimina un metodo de pago"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metodo eliminado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        boolean eliminado = metodoPagoService.eliminar(idUsuario, id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

