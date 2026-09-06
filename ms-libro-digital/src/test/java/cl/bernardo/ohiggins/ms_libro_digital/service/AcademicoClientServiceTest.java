package cl.bernardo.ohiggins.ms_libro_digital.service;

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
class AcademicoClientServiceTest {

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

    private AcademicoClientService academicoClientService;

    @BeforeEach
    void setUp() {
        academicoClientService = new AcademicoClientService(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void obtenerCurso_exitoso_retornaMapa() {
        Map<String, Object> cursoMock = Map.of("id", 1L, "nombre", "1° Básico A");

        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.just(cursoMock)).when(responseSpec).bodyToMono(Map.class);

        Map<String, Object> resultado = academicoClientService.obtenerCurso(1L);

        assertNotNull(resultado);
        assertEquals("1° Básico A", resultado.get("nombre"));
    }

    @Test
    void obtenerCurso_excepcion_retornaNull() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doThrow(new RuntimeException("Servicio caído")).when(headersSpec).retrieve();

        Map<String, Object> resultado = academicoClientService.obtenerCurso(1L);

        assertNull(resultado);
    }

    @Test
    void existeCurso_existente_retornaTrue() {
        Map<String, Object> cursoMock = Map.of("id", 1L);

        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.just(cursoMock)).when(responseSpec).bodyToMono(Map.class);

        assertTrue(academicoClientService.existeCurso(1L));
    }

    @Test
    void existeCurso_noExistente_retornaFalse() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doThrow(new RuntimeException("No encontrado")).when(headersSpec).retrieve();

        assertFalse(academicoClientService.existeCurso(99L));
    }
}