package com.gym.servicios.config;


import com.gym.servicios.model.EstadoServicio;
import com.gym.servicios.model.Servicio;
import com.gym.servicios.repository.EstadoServicioRepository;
import com.gym.servicios.repository.ServicioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class Carga {

    @Bean
    public CommandLineRunner initEstados(EstadoServicioRepository estadoRepo, ServicioRepository servicioRepo) {
        return args -> {
            if (estadoRepo.count() == 0) {
                estadoRepo.saveAll(List.of(
                        new EstadoServicio(null, "ACTIVO"),
                        new EstadoServicio(null, "INACTIVO")
                ));
            }
            if (servicioRepo.count() == 0) {
                EstadoServicio activo = estadoRepo.findByNombre("ACTIVO").orElseThrow();

                List<Servicio> servicios = List.of(
                        new Servicio(null, "Masaje relajante", "Masaje de cuerpo completo para aliviar tensiones", activo),
                        new Servicio(null, "Asesoría nutricional", "Consulta personalizada con nutricionista", activo),
                        new Servicio(null, "Entrenamiento personalizado", "Rutina personalizada con un entrenador", activo)
                );

                servicioRepo.saveAll(servicios);
                System.out.println("Servicios precargados correctamente.");
            }
        };
    }
}
