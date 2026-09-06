package cl.bernardo.ohiggins.ms_matricula.service;

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
class AcademicoClientServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec getUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec getHeadersSpec;

    @Mock
    private WebClient.RequestBodyUriSpec postUriSpec;

    @Mock
    private WebClient.RequestBodySpec postBodySpec;

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

        doReturn(getUriSpec).when(webClient).get();
        doReturn(getHeadersSpec).when(getUriSpec).uri(anyString());
        doReturn(responseSpec).when(getHeadersSpec).retrieve();
        doReturn(Mono.just(cursoMock)).when(responseSpec).bodyToMono(Map.class);

        Map<String, Object> resultado = academicoClientService.obtenerCurso(1L);

        assertNotNull(resultado);
        assertEquals("1° Básico A", resultado.get("nombre"));
    }

    @Test
    void obtenerCurso_excepcion_retornaNull() {
        doReturn(getUriSpec).when(webClient).get();
        doReturn(getHeadersSpec).when(getUriSpec).uri(anyString());
        doThrow(new RuntimeException("Servicio caído")).when(getHeadersSpec).retrieve();

        Map<String, Object> resultado = academicoClientService.obtenerCurso(1L);

        assertNull(resultado);
    }

    @Test
    void incrementarAlumnos_exitoso_retornaTrue() {
        doReturn(postUriSpec).when(webClient).post();
        doReturn(postBodySpec).when(postUriSpec).uri(anyString());
        doReturn(responseSpec).when(postBodySpec).retrieve();
        doReturn(Mono.empty()).when(responseSpec).bodyToMono(Object.class);

        boolean resultado = academicoClientService.incrementarAlumnos(1L);

        assertTrue(resultado);
    }

    @Test
    void incrementarAlumnos_cursoLleno_retornaFalse() {
        doReturn(postUriSpec).when(webClient).post();
        doReturn(postBodySpec).when(postUriSpec).uri(anyString());
        doThrow(WebClientResponseException.create(409, "Conflict", HttpHeaders.EMPTY, new byte[0], null))
                .when(postBodySpec).retrieve();

        boolean resultado = academicoClientService.incrementarAlumnos(1L);

        assertFalse(resultado);
    }

    @Test
    void decrementarAlumnos_exitoso_noLanzaExcepcion() {
        doReturn(postUriSpec).when(webClient).post();
        doReturn(postBodySpec).when(postUriSpec).uri(anyString());
        doReturn(responseSpec).when(postBodySpec).retrieve();
        doReturn(Mono.empty()).when(responseSpec).bodyToMono(Object.class);

        assertDoesNotThrow(() -> academicoClientService.decrementarAlumnos(1L));
    }

    @Test
    void decrementarAlumnos_excepcion_noPropaga() {
        doReturn(postUriSpec).when(webClient).post();
        doReturn(postBodySpec).when(postUriSpec).uri(anyString());
        doThrow(new RuntimeException("Servicio caído")).when(postBodySpec).retrieve();

        assertDoesNotThrow(() -> academicoClientService.decrementarAlumnos(1L));
    }
}