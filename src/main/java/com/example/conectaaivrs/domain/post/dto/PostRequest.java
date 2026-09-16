package com.example.conectaaivrs.domain.post.dto;

import com.example.conectaaivrs.domain.post.TipoPost;
import com.example.conectaaivrs.domain.post.VisibilidadePost;

import java.util.UUID;

public record PostRequest(
        UUID eventoId,
        String texto,
        TipoPost tipo,
        VisibilidadePost visibilidade
) {}
