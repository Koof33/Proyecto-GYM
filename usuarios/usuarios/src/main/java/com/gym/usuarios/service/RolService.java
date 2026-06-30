package com.gym.usuarios.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.usuarios.model.Rol;
import com.gym.usuarios.model.Enum.NombreRol;
import com.gym.usuarios.repository.RolRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RolService {
    @Autowired
    private RolRepository rolRepository;

    public List<Rol> findAll(){
        return rolRepository.findAll();
    }

    public Rol findById(Long id){
        return rolRepository.findById(id).orElseThrow(()-> new RuntimeException("Rol no encontrado con ID: "+ id));
    }

    public Rol findByNombre(String nombreRol) {
        return rolRepository.findByNombre(NombreRol.valueOf(nombreRol.toUpperCase()));
    }
}
