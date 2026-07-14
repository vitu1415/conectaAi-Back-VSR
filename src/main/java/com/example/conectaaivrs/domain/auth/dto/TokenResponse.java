package com.example.conectaaivrs.domain.auth.dto;

public record TokenResponse(
        String token,
        String refreshToken
) {}
