package com.example.conectaaivrs.domain.storage.dto;

public record UploadResponse(
        String url,
        String nomeArquivo,
        String contentType
) {
}
