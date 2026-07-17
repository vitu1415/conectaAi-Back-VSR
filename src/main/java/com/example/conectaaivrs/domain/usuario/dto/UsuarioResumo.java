package com.example.conectaaivrs.domain.usuario.dto;

import com.example.conectaaivrs.domain.usuario.Usuario;

import java.util.UUID;

public record UsuarioResumo(
        UUID id,
        String nome,
        String fotoPerfil,
        String cidade,
        String estado
) {
    public static UsuarioResumo fromEntity(Usuario usuario) {
        return new UsuarioResumo(
                usuario.getId(),
                usuario.getNome(),
                usuario.getFotoPerfil(),
                usuario.getCidade(),
                usuario.getEstado()
        );
    }
}
