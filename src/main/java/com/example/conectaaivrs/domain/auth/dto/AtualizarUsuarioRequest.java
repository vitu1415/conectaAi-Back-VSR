package com.example.conectaaivrs.domain.auth.dto;

import com.example.conectaaivrs.domain.usuario.Genero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AtualizarUsuarioRequest(
        @Size(max = 120) String nome,
        @Size(max = 150) String email,
        @Size(min = 6) String senha,
        LocalDate dataNascimento,
        Genero genero,
        String bio,
        @Size(max = 120) String cidade,
        @Size(max = 120) String estado
) {}
