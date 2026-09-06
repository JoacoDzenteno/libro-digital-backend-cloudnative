package cl.bernardo.ohiggins.ms_libro_digital.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AcademicoClientService {

    private final WebClient.Builder webClientBuilder;

    public Map<String, Object> obtenerCurso(Long idCurso) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://ms-academico/api/academico/cursos/" + idCurso)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean existeCurso(Long idCurso) {
        return obtenerCurso(idCurso) != null;
    }
}