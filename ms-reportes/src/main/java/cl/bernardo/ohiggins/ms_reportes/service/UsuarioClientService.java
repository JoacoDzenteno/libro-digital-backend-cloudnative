package cl.bernardo.ohiggins.ms_reportes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioClientService {

    private final WebClient.Builder webClientBuilder;

    public Map<String, Object> obtenerUsuario(Long id) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://ms-usuarios/api/usuarios/" + id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean existeUsuario(Long id) {
        return obtenerUsuario(id) != null;
    }
}