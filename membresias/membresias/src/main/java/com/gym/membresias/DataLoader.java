package com.gym.membresias;

import com.gym.membresias.model.Plan;
import com.gym.membresias.repository.PlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final PlanRepository planRepository;

    public DataLoader(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) {
        cargarPlanes();
    }

    // Se cargan 3 planes de ejemplo para poder crear membresías.
    // POST /membresias requiere un plan con { "plan": { "idPlan": 1 } }
    private void cargarPlanes() {
        if (planRepository.count() == 0) {
            List<Plan> planes = List.of(
                crearPlan("Plan Básico",    "Acceso a sala de musculación",              19990.0, 30),
                crearPlan("Plan Estándar",  "Musculación + clases grupales",             29990.0, 30),
                crearPlan("Plan Premium",   "Acceso total + sesiones con entrenador",    49990.0, 30)
            );
            planRepository.saveAll(planes);
            System.out.println(">>> DataLoader [membresias]: 3 planes cargados.");
        } else {
            System.out.println(">>> DataLoader [membresias]: planes ya existen.");
        }
    }

    private Plan crearPlan(String nombre, String descripcion, Double costo, Integer duracion) {
        Plan p = new Plan();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setCosto(costo);
        p.setDuracion(duracion);
        return p;
    }
}
