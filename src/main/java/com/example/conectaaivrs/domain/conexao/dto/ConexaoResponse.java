package com.example.conectaaivrs.domain.conexao.dto;

import com.example.conectaaivrs.domain.conexao.Conexao;
import com.example.conectaaivrs.domain.conexao.ConexaoStatus;
import com.example.conectaaivrs.domain.usuario.dto.UsuarioResumo;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConexaoResponse(
        UUID id,
        UsuarioResumo usuarioOrigem,
        UsuarioResumo usuarioDestino,
        ConexaoStatus status,
        LocalDateTime criadoEm
) {
    public static ConexaoResponse fromEntity(Conexao conexao) {
        return new ConexaoResponse(
                conexao.getId(),
                UsuarioResumo.fromEntity(conexao.getUsuarioOrigem()),
                UsuarioResumo.fromEntity(conexao.getUsuarioDestino()),
                conexao.getStatus(),
                conexao.getCriadoEm()
        );
    }
}
