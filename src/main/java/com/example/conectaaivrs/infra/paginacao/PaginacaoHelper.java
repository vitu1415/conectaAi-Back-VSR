package com.example.conectaaivrs.infra.paginacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class PaginacaoHelper {

    public static final int LIMITE_PADRAO = 5;
    public static final int LIMITE_MAXIMO = 10;

    public static final UUID INICIO_DESC_ID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    public static final LocalDateTime INICIO_DESC_DATA = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    public static final UUID INICIO_ASC_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final LocalDateTime INICIO_ASC_DATA = LocalDateTime.of(1, 1, 1, 0, 0, 0);

    private PaginacaoHelper() {}

    public static int normalizarLimite(Integer limite) {
        if (limite == null || limite <= 0) {
            return LIMITE_PADRAO;
        }
        return Math.min(limite, LIMITE_MAXIMO);
    }

    public static <T> PageResponse<T> montar(List<T> itens, int limite, Function<T, CursorInfo> cursorExtractor) {
        boolean hasNext = itens.size() > limite;
        List<T> content = hasNext ? itens.subList(0, limite) : itens;
        CursorInfo nextCursor = null;
        if (hasNext && !content.isEmpty()) {
            nextCursor = cursorExtractor.apply(content.get(content.size() - 1));
        }
        return new PageResponse<>(content, nextCursor, hasNext);
    }
}