package com.example.conectaaivrs.domain.auth.dto;

import com.example.conectaaivrs.domain.usuario.Genero;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 6) String senha,
        @Size(max = 50) String provider,
        @Size(max = 150) String providerId,
        LocalDate dataNascimento,
        Genero genero,
        String bio,
        String fotoPerfil,
        @Size(max = 120) String cidade,
        @Size(max = 120) String estado
) {}
