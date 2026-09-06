package cl.bernardo.ohiggins.ms_matricula.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    public boolean incrementarAlumnos(Long idCurso) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://ms-academico/api/academico/cursos/" + idCurso + "/incrementar-alumnos")
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            return true;
        } catch (WebClientResponseException e) {
            return false;
        }
    }

    public void decrementarAlumnos(Long idCurso) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://ms-academico/api/academico/cursos/" + idCurso + "/decrementar-alumnos")
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            // si falla, no bloqueamos la eliminación de la matrícula
        }
    }
}