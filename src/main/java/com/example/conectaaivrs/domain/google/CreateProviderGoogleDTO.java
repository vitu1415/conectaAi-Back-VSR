package com.example.conectaaivrs.domain.google;

public record CreateProviderGoogleDTO(
        String nome,
        String email,
        String provider,
        String providerId
) {
}