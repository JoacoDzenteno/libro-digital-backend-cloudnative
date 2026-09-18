package cl.bernardo.ohiggins.ms_usuarios.service;

import cl.bernardo.ohiggins.ms_usuarios.dto.UsuarioDTO;
import cl.bernardo.ohiggins.ms_usuarios.model.Persona;
import cl.bernardo.ohiggins.ms_usuarios.model.Usuario;
import cl.bernardo.ohiggins.ms_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> obtenerPorId(Long id) {
        return usuarioRepository.findById(id).map(this::toDTO);
    }

    public Optional<UsuarioDTO> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(this::toDTO);
    }

    public Optional<UsuarioDTO> obtenerPorAzureOid(String azureOid) {
        return usuarioRepository.findByAzureOid(azureOid).map(this::toDTO);
    }

    public List<UsuarioDTO> obtenerPorRol(Usuario.Rol rol) {
        return usuarioRepository.findByRol(rol)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> buscar(String nombre, String rut, String email, Integer limite) {
        String n = (nombre != null && !nombre.isBlank()) ? nombre.trim() : null;
        String r = (rut != null && !rut.isBlank()) ? rut.trim() : null;
        String e = (email != null && !email.isBlank()) ? email.trim() : null;

        Pageable pageable = (limite != null && limite > 0)
                ? PageRequest.of(0, limite)
                : Pageable.unpaged();

        return usuarioRepository.buscarCombinado(n, r, e, pageable)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO crear(Usuario usuario) {
        return toDTO(usuarioRepository.save(usuario));
    }

    public Optional<UsuarioDTO> actualizar(Long id, Usuario usuarioActualizado) {
    return usuarioRepository.findById(id).map(u -> {
            u.setEmail(usuarioActualizado.getEmail());
            u.setRol(usuarioActualizado.getRol());

            if (usuarioActualizado.getPersona() != null && u.getPersona() != null) {
                Persona personaExistente = u.getPersona();
                Persona personaNueva = usuarioActualizado.getPersona();

                personaExistente.setNombre(personaNueva.getNombre());
                personaExistente.setApellido(personaNueva.getApellido());
                personaExistente.setTelefono(personaNueva.getTelefono());
                personaExistente.setDireccion(personaNueva.getDireccion());
                // RUT y email de persona NO se modifican intencionalmente
            }

            return toDTO(usuarioRepository.save(u));
        });
    }

    public boolean eliminar(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private UsuarioDTO toDTO(Usuario u) {
        Persona p = u.getPersona();
        return UsuarioDTO.builder()
                .id(u.getId())
                .email(u.getEmail())
                .rol(u.getRol())
                .nombre(p != null ? p.getNombre() : null)
                .apellido(p != null ? p.getApellido() : null)
                .rut(p != null ? p.getRut() : null)
                .emailPersona(p != null ? p.getEmail() : null)
                .telefono(p != null ? p.getTelefono() : null)
                .direccion(p != null ? p.getDireccion() : null)
                .build();
    }

    public Optional<UsuarioDTO> actualizarPerfil(Long id, String nuevoEmail, String nuevaPassword) {
    return usuarioRepository.findById(id).map(u -> {
        if (nuevoEmail != null && !nuevoEmail.isEmpty()) {
            u.setEmail(nuevoEmail);
        }
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            u.setPassword(passwordEncoder.encode(nuevaPassword));
        }
        return toDTO(usuarioRepository.save(u));
    });
    }
}