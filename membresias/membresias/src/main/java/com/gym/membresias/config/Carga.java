package com.gym.membresias.config;

import com.gym.membresias.model.Incluido;
import com.gym.membresias.model.Membresia;
import com.gym.membresias.model.Plan;
import com.gym.membresias.repository.IncluidoRepository;
import com.gym.membresias.repository.MembresiaRepository;
import com.gym.membresias.repository.PlanRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class Carga {

    @Bean
    public CommandLineRunner seedData(
            PlanRepository planRepo,
            IncluidoRepository incluidoRepo,
            MembresiaRepository membresiaRepo
    ) {
        return args -> {
            if (planRepo.count() == 0) {
                Plan planBasico = new Plan(null, "Plan Básico", "Acceso general", 19900.0, 30, null);
                Plan planPremium = new Plan(null, "Plan Premium", "Acceso total + clases", 29900.0, 30, null);
                planRepo.saveAll(List.of(planBasico, planPremium));

                incluidoRepo.saveAll(List.of(
                        new Incluido(null, 1L, planBasico),
                        new Incluido(null, 2L, planPremium),
                        new Incluido(null, 3L, planPremium)
                ));

                Membresia activa = new Membresia(null, LocalDate.now(), LocalDate.now().plusDays(30), 2L, planPremium.getCosto(), planPremium);
                Membresia expirada = new Membresia(null, LocalDate.now().minusDays(60), LocalDate.now().minusDays(30), 2L, planBasico.getCosto(), planBasico);
                membresiaRepo.saveAll(List.of(activa, expirada));
            }
        };
    }
}