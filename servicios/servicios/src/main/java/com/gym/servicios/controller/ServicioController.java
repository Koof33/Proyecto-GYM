package com.gym.servicios.controller;

import com.gym.servicios.model.Servicio;
import com.gym.servicios.security.RoleValidator;
import com.gym.servicios.service.ServicioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;
    private final RoleValidator validator;

//

    @Operation(
        summary = "Crear servicio",
        description = "Crea un servicio"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Servicio creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping
    public ResponseEntity<Servicio> crear(@RequestBody Servicio servicio, HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        return ResponseEntity.ok(servicioService.crear(servicio.getNombre(), servicio.getDescripcion()));
    }

//

    @Operation(
        summary = "Listar servicio",
        description = "Lista todos los servicios"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Servicios listados correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping
    public ResponseEntity<List<Servicio>> listar(HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        return ResponseEntity.ok(servicioService.listarTodos());
    }

//

    @Operation(
        summary = "Listar servicio activos",
        description = "Lista todos los servicios activos"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Servicios activos listados correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/activos")
    public ResponseEntity<List<Servicio>> listarActivos() {
        return ResponseEntity.ok(servicioService.listarActivos());
    }

//

    @Operation(
        summary = "Actualizar servicio",
        description = "Actualiza servicio"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Servicio Actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}")
    public ResponseEntity<Servicio> actualizar(@PathVariable Long id, @RequestBody Servicio servicio, HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        return ResponseEntity.ok(servicioService.actualizar(id, servicio.getNombre(), servicio.getDescripcion()));
    }

//

    @Operation(
        summary = "Desactiva servicio",
        description = "Desactiva servicio"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Servicio desactivado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Servicio> desactivarServicio(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        try{
            return ResponseEntity.ok(servicioService.desactivarServicio(id));

        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

//

    @Operation(
        summary = "Activa servicio",
        description = "Activa servicio"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Servicio activado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}/activar")
    public ResponseEntity<Servicio> activarServicio(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        try{
            return ResponseEntity.ok(servicioService.activarServicio(id));
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

//

    @Operation(
        summary = "Obtiene servicio",
        description = "Obtiene servicio por id"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Servicio obtenido correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/{id}")
    public ResponseEntity<Servicio> obtener(@PathVariable Long id, HttpServletRequest request) {
        return servicioService.obtenerPorId(id)
                .map(servicio -> ResponseEntity.ok(servicio))
                .orElse(ResponseEntity.notFound().build());
    }


}

