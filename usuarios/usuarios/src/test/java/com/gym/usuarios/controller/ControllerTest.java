package com.gym.usuarios.controller;
 
import com.gym.usuarios.Controller.UsuariosController;
import com.gym.usuarios.dto.UsuarioPerfil;
import com.gym.usuarios.model.EstadoUsuario;
import com.gym.usuarios.model.Rol;
import com.gym.usuarios.model.Usuario;
import com.gym.usuarios.model.Enum.NombreRol;
import com.gym.usuarios.security.RoleValidator;
import com.gym.usuarios.service.EstadoUsuarioService;
import com.gym.usuarios.service.RolService;
import com.gym.usuarios.service.UsuarioService;
 
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.List;
import java.util.Map;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
 

//  Pruebas unitarias del UsuariosController usando Mockito.

@ExtendWith(MockitoExtension.class)
class ControllerTest {
 
    @Mock
    private UsuarioService usuarioService;
 
    @Mock
    private RolService rolService;
 
    @Mock
    private EstadoUsuarioService estadoUsuarioService;
 
    @Mock
    private RoleValidator roleValidator;
 
    @Mock
    private HttpServletRequest request;
 
    @InjectMocks
    private UsuariosController usuariosController;
 
    private Usuario usuario;
    private Rol rolAdmin;
    private EstadoUsuario estadoActivo;
 
    @BeforeEach
    void setUp() {
        rolAdmin = new Rol(1L, NombreRol.ADMINISTRADOR, null);
        estadoActivo = new EstadoUsuario(1L, "ACTIVO", null);
 
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("12345678-9");
        usuario.setPnombre("Juan");
        usuario.setSnombre("Andres");
        usuario.setAppaterno("Perez");
        usuario.setApmaterno("Soto");
        usuario.setCorreo("juan.perez@gmail.com");
        usuario.setClave("Clave123");
        usuario.setRol(rolAdmin);
        usuario.setEstado(estadoActivo);
    }
 
    // ───────────────────────── mostrarUsuarios ─────────────────────────
 
    @Test
    void mostrarUsuarios_devuelveListaConDatos_retorna200() {
        when(usuarioService.findAll()).thenReturn(List.of(usuario));
 
        ResponseEntity<List<Usuario>> response = usuariosController.mostrarUsuarios(request);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(roleValidator).requireRole(request, "ADMINISTRADOR");
        verify(usuarioService).findAll();
    }
 
    @Test
    void mostrarUsuarios_listaVacia_retorna204() {
        when(usuarioService.findAll()).thenReturn(List.of());
 
        ResponseEntity<List<Usuario>> response = usuariosController.mostrarUsuarios(request);
 
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
 
    @Test
    void mostrarUsuarios_sinRolAdministrador_lanzaForbidden() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a este recurso"))
                .when(roleValidator).requireRole(request, "ADMINISTRADOR");
 
        assertThrows(ResponseStatusException.class,
                () -> usuariosController.mostrarUsuarios(request));
 
        verify(usuarioService, never()).findAll();
    }
 
    // ───────────────────────── mostrarUsuario (por id) ─────────────────────────
 
