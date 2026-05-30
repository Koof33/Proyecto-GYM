package com.gym.clases.config;

import com.gym.clases.model.EstadoClase;
import com.gym.clases.model.EstadoInscripcion;
import com.gym.clases.repository.EstadoClaseRepository;
import com.gym.clases.repository.EstadoInscripcionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Carga implements CommandLineRunner {

    private final EstadoClaseRepository estadoClaseRepository;
    private final EstadoInscripcionRepository estadoInscripcionRepository;

    @Override
    public void run(String... args) {
        if (estadoClaseRepository.count() == 0) {
            estadoClaseRepository.saveAll(List.of(
                    new EstadoClase(null, "ACTIVO"),
                    new EstadoClase(null, "INACTIVO"),
                    new EstadoClase(null, "FINALIZADO")
            ));
        }

        if (estadoInscripcionRepository.count() == 0) {
            estadoInscripcionRepository.saveAll(List.of(
                    new EstadoInscripcion(null, "INSCRITO"),
                    new EstadoInscripcion(null, "CANCELADO"),
                    new EstadoInscripcion(null, "FINALIZADO")
            ));
        }
    }
}
