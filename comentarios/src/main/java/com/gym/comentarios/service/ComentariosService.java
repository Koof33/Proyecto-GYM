package com.gym.comentarios.service;

import com.gym.comentarios.model.Comentarios;
import com.gym.comentarios.repository.ComentariosRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComentariosService {

    private final ComentariosRepository comentariosRepo;

    public Comentarios crear(Long idUsuario, Long idServicio, String comentario, Double calificacion) {
        if (calificacion < 1.0 || calificacion > 10.0) {
            throw new IllegalArgumentException("La calificación debe estar entre 1.0 y 10.0");
        }

        Comentarios comentarios = new Comentarios();
        comentarios.setIdUsuario(idUsuario);
        comentarios.setIdServicio(idServicio);
        comentarios.setFechaComentario(LocalDate.now());
        comentarios.setComentario(comentario);
        comentarios.setCalificacion(calificacion);
        return comentariosRepo.save(comentarios);
    }

    public Double obtenerPromedioCalificacionPorServicio(Long idServicio) {
        List<Comentarios> comentarios = comentariosRepo.findByIdServicio(idServicio);

        if (comentarios.isEmpty()) return 0.0;

        return comentarios.stream()
                .mapToDouble(Comentarios::getCalificacion)
                .average()
                .orElse(0.0);
    }

    public List<Comentarios> obtenerPorServicio(Long idServicio) {
        return comentariosRepo.findByIdServicio(idServicio);
    }

    public List<Comentarios> obtenerPorUsuario(Long idUsuario) {
        return comentariosRepo.findByIdUsuario(idUsuario);
    }

    public Optional<Comentarios> obtenerPorId(Long id) {
        return comentariosRepo.findById(id);
    }


}

