package com.example.conectaaivrs.domain.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record AtualizarFotoRequest(
        @NotBlank String fotoPerfil
) {}
