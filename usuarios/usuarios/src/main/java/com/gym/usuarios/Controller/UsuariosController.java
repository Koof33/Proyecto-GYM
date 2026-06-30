package com.gym.usuarios.Controller;

import com.gym.usuarios.dto.UsuarioPerfil;
import com.gym.usuarios.security.RoleValidator;
import com.gym.usuarios.service.EstadoUsuarioService;
import com.gym.usuarios.service.RolService;
import com.gym.usuarios.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.gym.usuarios.model.Rol;
import com.gym.usuarios.model.Usuario;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    @Autowired
    private EstadoUsuarioService estadoUsuarioService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolService rolService;
    @Autowired
    private RoleValidator roleValidator;

//

    @Operation(
        summary = "Listar usuarios",
        description = "Obtiene una lista con todas los Usuarios registradas en el sistema"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping
    public ResponseEntity<List<Usuario>> mostrarUsuarios(HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Usuario> usuarios = usuarioService.findAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

//
    @Operation(
        summary = "Buscar Usuario Por ID",
        description = "Obtiene un usuario según su ID"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario obtenido correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @GetMapping("/id/{id}")
    public ResponseEntity<?> mostrarUsuario(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Usuario usuario = usuarioService.findById(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

//
    @Operation(
        summary = "Buscar Usuario",
        description = "Obtiene un usuario según su correo"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario obtenido correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @GetMapping("/correo/{correo}")
    public ResponseEntity<Usuario> buscarPorCorreoUsuario(@PathVariable String correo, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Usuario usuario = usuarioService.findByCorreo(correo);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

//
    @Operation(
        summary = "Buscar Usuario",
        description = "Obtiene un usuario según su rut"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario obtenido correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @GetMapping("/rut/{rut}")
    public ResponseEntity<Usuario> buscarPorRut(@PathVariable String rut, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Usuario usuario = usuarioService.findByRut(rut);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

//
    @Operation(
        summary = "Registrar usuario",
        description = "Registra un usuario"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @PostMapping
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            usuario.setIdUsuario(null);
            Usuario nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

//
    @Operation(
        summary = "Actualizar Usuario",
        description = "Actualiza un usuario según su id"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @PutMapping("/id/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuario, @PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Usuario usu = usuarioService.findById(id);
            usu.setRut(usuario.getRut());
            usu.setPnombre(usuario.getPnombre());
            usu.setSnombre(usuario.getSnombre());
            usu.setAppaterno(usuario.getAppaterno());
            usu.setApmaterno(usuario.getApmaterno());
            usu.setCorreo(usuario.getCorreo());
            usu.setClave(usuario.getClave());
            usu.setRol(usuario.getRol());
            usu.setEstado(usuario.getEstado());
            usuarioService.save(usu);
            return ResponseEntity.ok(usu);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

//
@Operation(
        summary = "Activar Usuario",
        description = "Activa un usuario según su rol"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario activado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @PutMapping("/id/{id}/activar")
    public ResponseEntity<Usuario> activarUsuario(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Usuario usu = usuarioService.findById(id);
            usu.setEstado(estadoUsuarioService.findByNombre("ACTIVO"));
            usuarioService.save(usu);
            return ResponseEntity.ok(usu);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

//
    @Operation(
        summary = "Desactivar Usuario",
        description = "Desactiva un usuario según su rol"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario desactivado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @PutMapping("/id/{id}/desactivar")
    public ResponseEntity<Usuario> desactivarUsuario(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Usuario usu = usuarioService.findById(id);
            usu.setEstado(estadoUsuarioService.findByNombre("DESACTIVADO"));
            usuarioService.save(usu);
            return ResponseEntity.ok(usu);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

//
@Operation(
        summary = "Cambiar rol de Usuario",
        description = "Cambia el rol un usuario según su rut"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rol actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @PutMapping("/id/{id}/{nombreRol}")
    public ResponseEntity<Usuario> cambiarRolUsuario(@PathVariable Long id, @PathVariable String nombreRol, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            String nomRol = nombreRol.toUpperCase();
            Usuario usu = usuarioService.findById(id);
            Rol rol = rolService.findByNombre(nomRol);
            usu.setRol(rol);
            usuarioService.save(usu);
            return ResponseEntity.ok(usu);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

//
    @Operation(
        summary = "Eliminar Usuario",
        description = "Elimina un usuario según su id"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

//
    @Operation(
        summary = "Cambia Contraseña Usuario",
        description = "Cambia contraseña de un usuario"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contraseña cambiada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @PutMapping("/{id}/cambiar-password")
    public ResponseEntity<String> cambiarPassword(@PathVariable Long id,
            @RequestBody Map<String, String> datos) {
        String nueva = datos.get("nuevaContrasena");
        String confirmar = datos.get("confirmarContrasena");
        try {
            usuarioService.cambiarClave(id, nueva, confirmar);
            return ResponseEntity.ok("Contrasena actualizada exitosamente.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//
    @Operation(
        summary = "Ver perfil usuario",
        description = "Ver usuario según su id"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Se buscó el perfil correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfil> verPerfil(HttpServletRequest request) {
        Long idUsuario = roleValidator.getUserId(request);
        Usuario usuario = usuarioService.findById(idUsuario);
        UsuarioPerfil dto = new UsuarioPerfil(
                usuario.getRut(),
                usuario.getPnombre(),
                usuario.getSnombre(),
                usuario.getAppaterno(),
                usuario.getApmaterno(),
                usuario.getCorreo()
        );
        return ResponseEntity.ok(dto);
    }

//
    @Operation(
        summary = "Actualizar perfil Usuario",
        description = "Actualiza el perfil de un usuario según su rut"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario activado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
//

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioPerfil> actualizarPerfil(@RequestBody UsuarioPerfil nuevosDatos,
        HttpServletRequest request) {
        Long idUsuario = roleValidator.getUserId(request);
        Usuario usuario = usuarioService.findById(idUsuario);
        usuario.setRut(nuevosDatos.getRut());
        usuario.setPnombre(nuevosDatos.getPnombre());
        usuario.setSnombre(nuevosDatos.getSnombre());
        usuario.setAppaterno(nuevosDatos.getAppaterno());
        usuario.setApmaterno(nuevosDatos.getApmaterno());
        usuario.setCorreo(nuevosDatos.getCorreo());
        usuarioService.save(usuario);
        UsuarioPerfil dto = new UsuarioPerfil(
                usuario.getRut(),
                usuario.getPnombre(),
                usuario.getSnombre(),
                usuario.getAppaterno(),
                usuario.getApmaterno(),
                usuario.getCorreo()
        );
        return ResponseEntity.ok(dto);
    }
}
