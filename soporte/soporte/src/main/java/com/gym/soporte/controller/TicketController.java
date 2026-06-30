package com.gym.soporte.controller;

import com.gym.soporte.model.Ticket;
import com.gym.soporte.security.RoleValidator;
import com.gym.soporte.service.TicketService;

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
@RequestMapping("/soporte")
public class TicketController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private RoleValidator roleValidator;

//

    @Operation(
        summary = "Crear ticket",
        description = "Crea un nuevo ticket"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ticket creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PostMapping("/crear")
    public ResponseEntity<Ticket> crearTicket(@RequestBody Ticket ticket, HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = roleValidator.getUserId(request);
        try{
            Ticket nuevoTicket = ticketService.crearTicket(ticket.getDescripcion(), idUsuario, ticket.getMotivo().getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTicket);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

//

    @Operation(
        summary = "Listar tickets",
        description = "Lista mis tickets"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tickets listados correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/mis-tickets")
    public ResponseEntity<List<Ticket>> misTickets(HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = roleValidator.getUserId(request);
        List<Ticket> tickets = ticketService.listarTicketsPorUsuario(idUsuario);
        if (tickets.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

//

    @Operation(
        summary = "Listar tickets",
        description = "Lista todos tickets"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tickets listados correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping("/todos")
    public ResponseEntity<List<Ticket>> listarTodos(HttpServletRequest request) {
        roleValidator.requireRole(request, "SOPORTE");
        List<Ticket> tickets = ticketService.listarTodos();
        if (tickets.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

//

    @Operation(
        summary = "Cambiar estado",
        description = "Cambia el estado del ticket"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estado cambiado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}/estado/{estado}")
    public ResponseEntity<Ticket> cambiarEstado(@PathVariable Long id, @PathVariable String estado, HttpServletRequest request) {
        roleValidator.requireRole(request, "SOPORTE");
        try{
            String estado1 = estado.toUpperCase();
            return ResponseEntity.ok(ticketService.cambiarEstado(id, estado1));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

//

    @Operation(
        summary = "Asignar ticket",
        description = "Asigna ticket según id"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tickets asignado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/{id}/asignar")
    public ResponseEntity<Ticket> asignar(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "SOPORTE");
        Long idSoporte = roleValidator.getUserId(request);
        try{
            return ResponseEntity.ok(ticketService.asignarSoporte(id, idSoporte));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}