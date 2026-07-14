package com.example.conectaaivrs.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequest(
        @NotBlank @Email String email,
        @NotBlank String token,
        @NotBlank @Size(min = 6) String novaSenha
) {}
