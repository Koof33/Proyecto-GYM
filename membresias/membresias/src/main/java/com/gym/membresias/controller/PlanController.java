package com.gym.membresias.controller;

import com.gym.membresias.model.Plan;
import com.gym.membresias.security.RoleValidator;
import com.gym.membresias.service.PlanService;

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
@RequestMapping("/planes")
public class PlanController {
    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private PlanService planService;

//

    @Operation(
        summary = "Lista los planes",
        description = "Lista todas los planes disponibles"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping
    public ResponseEntity<List<Plan>> listarPlanes() {
        List<Plan> planes = planService.findAll();
        if (planes.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(planes);
    }

//

    @Operation(
        summary = "Obtiene plan",
        description = "Obtiene un plan"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plan obtenido correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtenerPlan(@PathVariable Long id) {
        return planService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//

    @Operation(
        summary = "Crear plan",
        description = "Crea un plan"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plan creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping
    public ResponseEntity<Plan> crearPlan(@RequestBody Plan plan, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            plan.setIdPlan(null);
            Plan nuevoPlan = planService.save(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPlan);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

//

    @Operation(
        summary = "Eliminar plan",
        description = "Elimina un plan"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plan eliminado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPlan(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        planService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

