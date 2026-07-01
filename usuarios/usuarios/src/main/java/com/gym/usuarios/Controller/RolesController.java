package com.gym.usuarios.Controller;

import com.gym.usuarios.model.Rol;
import com.gym.usuarios.security.RoleValidator;
import com.gym.usuarios.service.RolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@RestController
@RequestMapping("/api/roles")
public class RolesController {
    @Autowired
    private RoleValidator roleValidator;
    @Autowired
    private RolService rolService;

//

    @Operation(
        summary = "Mostrar roles",
        description = "Mostrar todos los roles"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles mostrados correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping
    public ResponseEntity<List<Rol>> mostrarRoles(HttpServletRequest request){
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Rol> roles = rolService.findAll();
        if (roles.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles);
    }

//

    @Operation(
        summary = "Mostrar rol",
        description = "Muestra el rol de un usuario según su ID"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Roles mostrados correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/id/{id}")
    public ResponseEntity<?> mostrarRol(@PathVariable Long id, HttpServletRequest request){
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            Rol rol = rolService.findById(id);
            return ResponseEntity.ok(rol);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

//

    @Operation(
        summary = "Buscar rol",
        description = "Buscar por nombre de rol"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Roles mostrados correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/nombre/{nombreRol}")
    public ResponseEntity<?> buscarRolNombre(@PathVariable String nombreRol, HttpServletRequest request){
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            String nomRol = nombreRol.toUpperCase();
            Rol rol = rolService.findByNombre(nomRol);
            return ResponseEntity.ok(rol);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}

