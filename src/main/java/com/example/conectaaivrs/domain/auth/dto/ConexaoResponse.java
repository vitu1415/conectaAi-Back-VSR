package com.example.conectaaivrs.domain.auth.dto;

import com.example.conectaaivrs.domain.conexao.Conexao;
import com.example.conectaaivrs.domain.conexao.ConexaoStatus;
import com.example.conectaaivrs.domain.usuario.Usuario;

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

    public static ConexaoResponse fromEntityComUsuarioLogado(Conexao conexao, Usuario usuarioLogado) {
        Usuario other = conexao.getUsuarioOrigem().getId().equals(usuarioLogado.getId())
                ? conexao.getUsuarioDestino()
                : conexao.getUsuarioOrigem();
        return new ConexaoResponse(
                conexao.getId(),
                UsuarioResumo.fromEntity(conexao.getUsuarioOrigem()),
                UsuarioResumo.fromEntity(conexao.getUsuarioDestino()),
                conexao.getStatus(),
                conexao.getCriadoEm()
        );
    }
}
