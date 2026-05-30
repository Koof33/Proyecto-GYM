package com.gym.usuarios.service;

import com.gym.usuarios.model.Usuario;
import com.gym.usuarios.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id){
        return usuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuario no encontrado con ID: "+ id));
    }

    public Usuario save(Usuario usuario) {
        if (usuario.getClave().length() <= 4 || usuario.getClave().length() >= 16) {
            throw new IllegalArgumentException("La clave debe tener entre 5 y 15 caracteres");
        }
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario findByCorreo(String correo){
        return usuarioRepository.findByCorreo(correo).orElseThrow(()-> new RuntimeException("Usuario no encontrado con Correo: "+ correo));
    }

    public Usuario findByRut(String rut){
        return usuarioRepository.findByRut(rut).orElseThrow(()-> new RuntimeException("Usuario no encontrado con Rut: "+ rut));
    }

    public void cambiarClave(Long id, String nueva, String confirmar) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!nueva.equals(confirmar)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        usuario.setClave(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }

}