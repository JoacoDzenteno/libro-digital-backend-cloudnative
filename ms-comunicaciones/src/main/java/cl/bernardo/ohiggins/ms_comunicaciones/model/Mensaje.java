package cl.bernardo.ohiggins.ms_comunicaciones.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_remitente", nullable = false)
    private Long idRemitente;

    @Column(name = "id_destinatario", nullable = false)
    private Long idDestinatario;

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMensaje tipo;

    @Column(nullable = false)
    private Boolean leido;

    public enum TipoMensaje {
        NOTIFICACION, COMUNICADO, ALERTA
    }
}