package com.example.conectaaivrs.domain.post.dto;

import com.example.conectaaivrs.domain.post.TipoPost;
import com.example.conectaaivrs.domain.post.VisibilidadePost;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PostRequest(
        @NotNull UUID eventoId,
        @NotBlank String texto,
        String imagemUrl,
        TipoPost tipo,
        VisibilidadePost visibilidade
) {}
