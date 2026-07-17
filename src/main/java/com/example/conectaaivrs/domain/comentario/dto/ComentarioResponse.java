package com.example.conectaaivrs.domain.comentario.dto;

import com.example.conectaaivrs.domain.comentario.Comentario;
import com.example.conectaaivrs.domain.usuario.dto.UsuarioResumo;

import java.time.LocalDateTime;
import java.util.UUID;

public record ComentarioResponse(
        UUID id,
        UUID postId,
        UsuarioResumo usuario,
        String texto,
        UUID comentarioPaiId,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static ComentarioResponse fromEntity(Comentario comentario) {
        return new ComentarioResponse(
                comentario.getId(),
                comentario.getPost().getId(),
                UsuarioResumo.fromEntity(comentario.getUsuario()),
                comentario.getTexto(),
                comentario.getComentarioPai() != null ? comentario.getComentarioPai().getId() : null,
                comentario.getCriadoEm(),
                comentario.getAtualizadoEm()
        );
    }
}
