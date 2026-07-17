package com.example.conectaaivrs.domain.inscricao;

import com.example.conectaaivrs.domain.evento.Evento;
import com.example.conectaaivrs.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "participantes_evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class ParticipanteEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ParticipantePapel papel = ParticipantePapel.PARTICIPANTE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ParticipanteStatus status = ParticipanteStatus.INSCRITO;

    @Builder.Default
    private Boolean checkin = false;

    @Column(name = "data_inscricao", nullable = false, updatable = false)
    private LocalDateTime dataInscricao;

    @PrePersist
    protected void onCreate() {
        dataInscricao = LocalDateTime.now();
    }
}
