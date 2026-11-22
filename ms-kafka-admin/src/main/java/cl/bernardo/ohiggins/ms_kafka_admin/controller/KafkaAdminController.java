package cl.bernardo.ohiggins.ms_kafka_admin.controller;

import cl.bernardo.ohiggins.ms_kafka_admin.dto.AumentarParticionesRequest;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.CrearTopicoRequest;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.GrupoDetalle;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.GrupoResumen;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.ModificarConfiguracionRequest;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.ReglasNombreTopico;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.RespuestaOperacion;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.TopicoDetalle;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.TopicoResumen;
import cl.bernardo.ohiggins.ms_kafka_admin.service.KafkaAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * API REST para administrar Kafka.
 * Solo traduce HTTP a llamadas del service: no contiene lógica de Kafka.
 */
@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaAdminController {

    private static final String MENSAJE_NOMBRE = "El nombre del tópico " + ReglasNombreTopico.MENSAJE;

    private final KafkaAdminService kafkaAdminService;

    // ---------- Tópicos ----------

    @GetMapping("/topics")
    public List<TopicoResumen> listarTopicos() {
        return kafkaAdminService.listarTopicos();
    }

    @GetMapping("/topics/{nombre}")
    public TopicoDetalle obtenerTopico(
            @PathVariable @Pattern(regexp = ReglasNombreTopico.PATRON, message = MENSAJE_NOMBRE) String nombre) {
        return kafkaAdminService.obtenerTopico(nombre);
    }

    @PostMapping("/topics")
    public ResponseEntity<RespuestaOperacion> crearTopico(@Valid @RequestBody CrearTopicoRequest request) {
        // Si no se envía configuración, se usa un mapa vacío (valores por defecto del broker)
        Map<String, String> configuracion = request.configuracion() == null ? Map.of() : request.configuracion();
        kafkaAdminService.crearTopico(request.nombre(), request.particiones(),
                request.replicas().shortValue(), configuracion);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RespuestaOperacion("Tópico '" + request.nombre() + "' creado correctamente"));
    }

    @PatchMapping("/topics/{nombre}/config")
    public RespuestaOperacion modificarConfiguracion(
            @PathVariable @Pattern(regexp = ReglasNombreTopico.PATRON, message = MENSAJE_NOMBRE) String nombre,
            @Valid @RequestBody ModificarConfiguracionRequest request) {
        kafkaAdminService.modificarConfiguracion(nombre, request.configuracion());
        return new RespuestaOperacion("Configuración de '" + nombre + "' modificada: " + request.configuracion());
    }

    @PatchMapping("/topics/{nombre}/particiones")
    public RespuestaOperacion aumentarParticiones(
            @PathVariable @Pattern(regexp = ReglasNombreTopico.PATRON, message = MENSAJE_NOMBRE) String nombre,
            @Valid @RequestBody AumentarParticionesRequest request) {
        kafkaAdminService.aumentarParticiones(nombre, request.particiones());
        return new RespuestaOperacion("Tópico '" + nombre + "' ahora tiene " + request.particiones() + " particiones");
    }

    @DeleteMapping("/topics/{nombre}")
    public ResponseEntity<Void> eliminarTopico(
            @PathVariable @Pattern(regexp = ReglasNombreTopico.PATRON, message = MENSAJE_NOMBRE) String nombre) {
        kafkaAdminService.eliminarTopico(nombre);
        return ResponseEntity.noContent().build();
    }

    // ---------- Consumer groups ----------

    @GetMapping("/consumer-groups")
    public List<GrupoResumen> listarGrupos() {
        return kafkaAdminService.listarGrupos();
    }

    @GetMapping("/consumer-groups/{grupo}")
    public GrupoDetalle obtenerGrupo(@PathVariable String grupo) {
        return kafkaAdminService.obtenerGrupo(grupo);
    }

    @DeleteMapping("/consumer-groups/{grupo}")
    public ResponseEntity<Void> eliminarGrupo(@PathVariable String grupo) {
        kafkaAdminService.eliminarGrupo(grupo);
        return ResponseEntity.noContent().build();
    }
}