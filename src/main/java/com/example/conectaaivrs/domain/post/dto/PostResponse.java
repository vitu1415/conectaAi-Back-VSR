package com.example.conectaaivrs.domain.post.dto;

import com.example.conectaaivrs.domain.post.Post;
import com.example.conectaaivrs.domain.post.TipoPost;
import com.example.conectaaivrs.domain.post.VisibilidadePost;
import com.example.conectaaivrs.domain.usuario.dto.UsuarioResumo;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostResponse(
        UUID id,
        UUID eventoId,
        UsuarioResumo autor,
        String texto,
        String imagemUrl,
        TipoPost tipo,
        VisibilidadePost visibilidade,
        Boolean ativo,
        int curtidasCount,
        boolean curtido,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static PostResponse fromEntity(Post post, int curtidasCount, boolean curtido) {
        return new PostResponse(
                post.getId(),
                post.getEvento().getId(),
                UsuarioResumo.fromEntity(post.getAutor()),
                post.getTexto(),
                post.getImagemUrl(),
                post.getTipo(),
                post.getVisibilidade(),
                post.getAtivo(),
                curtidasCount,
                curtido,
                post.getCriadoEm(),
                post.getAtualizadoEm()
        );
    }
}
