package com.example.conectaaivrs.domain.comentario.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ComentarioRequest(
        @NotBlank String texto,
        UUID comentarioPaiId
) {}
