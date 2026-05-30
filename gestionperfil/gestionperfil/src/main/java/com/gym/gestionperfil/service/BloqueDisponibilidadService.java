package com.gym.gestionperfil.service;

import com.gym.gestionperfil.model.BloqueDisponibilidad;
import com.gym.gestionperfil.repository.BloqueDisponibilidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BloqueDisponibilidadService {

    private final BloqueDisponibilidadRepository bloqueDisponibilidadRepository;

    public List<BloqueDisponibilidad> listarPorEntrenador(Long idUsuario) {
        return bloqueDisponibilidadRepository.findByIdUsuario(idUsuario);
    }

    public BloqueDisponibilidad agregar(Long idUsuario, BloqueDisponibilidad bloque) {
        bloque.setIdUsuario(idUsuario);
        return bloqueDisponibilidadRepository.save(bloque);
    }

    public Optional<BloqueDisponibilidad> actualizar(Long idUsuario, Long id, BloqueDisponibilidad nuevo) {
        return bloqueDisponibilidadRepository.findById(id)
                .filter(b -> b.getIdUsuario().equals(idUsuario))
                .map(b -> {
                    b.setDia(nuevo.getDia());
                    b.setHoraInicio(nuevo.getHoraInicio());
                    b.setHoraFin(nuevo.getHoraFin());
                    return bloqueDisponibilidadRepository.save(b);
                });
    }

    public boolean eliminar(Long idUsuario, Long id) {
        return bloqueDisponibilidadRepository.findById(id)
                .filter(b -> b.getIdUsuario().equals(idUsuario))
                .map(b -> {
                    bloqueDisponibilidadRepository.delete(b);
                    return true;
                }).orElse(false);
    }


}

