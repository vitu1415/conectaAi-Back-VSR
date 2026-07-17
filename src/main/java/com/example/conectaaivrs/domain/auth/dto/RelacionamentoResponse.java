package com.example.conectaaivrs.domain.auth.dto;

import com.example.conectaaivrs.domain.conexao.ConexaoStatus;

public record RelacionamentoResponse(
        ConexaoStatus status
) {}
