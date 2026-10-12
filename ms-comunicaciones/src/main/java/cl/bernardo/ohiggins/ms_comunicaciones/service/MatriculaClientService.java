package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.exception.ServicioNoDisponibleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatriculaClientService {

    private final WebClient.Builder webClientBuilder;

    /**
     * Devuelve el id del apoderado del estudiante, o null si no tiene
     * (sin matrícula, matrícula sin apoderado o respuesta 404).
     * Lanza ServicioNoDisponibleException si ms-matricula no responde o falla.
     */
    public Long obtenerIdApoderado(Long idEstudiante) {
        try {
            List<Map<String, Object>> matriculas = webClientBuilder.build()
                    .get()
                    .uri("http://ms-matricula/api/matricula/estudiante/" + idEstudiante)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (matriculas == null || matriculas.isEmpty()) {
                return null;
            }

            Object idApoderado = matriculas.get(0).get("idApoderado");
            return idApoderado != null ? Long.valueOf(idApoderado.toString()) : null;
        } catch (WebClientResponseException.NotFound e) {
            return null; // el estudiante no tiene matrícula: dato inexistente, no una caída
        } catch (Exception e) {
            throw new ServicioNoDisponibleException("ms-matricula no disponible", e);
        }
    }
}