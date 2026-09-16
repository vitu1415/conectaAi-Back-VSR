package com.example.conectaaivrs.domain.post.dto;

import com.example.conectaaivrs.domain.post.MidiaPost;
import com.example.conectaaivrs.domain.post.MidiaTipo;

import java.util.UUID;

public record MidiaPostResponse(
        UUID id,
        String url,
        String nomeArquivo,
        String contentType,
        MidiaTipo tipo,
        Integer ordem
) {
    public static MidiaPostResponse fromEntity(MidiaPost midia) {
        return new MidiaPostResponse(
                midia.getId(),
                midia.getUrl(),
                midia.getNomeArquivo(),
                midia.getContentType(),
                midia.getTipo(),
                midia.getOrdem()
        );
    }
}
