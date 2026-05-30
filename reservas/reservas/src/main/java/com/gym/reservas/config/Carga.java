package com.gym.reservas.config;

import com.gym.reservas.model.EstadoReserva;
import com.gym.reservas.model.EstadoReservaHistorial;
import com.gym.reservas.model.Reserva;
import com.gym.reservas.repository.EstadoReservaHistorialRepository;
import com.gym.reservas.repository.EstadoReservaRepository;
import com.gym.reservas.repository.ReservaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class Carga {

    @Bean
    public CommandLineRunner precargarDatos(
            EstadoReservaRepository estadoRepo,
            ReservaRepository reservaRepo,
            EstadoReservaHistorialRepository historialRepo) {
        return args -> {

            // Precargar estados si no existen
            EstadoReserva pendiente = estadoRepo.findByNombre("PENDIENTE")
                    .orElseGet(() -> estadoRepo.save(new EstadoReserva(null, "PENDIENTE")));

            EstadoReserva confirmada = estadoRepo.findByNombre("CONFIRMADA")
                    .orElseGet(() -> estadoRepo.save(new EstadoReserva(null, "CONFIRMADA")));

            EstadoReserva cancelada = estadoRepo.findByNombre("CANCELADA")
                    .orElseGet(() -> estadoRepo.save(new EstadoReserva(null, "CANCELADA")));

            EstadoReserva completada = estadoRepo.findByNombre("COMPLETADA")
                    .orElseGet(() -> estadoRepo.save(new EstadoReserva(null, "COMPLETADA")));

            // Verificar si ya hay reservas
            if (reservaRepo.count() == 0) {

                // Reserva 1: Pendiente
                Reserva r1 = new Reserva(null, LocalDate.now().plusDays(2), LocalDateTime.now(),
                        "Reserva de prueba", 1L, 1L, 3L, pendiente);
                r1 = reservaRepo.save(r1);
                historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now(), "Creada como prueba", pendiente, r1));

                // Reserva 2: Completada
                Reserva r2 = new Reserva(null, LocalDate.now().minusDays(3), LocalDateTime.now().minusDays(5),
                        "Membresía previa", 2L, 2L, 3L, completada);
                r2 = reservaRepo.save(r2);
                historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now().minusDays(5), "Reservado", pendiente, r2));
                historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now().minusDays(4), "Confirmado", confirmada, r2));
                historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now().minusDays(3), "Finalizado", completada, r2));

                // Reserva 3: Cancelada
                Reserva r3 = new Reserva(null, LocalDate.now().plusDays(1), LocalDateTime.now().minusDays(1),
                        "Cancelada por el usuario", 1L, 1L, 3L, cancelada);
                r3 = reservaRepo.save(r3);
                historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now().minusDays(1), "Creada", pendiente, r3));
                historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now(), "Cancelada por usuario", cancelada, r3));

                System.out.println("Reservas de prueba cargadas.");
            }
        };
    }
}