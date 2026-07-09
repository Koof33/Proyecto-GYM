package com.gym.membresias.controller;

import com.gym.membresias.model.Incluido;
import com.gym.membresias.security.RoleValidator;
import com.gym.membresias.service.IncluidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incluidos")
public class IncluidoController {
    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private IncluidoService incluidoService;

//

    @Operation(
        summary = "Lista por plan",
        description = "Lista el plan disponible"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/plan/{idPlan}")
    public ResponseEntity<List<Incluido>> listarPorPlan(@PathVariable Long idPlan, HttpServletRequest request) {
        List<Incluido> incluidos = incluidoService.listarPorPlan(idPlan);
        if (incluidos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(incluidos);
    }

//

    @Operation(
        summary = "Crear incluido",
        description = "Crear incluido"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Incluido creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping
    public ResponseEntity<Incluido> crearIncluido(@RequestBody Incluido incluido, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            incluido.setIdIncluido(null);
            Incluido nuevoIncluido = incluidoService.save(incluido);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoIncluido);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

//

    @Operation(
        summary = "Eliminar incluido",
        description = "Elimina un incluido en el plan"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Incluido Eliminado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIncluido(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            incluidoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
