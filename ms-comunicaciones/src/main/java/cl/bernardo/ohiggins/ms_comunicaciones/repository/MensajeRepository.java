package cl.bernardo.ohiggins.ms_comunicaciones.repository;

import cl.bernardo.ohiggins.ms_comunicaciones.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByIdDestinatario(Long idDestinatario);
    List<Mensaje> findByIdRemitente(Long idRemitente);
    List<Mensaje> findByIdDestinatarioAndLeido(Long idDestinatario, Boolean leido);
    List<Mensaje> findByIdDestinatarioAndTipo(Long idDestinatario, Mensaje.TipoMensaje tipo);
}