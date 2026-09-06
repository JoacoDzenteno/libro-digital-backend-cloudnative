package cl.bernardo.ohiggins.ms_libro_digital.service;

import cl.bernardo.ohiggins.ms_libro_digital.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_libro_digital.model.HojaVida;
import cl.bernardo.ohiggins.ms_libro_digital.repository.HojaVidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HojaVidaService {

    private final HojaVidaRepository hojaVidaRepository;
    private final NotificacionPublisher notificacionPublisher;

    public List<HojaVida> obtenerTodas() {
        return hojaVidaRepository.findAll();
    }

    public Optional<HojaVida> obtenerPorId(Long id) {
        return hojaVidaRepository.findById(id);
    }

    public List<HojaVida> obtenerPorEstudiante(Long idEstudiante) {
        return hojaVidaRepository.findByIdEstudiante(idEstudiante);
    }

    public List<HojaVida> obtenerPorEstudianteYTipo(Long idEstudiante, HojaVida.TipoAnotacion tipo) {
        return hojaVidaRepository.findByIdEstudianteAndTipo(idEstudiante, tipo);
    }

    public HojaVida registrar(HojaVida hojaVida) {
        if (hojaVida.getFecha() == null) {
            hojaVida.setFecha(LocalDate.now());
        }
        HojaVida guardada = hojaVidaRepository.save(hojaVida);

        notificacionPublisher.publicarNotificacion(NotificacionEvent.builder()
                .idEstudiante(guardada.getIdEstudiante())
                .asunto("Nueva anotación en hoja de vida")
                .contenido("Se registró una anotación " + guardada.getTipo() + ": " + guardada.getDescripcion())
                .tipoEvento("ANOTACION")
                .build());

        return guardada;
    }

    public boolean eliminar(Long id) {
        if (hojaVidaRepository.existsById(id)) {
            hojaVidaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}