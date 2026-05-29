package com.gym.autenticacion.service;

import com.gym.autenticacion.dto.LoginRequest;
import com.gym.autenticacion.dto.LoginResponse;
import com.gym.autenticacion.dto.RegisterRequest;
import com.gym.autenticacion.model.Enum.NombreRol;
import com.gym.autenticacion.model.EstadoUsuario;
import com.gym.autenticacion.model.Rol;
import com.gym.autenticacion.model.Usuario;
import com.gym.autenticacion.repository.EstadoUsuarioRepository;
import com.gym.autenticacion.repository.RolRepository;
import com.gym.autenticacion.repository.UsuarioRepository;
import com.gym.autenticacion.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EstadoUsuarioRepository estadoUsuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Usuario user = usuarioRepository.findByCorreo(request.correo())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.clave(), user.getClave())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token);
    }

    public void register(RegisterRequest request) {
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new IllegalArgumentException("Correo ya registrado");
        }

        Rol rolCliente = rolRepository.findByNombreRol(NombreRol.CLIENTE)
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));

        EstadoUsuario estadoUsuarioActivo = estadoUsuarioRepository.findByNombreEstado("ACTIVO")
                .orElseThrow(() -> new RuntimeException("Estado ACTIVO no encontrado"));

        Usuario nuevo = new Usuario();
        nuevo.setRut(request.rut());
        nuevo.setCorreo(request.correo());
        nuevo.setClave(passwordEncoder.encode(request.clave()));
        nuevo.setPnombre(request.pnombre());
        nuevo.setSnombre(request.snombre());
        nuevo.setAppaterno(request.appaterno());
        nuevo.setApmaterno(request.apmaterno());
        nuevo.setRol(rolCliente);
        nuevo.setEstado(estadoUsuarioActivo);

        usuarioRepository.save(nuevo);
    }
}

