package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.exception.ServicioNoDisponibleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioClientService {

    private final WebClient.Builder webClientBuilder;

    /**
     * Devuelve el usuario, o null si no existe (respuesta 404).
     * Lanza ServicioNoDisponibleException si ms-usuarios no responde o falla.
     */
    public Map<String, Object> obtenerUsuario(Long id) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://ms-usuarios/api/usuarios/" + id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null; // el usuario no existe: dato inexistente, no una caída
        } catch (Exception e) {
            throw new ServicioNoDisponibleException("ms-usuarios no disponible", e);
        }
    }

    public boolean existeUsuario(Long id) {
        return obtenerUsuario(id) != null;
    }
}