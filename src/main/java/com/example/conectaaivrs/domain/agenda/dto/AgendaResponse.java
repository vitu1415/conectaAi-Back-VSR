package com.example.conectaaivrs.domain.agenda.dto;

import com.example.conectaaivrs.domain.agenda.Agenda;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendaResponse(
        UUID id,
        UUID eventoId,
        String titulo,
        String descricao,
        String categoria,
        String local,
        LocalDateTime inicio,
        LocalDateTime fim,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static AgendaResponse fromEntity(Agenda agenda) {
        return new AgendaResponse(
                agenda.getId(),
                agenda.getEvento().getId(),
                agenda.getTitulo(),
                agenda.getDescricao(),
                agenda.getCategoria(),
                agenda.getLocal(),
                agenda.getInicio(),
                agenda.getFim(),
                agenda.getAtivo(),
                agenda.getCriadoEm(),
                agenda.getAtualizadoEm()
        );
    }
}
