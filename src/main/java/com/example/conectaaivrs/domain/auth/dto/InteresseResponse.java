package com.example.conectaaivrs.domain.auth.dto;

import com.example.conectaaivrs.domain.interesse.Interesse;

import java.util.UUID;

public record InteresseResponse(
        UUID id,
        String nome,
        String categoria,
        String icone
) {
    public static InteresseResponse fromEntity(Interesse interesse) {
        return new InteresseResponse(
                interesse.getId(),
                interesse.getNome(),
                interesse.getCategoria(),
                interesse.getIcone()
        );
    }
}
