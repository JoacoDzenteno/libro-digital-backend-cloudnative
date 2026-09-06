package cl.bernardo.ohiggins.ms_reportes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

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
    void obtenerUsuario_exitoso_retornaMapa() {
        Map<String, Object> usuarioMock = Map.of("id", 1L);

        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.just(usuarioMock)).when(responseSpec).bodyToMono(Map.class);

        Map<String, Object> resultado = usuarioClientService.obtenerUsuario(1L);

        assertNotNull(resultado);
    }

    @Test
    void obtenerUsuario_excepcion_retornaNull() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doThrow(new RuntimeException("No encontrado")).when(headersSpec).retrieve();

        Map<String, Object> resultado = usuarioClientService.obtenerUsuario(99L);

        assertNull(resultado);
    }

    @Test
    void existeUsuario_existente_retornaTrue() {
        Map<String, Object> usuarioMock = Map.of("id", 1L);

        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.just(usuarioMock)).when(responseSpec).bodyToMono(Map.class);

        assertTrue(usuarioClientService.existeUsuario(1L));
    }

    @Test
    void existeUsuario_noExistente_retornaFalse() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doThrow(new RuntimeException("No encontrado")).when(headersSpec).retrieve();

        assertFalse(usuarioClientService.existeUsuario(99L));
    }
}