package com.gym.comentarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comentarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaComentario;

    @Column(length = 500)
    private String comentario;

    private Long idUsuario;

    private Long idServicio;

    @Column(nullable = false)
    private Double calificacion;
}

