package com.gym.usuarios;

import com.gym.usuarios.model.EstadoUsuario;
import com.gym.usuarios.model.Rol;
import com.gym.usuarios.model.Enum.NombreRol;
import com.gym.usuarios.repository.EstadoUsuarioRepository;
import com.gym.usuarios.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final EstadoUsuarioRepository estadoUsuarioRepository;

    public DataLoader(RolRepository rolRepository,
                      EstadoUsuarioRepository estadoUsuarioRepository) {
        this.rolRepository = rolRepository;
        this.estadoUsuarioRepository = estadoUsuarioRepository;
    }

    @Override
    public void run(String... args) {
        cargarRoles();
        cargarEstados();
    }

    private void cargarRoles() {
        for (NombreRol nombre : NombreRol.values()) {
        try {
            if (rolRepository.findByNombre(nombre) == null) {
                rolRepository.save(crearRol(nombre));
            }
        } catch (Exception e) {
            System.out.println(">>> DataLoader [usuarios]: rol " + nombre + " ya existe, se omite.");
        }
    }
    System.out.println(">>> DataLoader [usuarios]: roles verificados.");
}

    private void cargarEstados() {
        List<String> estados = List.of("ACTIVO", "DESACTIVADO", "INACTIVO", "SUSPENDIDO");
        for (String nombre : estados) {
            if (estadoUsuarioRepository.findByNombre(nombre) == null) {
                estadoUsuarioRepository.save(crearEstado(nombre));
            }
        }
        System.out.println(">>> DataLoader [usuarios]: estados verificados.");
    }

    private Rol crearRol(NombreRol nombreRol) {
        Rol rol = new Rol();
        rol.setNombreRol(nombreRol);
        return rol;
    }

    private EstadoUsuario crearEstado(String nombre) {
        EstadoUsuario estado = new EstadoUsuario();
        estado.setNombreEstado(nombre);
        return estado;
    }
}