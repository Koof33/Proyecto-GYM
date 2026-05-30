package com.gym.membresias.service;

import com.gym.membresias.model.Incluido;

import com.gym.membresias.repository.IncluidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncluidoService {

    @Autowired
    private IncluidoRepository incluidoRepository;

    public List<Incluido> listarPorPlan(Long idPlan) {
        return incluidoRepository.findByPlanIdPlan(idPlan);
    }

    public Incluido save(Incluido incluido) {
        return incluidoRepository.save(incluido);
    }

    public void deleteById(Long id) {
        incluidoRepository.deleteById(id);
    }
}
