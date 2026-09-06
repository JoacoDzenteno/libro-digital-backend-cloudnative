package cl.bernardo.ohiggins.ms_matricula.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UsuarioClientService {

    private final WebClient.Builder webClientBuilder;

    public boolean existeUsuario(Long id) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("http://ms-usuarios/api/usuarios/" + id)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}