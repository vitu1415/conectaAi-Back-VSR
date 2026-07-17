package com.example.conectaaivrs.domain.evento.dto;

import com.example.conectaaivrs.domain.evento.EventoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoRequest(
        @NotBlank @Size(max = 150) String nome,
        String descricao,
        String banner,
        @Size(max = 100) String cidade,
        @Size(max = 100) String estado,
        String endereco,
        BigDecimal latitude,
        BigDecimal longitude,
        @NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fim,
        Integer capacidade,
        Boolean privado,
        EventoStatus status
) {}
