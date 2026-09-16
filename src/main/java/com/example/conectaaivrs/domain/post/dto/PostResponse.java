package com.example.conectaaivrs.domain.post.dto;

import com.example.conectaaivrs.domain.post.Post;
import com.example.conectaaivrs.domain.post.TipoPost;
import com.example.conectaaivrs.domain.post.VisibilidadePost;
import com.example.conectaaivrs.domain.usuario.dto.UsuarioResumo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        UUID eventoId,
        UsuarioResumo autor,
        String texto,
        List<MidiaPostResponse> midias,
        TipoPost tipo,
        VisibilidadePost visibilidade,
        Boolean ativo,
        int curtidasCount,
        boolean curtido,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static PostResponse fromEntity(Post post, int curtidasCount, boolean curtido) {
        List<MidiaPostResponse> midias = post.getMidias() != null
                ? post.getMidias().stream().map(MidiaPostResponse::fromEntity).toList()
                : List.of();

        return new PostResponse(
                post.getId(),
                post.getEvento().getId(),
                UsuarioResumo.fromEntity(post.getAutor()),
                post.getTexto(),
                midias,
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
