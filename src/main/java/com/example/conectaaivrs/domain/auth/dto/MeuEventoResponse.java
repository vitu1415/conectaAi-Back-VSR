package com.example.conectaaivrs.domain.auth.dto;

import com.example.conectaaivrs.domain.inscricao.ParticipanteEvento;
import com.example.conectaaivrs.domain.inscricao.ParticipantePapel;
import com.example.conectaaivrs.domain.inscricao.ParticipanteStatus;

import java.time.LocalDateTime;

public record MeuEventoResponse(
        EventoResponse evento,
        ParticipantePapel papel,
        ParticipanteStatus status,
        LocalDateTime dataInscricao
) {
    public static MeuEventoResponse fromEntity(ParticipanteEvento participante) {
        return new MeuEventoResponse(
                EventoResponse.fromEntity(participante.getEvento()),
                participante.getPapel(),
                participante.getStatus(),
                participante.getDataInscricao()
        );
    }
}
