package com.example.conectaaivrs.domain.auth.dto;

import com.example.conectaaivrs.domain.evento.Evento;
import com.example.conectaaivrs.domain.evento.EventoStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record EventoResponse(
        UUID id,
        UsuarioResumo organizador,
        String nome,
        String descricao,
        String banner,
        String cidade,
        String estado,
        String endereco,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDateTime inicio,
        LocalDateTime fim,
        Integer capacidade,
        Boolean privado,
        EventoStatus status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static EventoResponse fromEntity(Evento evento) {
        return new EventoResponse(
                evento.getId(),
                UsuarioResumo.fromEntity(evento.getOrganizador()),
                evento.getNome(),
                evento.getDescricao(),
                evento.getBanner(),
                evento.getCidade(),
                evento.getEstado(),
                evento.getEndereco(),
                evento.getLatitude(),
                evento.getLongitude(),
                evento.getInicio(),
                evento.getFim(),
                evento.getCapacidade(),
                evento.getPrivado(),
                evento.getStatus(),
                evento.getCriadoEm(),
                evento.getAtualizadoEm()
        );
    }
}
