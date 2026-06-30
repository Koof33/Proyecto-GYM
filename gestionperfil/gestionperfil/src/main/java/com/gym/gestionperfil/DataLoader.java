package com.gym.gestionperfil;

import com.gym.gestionperfil.model.BloqueDisponibilidad;
import com.gym.gestionperfil.model.Enum.DiaSemana;
import com.gym.gestionperfil.repository.BloqueDisponibilidadRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final BloqueDisponibilidadRepository disponibilidadRepo;

    @Override
    public void run(String... args) throws Exception {
        if (disponibilidadRepo.count() == 0) {
            List<BloqueDisponibilidad> bloques = List.of(
                    // LUNES
                    new BloqueDisponibilidad(null, DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(9, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.LUNES, LocalTime.of(10, 0), LocalTime.of(11, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.LUNES, LocalTime.of(12, 0), LocalTime.of(13, 30), 5L),

                    // MARTES
                    new BloqueDisponibilidad(null, DiaSemana.MARTES, LocalTime.of(9, 0), LocalTime.of(10, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.MARTES, LocalTime.of(11, 0), LocalTime.of(12, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.MARTES, LocalTime.of(13, 0), LocalTime.of(14, 30), 5L),

                    // MIÉRCOLES
                    new BloqueDisponibilidad(null, DiaSemana.MIÉRCOLES, LocalTime.of(14, 0), LocalTime.of(15, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.MIÉRCOLES, LocalTime.of(16, 0), LocalTime.of(17, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.MIÉRCOLES, LocalTime.of(18, 0), LocalTime.of(19, 30), 5L),

                    // JUEVES
                    new BloqueDisponibilidad(null, DiaSemana.JUEVES, LocalTime.of(7, 30), LocalTime.of(9, 0), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.JUEVES, LocalTime.of(9, 30), LocalTime.of(11, 0), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.JUEVES, LocalTime.of(11, 30), LocalTime.of(13, 0), 5L),

                    // VIERNES
                    new BloqueDisponibilidad(null, DiaSemana.VIERNES, LocalTime.of(13, 0), LocalTime.of(14, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.VIERNES, LocalTime.of(15, 0), LocalTime.of(16, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.VIERNES, LocalTime.of(17, 0), LocalTime.of(18, 30), 5L),

                    // SABADO
                    new BloqueDisponibilidad(null, DiaSemana.SABADO, LocalTime.of(13, 0), LocalTime.of(14, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.SABADO, LocalTime.of(15, 0), LocalTime.of(16, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.SABADO, LocalTime.of(17, 0), LocalTime.of(18, 30), 5L),

                    // DOMINGO
                    new BloqueDisponibilidad(null, DiaSemana.DOMINGO, LocalTime.of(13, 0), LocalTime.of(14, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.DOMINGO, LocalTime.of(15, 0), LocalTime.of(16, 30), 5L),
                    new BloqueDisponibilidad(null, DiaSemana.DOMINGO, LocalTime.of(17, 0), LocalTime.of(18, 30), 5L)
            );

            disponibilidadRepo.saveAll(bloques);
            System.out.println("Bloques de disponibilidad precargados correctamente.");
        }
    }
}
