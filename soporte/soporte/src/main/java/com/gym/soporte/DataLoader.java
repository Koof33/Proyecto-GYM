package com.gym.soporte;

import com.gym.soporte.model.EstadoTicket;
import com.gym.soporte.model.Motivo;
import com.gym.soporte.repository.EstadoTicketRepository;
import com.gym.soporte.repository.MotivoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final EstadoTicketRepository estadoTicketRepo;
    private final MotivoRepository motivoRepo;

    public DataLoader(EstadoTicketRepository estadoTicketRepo,
                      MotivoRepository motivoRepo) {
        this.estadoTicketRepo = estadoTicketRepo;
        this.motivoRepo = motivoRepo;
    }

    @Override
    public void run(String... args) {
        cargarEstadosTicket();
        cargarMotivos();
    }

    // TicketService busca "PENDIENTE" al crear ticket.
    // cambiarEstado() acepta cualquier estado que exista en la tabla.
    private void cargarEstadosTicket() {
        if (estadoTicketRepo.count() == 0) {
            List<String> nombres = List.of("PENDIENTE", "EN_PROCESO", "RESUELTO", "CERRADO");
            for (String nombre : nombres) {
                EstadoTicket e = new EstadoTicket();
                e.setNombre(nombre);
                estadoTicketRepo.save(e);
            }
            System.out.println(">>> DataLoader [soporte]: 4 estados de ticket cargados.");
        } else {
            System.out.println(">>> DataLoader [soporte]: estados ya existen.");
        }
    }

    // Al crear un ticket se debe pasar motivo.id en el body.
    // POST /soporte/crear → { "descripcion": "...", "motivo": { "id": 1 } }
    private void cargarMotivos() {
        if (motivoRepo.count() == 0) {
            List<String> descripciones = List.of(
                "Problema con membresía",
                "Error en reserva",
                "Problema de acceso",
                "Consulta de facturación",
                "Otro"
            );
            for (String desc : descripciones) {
                Motivo m = new Motivo();
                m.setDescripcion(desc);
                motivoRepo.save(m);
            }
            System.out.println(">>> DataLoader [soporte]: 5 motivos cargados.");
        } else {
            System.out.println(">>> DataLoader [soporte]: motivos ya existen.");
        }
    }
}
