package com.example.conectaaivrs.infra.paginacao;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resposta paginada por cursor. Use `nextCursor` (campos `id` e `data`) como `cursorId` e `cursorData` na próxima requisição.")
public record PageResponse<T>(
        @Schema(description = "Itens da página atual")
        List<T> content,
        @Schema(description = "Cursor da próxima página, ou null quando não há mais resultados")
        CursorInfo nextCursor,
        @Schema(description = "Indica se existem mais resultados após esta página")
        boolean hasNext
) {}