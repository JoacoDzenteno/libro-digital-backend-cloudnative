package cl.bernardo.ohiggins.ms_comunicaciones.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaClientServiceTest {

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

    private MatriculaClientService matriculaClientService;

    @BeforeEach
    void setUp() {
        matriculaClientService = new MatriculaClientService(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void obtenerIdApoderado_conMatriculaValida_retornaId() {
        List<Map<String, Object>> matriculas = List.of(Map.of("idApoderado", 3L));

        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.just(matriculas)).when(responseSpec).bodyToMono(List.class);

        Long resultado = matriculaClientService.obtenerIdApoderado(2L);

        assertEquals(3L, resultado);
    }

    @Test
    void obtenerIdApoderado_sinMatriculas_retornaNull() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.just(List.of())).when(responseSpec).bodyToMono(List.class);

        Long resultado = matriculaClientService.obtenerIdApoderado(2L);

        assertNull(resultado);
    }

    @Test
    void obtenerIdApoderado_matriculaSinIdApoderado_retornaNull() {
        List<Map<String, Object>> matriculas = List.of(Map.of("idEstudiante", 2L));

        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.just(matriculas)).when(responseSpec).bodyToMono(List.class);

        Long resultado = matriculaClientService.obtenerIdApoderado(2L);

        assertNull(resultado);
    }

    @Test
    void obtenerIdApoderado_excepcion_retornaNull() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doThrow(new RuntimeException("Servicio caído")).when(headersSpec).retrieve();

        Long resultado = matriculaClientService.obtenerIdApoderado(2L);

        assertNull(resultado);
    }

    @Test
    void obtenerIdApoderado_responseNull_retornaNull() {
        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.empty()).when(responseSpec).bodyToMono(List.class);

        Long resultado = matriculaClientService.obtenerIdApoderado(2L);

        assertNull(resultado);
    }
}