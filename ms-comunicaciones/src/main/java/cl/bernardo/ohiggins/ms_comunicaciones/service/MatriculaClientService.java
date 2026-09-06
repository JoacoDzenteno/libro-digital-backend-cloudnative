package cl.bernardo.ohiggins.ms_comunicaciones.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatriculaClientService {

    private final WebClient.Builder webClientBuilder;

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
        } catch (Exception e) {
            return null;
        }
    }
}