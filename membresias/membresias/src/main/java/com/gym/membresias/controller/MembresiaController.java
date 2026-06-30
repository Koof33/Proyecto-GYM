package com.gym.membresias.controller;

import com.gym.membresias.security.RoleValidator;
import com.gym.membresias.model.Membresia;
import com.gym.membresias.service.MembresiaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/membresias")
public class MembresiaController {
    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private MembresiaService membresiaService;

//

    @Operation(
        summary = "Lista las membresias",
        description = "Lista todas las membresias disponibles"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping
    public ResponseEntity<List<Membresia>> listarMembresias(HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Membresia> membresias = membresiaService.findAll();
        if (membresias.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(membresias);
    }

//

    @Operation(
        summary = "Obtiene membresia ",
        description = "Retorna la membresia según el id ingresado"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Membresia obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/{id}")
    public ResponseEntity<Membresia> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        return membresiaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//

    @Operation(
        summary = "Usuario membresia",
        description = "Muestra la membresia del usuario ingresado"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Membresia obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Membresia>> listarPorUsuario(@PathVariable Long idUsuario, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Membresia> membresias = membresiaService.findAllByIdUser(idUsuario);
        if (membresias.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(membresias);
    }

//

    @Operation(
        summary = "Usuario membresia",
        description = "Muestra la membresia del usuario ingresado"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Membresia obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/mis-membresias")
    public ResponseEntity<List<Membresia>> listarMembresiasCliente(HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = Long.parseLong(request.getHeader("X-User-Id"));
        List<Membresia> membresias = membresiaService.findAllByIdUser(idUsuario);
        if (membresias.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(membresiaService.findAllByIdUser(idUsuario));
    }

//

    @Operation(
        summary = "Crear Membresia",
        description = "Crea una membresia"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Membresia creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping
    public ResponseEntity<Membresia> crearMembresia(@RequestBody Membresia membresia, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR", "CLIENTE");
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaTermino = fechaInicio.plusDays(30);
        try{
            membresia.setIdMembresia(null);
            membresia.setFechaInicio(fechaInicio);
            membresia.setFechaTermino(fechaTermino);
            Membresia nuevaMembresia = membresiaService.save(membresia);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMembresia);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

//

    @Operation(
        summary = "Renovar membresia",
        description = "Renueva la membresia del usuario ingresado"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Membresia renovada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/renovar/{id}")
    public ResponseEntity<Membresia> renovar(@PathVariable String id, HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = Long.parseLong(request.getHeader("X-User-Id"));
        Long idMembresia = Long.parseLong(id);
        Membresia renovada = membresiaService.renovarMembresia(idUsuario, idMembresia);
        return ResponseEntity.ok(renovada);
    }


}

