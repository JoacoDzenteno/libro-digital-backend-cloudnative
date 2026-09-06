package cl.bernardo.ohiggins.ms_matricula.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioClientServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private WebClient.RequestHeadersSpec headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private UsuarioClientService usuarioClientService;

    @BeforeEach
    void setUp() {
        usuarioClientService = new UsuarioClientService(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void existeUsuario_exitoso_retornaTrue() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.empty()).when(responseSpec).bodyToMono(Object.class);

        boolean resultado = usuarioClientService.existeUsuario(2L);

        assertTrue(resultado);
    }

    @Test
    void existeUsuario_excepcion_retornaFalse() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doThrow(new RuntimeException("No encontrado")).when(headersSpec).retrieve();

        boolean resultado = usuarioClientService.existeUsuario(99L);

        assertFalse(resultado);
    }
}