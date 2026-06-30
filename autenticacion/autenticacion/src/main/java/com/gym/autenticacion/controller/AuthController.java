package com.gym.autenticacion.controller;

import com.gym.autenticacion.dto.LoginRequest;
import com.gym.autenticacion.dto.LoginResponse;
import com.gym.autenticacion.dto.RegisterRequest;
import com.gym.autenticacion.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autenticacion")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

//
    @Operation(
        summary = "Login",
        description = "Inicia sesión con un usuario"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario iniciado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

//
    @Operation(
        summary = "Register",
        description = "Registra un usuario"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Registro exitoso");
    }
}
