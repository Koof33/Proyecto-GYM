package com.gym.reservas;

import com.gym.reservas.model.EstadoReserva;
import com.gym.reservas.repository.EstadoReservaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final EstadoReservaRepository estadoReservaRepo;

    public DataLoader(EstadoReservaRepository estadoReservaRepo) {
        this.estadoReservaRepo = estadoReservaRepo;
    }

    @Override
    public void run(String... args) {
        cargarEstados();
    }

    // ReservaService busca "PENDIENTE" al crear, y "CANCELADA","CONFIRMADA","COMPLETADA"
    // al cambiar estado. Todos deben existir.
    private void cargarEstados() {
        if (estadoReservaRepo.count() == 0) {
            List<String> nombres = List.of("PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA");
            for (String nombre : nombres) {
                EstadoReserva e = new EstadoReserva();
                e.setNombre(nombre);
                estadoReservaRepo.save(e);
            }
            System.out.println(">>> DataLoader [reservas]: 4 estados cargados.");
        } else {
            System.out.println(">>> DataLoader [reservas]: estados ya existen.");
        }
    }
}
