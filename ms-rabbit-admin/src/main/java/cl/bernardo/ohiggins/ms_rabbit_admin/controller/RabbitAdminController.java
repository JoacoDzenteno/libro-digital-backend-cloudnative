package cl.bernardo.ohiggins.ms_rabbit_admin.controller;


import cl.bernardo.ohiggins.ms_rabbit_admin.dto.BindingRequest;
import cl.bernardo.ohiggins.ms_rabbit_admin.dto.CrearColaRequest;
import cl.bernardo.ohiggins.ms_rabbit_admin.dto.CrearExchangeRequest;
import cl.bernardo.ohiggins.ms_rabbit_admin.dto.ReglasNombre;
import cl.bernardo.ohiggins.ms_rabbit_admin.dto.RespuestaOperacion;
import cl.bernardo.ohiggins.ms_rabbit_admin.service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API REST para administrar RabbitMQ.
 * Solo traduce HTTP a llamadas del servicio: no contiene lógica de RabbitMQ.
 */
@RestController
@RequestMapping("/api/rabbit")
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    // ---------- Colas ----------

    @PostMapping("/queues")
    public ResponseEntity<RespuestaOperacion> crearCola(@Valid @RequestBody CrearColaRequest request) {
        rabbitAdminService.crearCola(request.nombre(), request.durable());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RespuestaOperacion("Cola '" + request.nombre() + "' creada correctamente"));
    }

    @DeleteMapping("/queues/{nombre}")
    public ResponseEntity<Void> eliminarCola(
            @PathVariable
            @Pattern(regexp = ReglasNombre.PATRON, message = "El nombre de la cola " + ReglasNombre.MENSAJE)
            String nombre) {
        rabbitAdminService.eliminarCola(nombre);
        return ResponseEntity.noContent().build();
    }

    // ---------- Exchanges ----------

    @PostMapping("/exchanges")
    public ResponseEntity<RespuestaOperacion> crearExchange(@Valid @RequestBody CrearExchangeRequest request) {
        rabbitAdminService.crearExchange(request.nombre(), request.tipo(), request.durable());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RespuestaOperacion("Exchange '" + request.nombre() + "' creado correctamente"));
    }

    @DeleteMapping("/exchanges/{nombre}")
    public ResponseEntity<Void> eliminarExchange(
            @PathVariable
            @Pattern(regexp = ReglasNombre.PATRON, message = "El nombre del exchange " + ReglasNombre.MENSAJE)
            String nombre) {
        rabbitAdminService.eliminarExchange(nombre);
        return ResponseEntity.noContent().build();
    }

    // ---------- Bindings ----------

    @PostMapping("/bindings")
    public ResponseEntity<RespuestaOperacion> crearBinding(@Valid @RequestBody BindingRequest request) {
        rabbitAdminService.crearBinding(request.cola(), request.exchange(), request.routingKey());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RespuestaOperacion("Binding " + request.exchange() + " -> " + request.cola()
                        + " (routingKey='" + request.routingKey() + "') creado correctamente"));
    }

    /** Un binding no tiene nombre propio: se identifica por exchange + cola + routing key (query params). */
    @DeleteMapping("/bindings")
    public ResponseEntity<Void> eliminarBinding(@Valid @ModelAttribute BindingRequest request) {
        rabbitAdminService.eliminarBinding(request.cola(), request.exchange(), request.routingKey());
        return ResponseEntity.noContent().build();
    }
}