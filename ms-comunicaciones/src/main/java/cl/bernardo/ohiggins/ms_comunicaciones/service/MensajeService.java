package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.model.Mensaje;
import cl.bernardo.ohiggins.ms_comunicaciones.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioClientService usuarioClientService; 

    public List<Mensaje> obtenerTodos() {
        return mensajeRepository.findAll();
    }

    public Optional<Mensaje> obtenerPorId(Long id) {
        return mensajeRepository.findById(id);
    }

    public List<Mensaje> obtenerPorDestinatario(Long idDestinatario) {
        return mensajeRepository.findByIdDestinatario(idDestinatario);
    }

    public List<Mensaje> obtenerNoLeidos(Long idDestinatario) {
        return mensajeRepository.findByIdDestinatarioAndLeido(idDestinatario, false);
    }

    public List<Mensaje> obtenerPorTipo(Long idDestinatario, Mensaje.TipoMensaje tipo) {
        return mensajeRepository.findByIdDestinatarioAndTipo(idDestinatario, tipo);
    }

    public Optional<Mensaje> enviar(Mensaje mensaje) {
        // Verificar via WebClient que el destinatario existe en ms-usuarios
        if (!usuarioClientService.existeUsuario(mensaje.getIdDestinatario())) {
            return Optional.empty();
        }
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setLeido(false);
        return Optional.of(mensajeRepository.save(mensaje));
    }

    public Optional<Mensaje> marcarComoLeido(Long id) {
        return mensajeRepository.findById(id).map(m -> {
            m.setLeido(true);
            return mensajeRepository.save(m);
        });
    }

    public boolean eliminar(Long id) {
        if (mensajeRepository.existsById(id)) {
            mensajeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}