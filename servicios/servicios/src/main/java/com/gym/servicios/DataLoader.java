package com.gym.servicios;

import com.gym.servicios.model.EstadoServicio;
import com.gym.servicios.repository.EstadoServicioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final EstadoServicioRepository estadoServicioRepo;

    public DataLoader(EstadoServicioRepository estadoServicioRepo) {
        this.estadoServicioRepo = estadoServicioRepo;
    }

    @Override
    public void run(String... args) {
        cargarEstados();
    }

    // ServicioService busca "ACTIVO" al crear un servicio, e "INACTIVO" al desactivar.
    private void cargarEstados() {
        if (estadoServicioRepo.count() == 0) {
            List<String> nombres = List.of("ACTIVO", "INACTIVO");
            for (String nombre : nombres) {
                EstadoServicio e = new EstadoServicio();
                e.setNombre(nombre);
                estadoServicioRepo.save(e);
            }
            System.out.println(">>> DataLoader [servicios]: 2 estados cargados.");
        } else {
            System.out.println(">>> DataLoader [servicios]: estados ya existen.");
        }
    }
}
