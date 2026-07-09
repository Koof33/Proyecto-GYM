package com.gym.autenticacion;

import com.gym.autenticacion.model.EstadoUsuario;
import com.gym.autenticacion.model.Enum.NombreRol;
import com.gym.autenticacion.model.Rol;
import com.gym.autenticacion.model.Usuario;
import com.gym.autenticacion.repository.EstadoUsuarioRepository;
import com.gym.autenticacion.repository.RolRepository;
import com.gym.autenticacion.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final PasswordEncoder encoder;

    @Bean
    public CommandLineRunner seedData(
            RolRepository rolRepo,
            EstadoUsuarioRepository estadoUsuarioRepo,
            UsuarioRepository usuarioRepo
    ) {
        return args -> {
            // Roles
            for (NombreRol nombreRol : NombreRol.values()) {
                rolRepo.findByNombreRol(nombreRol)
                        .or(() -> Optional.of(rolRepo.save(new Rol(null, nombreRol, null))));
            }

            // Estados
            List<String> estados = List.of("ACTIVO", "INACTIVO", "SUSPENDIDO");
            for (String estado : estados) {
                estadoUsuarioRepo.findByNombreEstado(estado)
                        .or(() -> Optional.of(estadoUsuarioRepo.save(new EstadoUsuario(null, estado, null))));
            }

            // Usuarios - protegidos por correo Y por try-catch para evitar
            // duplicados de RUT si el usuario ya existe desde otro microservicio
            insertarUsuario(usuarioRepo, rolRepo, estadoUsuarioRepo, encoder,
                "11111111-1", "Admin",      "Sys",     "Admin",     "One",
                "admin@gimnasio.com",       "admin123",       NombreRol.ADMINISTRADOR);

            insertarUsuario(usuarioRepo, rolRepo, estadoUsuarioRepo, encoder,
                "22222222-2", "Cliente",   "Ejemplo", "Perez",     "Lopez",
                "cliente@gimnasio.com",     "cliente123",     NombreRol.CLIENTE);

            insertarUsuario(usuarioRepo, rolRepo, estadoUsuarioRepo, encoder,
                "33333333-3", "Soporte",   "Ejemplo", "Ramirez",   "Mena",
                "soporte@gimnasio.com",     "soporte123",     NombreRol.SOPORTE);

            insertarUsuario(usuarioRepo, rolRepo, estadoUsuarioRepo, encoder,
                "44444444-4", "Entrenador","Ejemplo", "Diaz",      "Vega",
                "entrenador@gimnasio.com",  "entrenador123",  NombreRol.ENTRENADOR);
        };
    }

    private void insertarUsuario(UsuarioRepository usuarioRepo,
                                 RolRepository rolRepo,
                                 EstadoUsuarioRepository estadoUsuarioRepo,
                                 PasswordEncoder encoder,
                                 String rut, String pnombre, String snombre,
                                 String appaterno, String apmaterno,
                                 String correo, String clave, NombreRol rol) {
        if (usuarioRepo.findByCorreo(correo).isPresent()) {
            System.out.println(">>> Carga: usuario " + correo + " ya existe, se omite.");
            return;
        }
        try {
            usuarioRepo.save(new Usuario(null, rut, pnombre, snombre, appaterno, apmaterno,
                    correo, encoder.encode(clave),
                    rolRepo.findByNombreRol(rol).get(),
                    estadoUsuarioRepo.findByNombreEstado("ACTIVO").get()));
            System.out.println(">>> Carga: usuario " + correo + " creado.");
        } catch (Exception e) {
            System.out.println(">>> Carga: usuario " + correo + " omitido (conflicto: " + e.getMessage().split("\n")[0] + ").");
        }
    }
}