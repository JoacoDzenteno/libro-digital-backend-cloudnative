package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.exception.ServicioNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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

    private void prepararLlamada() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
    }

    private WebClientResponseException respuesta404() {
        return WebClientResponseException.create(404, "Not Found", HttpHeaders.EMPTY, new byte[0], null);
    }

    @Test
    void obtenerUsuario_exitoso_retornaMapa() {
        Map<String, Object> usuarioMock = Map.of("id", 1L);

        prepararLlamada();
        doReturn(Mono.just(usuarioMock)).when(responseSpec).bodyToMono(Map.class);

        Map<String, Object> resultado = usuarioClientService.obtenerUsuario(1L);

        assertNotNull(resultado);
    }

    @Test
    void obtenerUsuario_respuesta404_retornaNull() {
        prepararLlamada();
        doThrow(respuesta404()).when(responseSpec).bodyToMono(Map.class);

        Map<String, Object> resultado = usuarioClientService.obtenerUsuario(99L);

        assertNull(resultado);
    }

    @Test
    void obtenerUsuario_servicioCaido_lanzaServicioNoDisponible() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doThrow(new RuntimeException("Servicio caído")).when(headersSpec).retrieve();

        assertThrows(ServicioNoDisponibleException.class,
                () -> usuarioClientService.obtenerUsuario(1L));
    }

    @Test
    void existeUsuario_existente_retornaTrue() {
        Map<String, Object> usuarioMock = Map.of("id", 1L);

        prepararLlamada();
        doReturn(Mono.just(usuarioMock)).when(responseSpec).bodyToMono(Map.class);

        assertTrue(usuarioClientService.existeUsuario(1L));
    }

    @Test
    void existeUsuario_noExistente_retornaFalse() {
        prepararLlamada();
        doThrow(respuesta404()).when(responseSpec).bodyToMono(Map.class);

        assertFalse(usuarioClientService.existeUsuario(99L));
    }

    @Test
    void existeUsuario_servicioCaido_lanzaServicioNoDisponible() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doThrow(new RuntimeException("Servicio caído")).when(headersSpec).retrieve();

        assertThrows(ServicioNoDisponibleException.class,
                () -> usuarioClientService.existeUsuario(1L));
    }
}