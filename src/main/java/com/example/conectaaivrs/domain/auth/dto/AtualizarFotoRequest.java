package com.example.conectaaivrs.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AtualizarFotoRequest(
        @NotBlank String fotoPerfil
) {}
