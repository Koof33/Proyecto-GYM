package com.gym.usuarios.service;

import com.gym.usuarios.model.EstadoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.usuarios.repository.EstadoUsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EstadoUsuarioService {
    @Autowired
    private EstadoUsuarioRepository estadoUsuarioRepository;

    public EstadoUsuario findById(Long id){
        return estadoUsuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("Estado no encontrado con ID: "+ id));
    }

    public EstadoUsuario findByNombre(String nombreEstado){
        return estadoUsuarioRepository.findByNombre(nombreEstado);
    }
}