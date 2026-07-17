package com.example.conectaaivrs.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendaRequest(
        @NotNull UUID eventoId,
        @NotBlank @Size(max = 200) String titulo,
        String descricao,
        @NotBlank @Size(max = 100) String categoria,
        @Size(max = 200) String local,
        @NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fim,
        Boolean ativo
) {}
