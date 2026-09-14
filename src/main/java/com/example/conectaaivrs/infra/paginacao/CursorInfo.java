package com.example.conectaaivrs.infra.paginacao;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Cursor para a próxima página. Envie `id` e `data` como query params `cursorId` e `cursorData` na próxima chamada.")
public record CursorInfo(
        @Schema(description = "Id do último item retornado na página", example = "3d7c84da-b7c4-4c27-976f-70e5f197db43")
        UUID id,
        @Schema(description = "Data de ordenação do último item retornado (criadoEm ou inicio, conforme o endpoint)", example = "2026-08-15T13:48:38")
        LocalDateTime data
) {}