package com.gym.clases;

import com.gym.clases.model.EstadoClase;
import com.gym.clases.model.EstadoInscripcion;
import com.gym.clases.repository.EstadoClaseRepository;
import com.gym.clases.repository.EstadoInscripcionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final EstadoClaseRepository estadoClaseRepo;
    private final EstadoInscripcionRepository estadoInscripcionRepo;

    public DataLoader(EstadoClaseRepository estadoClaseRepo,
                      EstadoInscripcionRepository estadoInscripcionRepo) {
        this.estadoClaseRepo = estadoClaseRepo;
        this.estadoInscripcionRepo = estadoInscripcionRepo;
    }

    @Override
    public void run(String... args) {
        cargarEstadosClase();
        cargarEstadosInscripcion();
    }

    // Requerido por ClaseService: busca "ACTIVO" al crear clase,
    // y cualquier otro estado al llamar cambiarEstado()
    private void cargarEstadosClase() {
        if (estadoClaseRepo.count() == 0) {
            List<String> nombres = List.of("ACTIVO", "CANCELADO", "FINALIZADO");
            for (String nombre : nombres) {
                EstadoClase e = new EstadoClase();
                e.setNombre(nombre);
                estadoClaseRepo.save(e);
            }
            System.out.println(">>> DataLoader [clases]: 3 estados de clase cargados.");
        } else {
            System.out.println(">>> DataLoader [clases]: estados de clase ya existen.");
        }
    }

    // Requerido por InscripcionService: busca "INSCRITO", "CANCELADO", "FINALIZADO"
    private void cargarEstadosInscripcion() {
        if (estadoInscripcionRepo.count() == 0) {
            List<String> nombres = List.of("INSCRITO", "CANCELADO", "FINALIZADO");
            for (String nombre : nombres) {
                EstadoInscripcion e = new EstadoInscripcion();
                e.setNombre(nombre);
                estadoInscripcionRepo.save(e);
            }
            System.out.println(">>> DataLoader [clases]: 3 estados de inscripcion cargados.");
        } else {
            System.out.println(">>> DataLoader [clases]: estados de inscripcion ya existen.");
        }
    }
}
