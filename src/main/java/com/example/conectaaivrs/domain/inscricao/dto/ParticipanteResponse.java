package com.example.conectaaivrs.domain.inscricao.dto;

import com.example.conectaaivrs.domain.inscricao.ParticipanteEvento;
import com.example.conectaaivrs.domain.inscricao.ParticipantePapel;
import com.example.conectaaivrs.domain.inscricao.ParticipanteStatus;
import com.example.conectaaivrs.domain.usuario.dto.UsuarioResumo;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParticipanteResponse(
        UUID id,
        UsuarioResumo usuario,
        ParticipantePapel papel,
        ParticipanteStatus status,
        Boolean checkin,
        LocalDateTime dataInscricao
) {
    public static ParticipanteResponse fromEntity(ParticipanteEvento participante) {
        return new ParticipanteResponse(
                participante.getId(),
                UsuarioResumo.fromEntity(participante.getUsuario()),
                participante.getPapel(),
                participante.getStatus(),
                participante.getCheckin(),
                participante.getDataInscricao()
        );
    }
}
