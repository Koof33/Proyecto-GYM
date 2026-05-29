package com.gym.membresias.repository;

import com.gym.membresias.model.Incluido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncluidoRepository extends JpaRepository<Incluido, Long> {

    List<Incluido> findByPlanIdPlan(Long idPlan);
}