    @Test
    void mostrarUsuario_existente_retorna200() {
        when(usuarioService.findById(1L)).thenReturn(usuario);
 
        ResponseEntity<?> response = usuariosController.mostrarUsuario(1L, request);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario, response.getBody());
    }
 
    @Test
    void mostrarUsuario_inexistente_retorna404() {
        when(usuarioService.findById(99L)).thenThrow(new RuntimeException("Usuario no encontrado con ID: 99"));
 
        ResponseEntity<?> response = usuariosController.mostrarUsuario(99L, request);
 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
 
    // ───────────────────────── buscarPorCorreoUsuario ─────────────────────────
 
    @Test
    void buscarPorCorreoUsuario_existente_retorna200() {
        when(usuarioService.findByCorreo("juan.perez@gmail.com")).thenReturn(usuario);
 
        ResponseEntity<Usuario> response = usuariosController.buscarPorCorreoUsuario("juan.perez@gmail.com", request);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("juan.perez@gmail.com", response.getBody().getCorreo());
    }
 
    @Test
    void buscarPorCorreoUsuario_inexistente_retorna404() {
        when(usuarioService.findByCorreo("noexiste@gmail.com"))
                .thenThrow(new RuntimeException("Usuario no encontrado con Correo: noexiste@gmail.com"));
 
        ResponseEntity<Usuario> response = usuariosController.buscarPorCorreoUsuario("noexiste@gmail.com", request);
 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
 
    // ───────────────────────── buscarPorRut ─────────────────────────
 
    @Test
    void buscarPorRut_existente_retorna200() {
        when(usuarioService.findByRut("12345678-9")).thenReturn(usuario);
 
        ResponseEntity<Usuario> response = usuariosController.buscarPorRut("12345678-9", request);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("12345678-9", response.getBody().getRut());
    }
 
    @Test
    void buscarPorRut_inexistente_retorna404() {
        when(usuarioService.findByRut("00000000-0"))
                .thenThrow(new RuntimeException("Usuario no encontrado con Rut: 00000000-0"));
 
        ResponseEntity<Usuario> response = usuariosController.buscarPorRut("00000000-0", request);
 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
 
    // ───────────────────────── registrarUsuario ─────────────────────────
 
    @Test
    void registrarUsuario_datosValidos_retorna201() {
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);
 
        ResponseEntity<Usuario> response = usuariosController.registrarUsuario(usuario, request);
 
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(usuarioService).save(usuario);
    }
 
    @Test
    void registrarUsuario_idForzadoANull_antesDeGuardar() {
        usuario.setIdUsuario(50L); // el cliente intenta mandar un id
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);
 
        usuariosController.registrarUsuario(usuario, request);
 
        // El controller debe forzar idUsuario a null antes de llamar a save()
        assertNull(usuario.getIdUsuario());
    }
 
    @Test
    void registrarUsuario_claveInvalida_retorna400() {
        when(usuarioService.save(any(Usuario.class)))
                .thenThrow(new IllegalArgumentException("La clave debe tener entre 5 y 15 caracteres"));
 
        ResponseEntity<Usuario> response = usuariosController.registrarUsuario(usuario, request);
 
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
 
    // ───────────────────────── actualizarUsuario ─────────────────────────
 
    @Test
    void actualizarUsuario_existente_retorna200ConDatosActualizados() {
        Usuario datosNuevos = new Usuario();
        datosNuevos.setRut("11111111-1");
        datosNuevos.setPnombre("Pedro");
        datosNuevos.setSnombre("Luis");
        datosNuevos.setAppaterno("Gomez");
        datosNuevos.setApmaterno("Diaz");
        datosNuevos.setCorreo("pedro@gmail.com");
        datosNuevos.setClave("NuevaClave1");
        datosNuevos.setRol(rolAdmin);
        datosNuevos.setEstado(estadoActivo);
 
        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);
 
        ResponseEntity<Usuario> response = usuariosController.actualizarUsuario(datosNuevos, 1L, request);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pedro", response.getBody().getPnombre());
        assertEquals("pedro@gmail.com", response.getBody().getCorreo());
        verify(usuarioService).save(usuario);
    }
 
    @Test
    void actualizarUsuario_inexistente_retorna404() {
        when(usuarioService.findById(99L)).thenThrow(new RuntimeException("Usuario no encontrado con ID: 99"));
 
        ResponseEntity<Usuario> response = usuariosController.actualizarUsuario(usuario, 99L, request);
 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
 
    // ───────────────────────── activarUsuario ─────────────────────────
 
    @Test
    void activarUsuario_existente_cambiaEstadoYRetorna200() {
        EstadoUsuario activo = new EstadoUsuario(1L, "ACTIVO", null);
        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(estadoUsuarioService.findByNombre("ACTIVO")).thenReturn(activo);
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);
 
        ResponseEntity<Usuario> response = usuariosController.activarUsuario(1L, request);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ACTIVO", response.getBody().getEstado().getNombreEstado());
    }
 
    @Test
    void activarUsuario_inexistente_retorna404() {
        when(usuarioService.findById(99L)).thenThrow(new RuntimeException("Usuario no encontrado con ID: 99"));
 
        ResponseEntity<Usuario> response = usuariosController.activarUsuario(99L, request);
 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
 
    // ───────────────────────── desactivarUsuario ─────────────────────────
 
    @Test
    void desactivarUsuario_existente_cambiaEstadoYRetorna200() {
        EstadoUsuario desactivado = new EstadoUsuario(2L, "DESACTIVADO", null);
        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(estadoUsuarioService.findByNombre("DESACTIVADO")).thenReturn(desactivado);
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);
 
        ResponseEntity<Usuario> response = usuariosController.desactivarUsuario(1L, request);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("DESACTIVADO", response.getBody().getEstado().getNombreEstado());
    }
 
    // ───────────────────────── cambiarRolUsuario ─────────────────────────
 
    @Test
    void cambiarRolUsuario_existente_retorna200ConNuevoRol() {
        Rol rolCliente = new Rol(2L, NombreRol.CLIENTE, null);
        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(rolService.findByNombre("CLIENTE")).thenReturn(rolCliente);
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);
 
        ResponseEntity<Usuario> response = usuariosController.cambiarRolUsuario(1L, "cliente", request);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(NombreRol.CLIENTE, response.getBody().getRol().getNombreRol());
        // Verifica que el nombre se normalizó a mayúsculas antes de buscar
        verify(rolService).findByNombre("CLIENTE");
    }
 
    @Test
    void cambiarRolUsuario_rolInexistente_retorna404() {
        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(rolService.findByNombre("INVALIDO"))
                .thenThrow(new IllegalArgumentException("No existe el rol"));
 
        ResponseEntity<Usuario> response = usuariosController.cambiarRolUsuario(1L, "invalido", request);
 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
 
    // ───────────────────────── eliminarUsuario ─────────────────────────
 
    @Test
    void eliminarUsuario_existente_retorna204() {
        doNothing().when(usuarioService).deleteById(1L);
 
        ResponseEntity<?> response = usuariosController.eliminarUsuario(1L, request);
 
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(usuarioService).deleteById(1L);
    }
 
    @Test
    void eliminarUsuario_inexistente_retorna404() {
        doThrow(new RuntimeException("No existe")).when(usuarioService).deleteById(99L);
 
        ResponseEntity<?> response = usuariosController.eliminarUsuario(99L, request);
 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
 
    // ───────────────────────── cambiarPassword ─────────────────────────
 
    @Test
    void cambiarPassword_datosValidos_retorna200() {
        Map<String, String> datos = Map.of(
                "nuevaContrasena", "NuevaClave123",
                "confirmarContrasena", "NuevaClave123"
        );
        doNothing().when(usuarioService).cambiarClave(1L, "NuevaClave123", "NuevaClave123");
 
        ResponseEntity<String> response = usuariosController.cambiarPassword(1L, datos);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Contrasena actualizada exitosamente.", response.getBody());
    }
 
    @Test
    void cambiarPassword_usuarioInexistente_retorna404() {
        Map<String, String> datos = Map.of(
                "nuevaContrasena", "NuevaClave123",
                "confirmarContrasena", "NuevaClave123"
        );
        doThrow(new EntityNotFoundException("Usuario no encontrado"))
                .when(usuarioService).cambiarClave(99L, "NuevaClave123", "NuevaClave123");
 
        ResponseEntity<String> response = usuariosController.cambiarPassword(99L, datos);
 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado.", response.getBody());
    }
 
    @Test
    void cambiarPassword_contrasenasNoCoinciden_retorna400() {
        Map<String, String> datos = Map.of(
                "nuevaContrasena", "ClaveA123",
                "confirmarContrasena", "ClaveB456"
        );
        doThrow(new IllegalArgumentException("Las contraseñas no coinciden"))
                .when(usuarioService).cambiarClave(1L, "ClaveA123", "ClaveB456");
 
        ResponseEntity<String> response = usuariosController.cambiarPassword(1L, datos);
 
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Las contraseñas no coinciden", response.getBody());
    }
 
    // ───────────────────────── verPerfil ─────────────────────────
 
    @Test
    void verPerfil_devuelveDatosDelUsuarioAutenticado() {
        when(roleValidator.getUserId(request)).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(usuario);
 
        ResponseEntity<UsuarioPerfil> response = usuariosController.verPerfil(request);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("juan.perez@gmail.com", response.getBody().getCorreo());
        assertEquals("Juan", response.getBody().getPnombre());
    }
 
    // ───────────────────────── actualizarPerfil ─────────────────────────
 
    @Test
    void actualizarPerfil_actualizaDatosPropiosYRetorna200() {
        UsuarioPerfil nuevosDatos = new UsuarioPerfil(
                "99999999-9", "Carla", "Maria", "Rojas", "Vidal", "carla@gmail.com"
        );
 
        when(roleValidator.getUserId(request)).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);
 
        ResponseEntity<UsuarioPerfil> response = usuariosController.actualizarPerfil(nuevosDatos, request);
 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("carla@gmail.com", response.getBody().getCorreo());
        assertEquals("Carla", response.getBody().getPnombre());
        verify(usuarioService).save(usuario);
    }
}