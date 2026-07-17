package com.example.conectaaivrs.domain.post.dto;

import com.example.conectaaivrs.domain.evento.dto.EventoResponse;

import java.util.List;

public record FeedEventoResponse(
        EventoResponse evento,
        List<PostResponse> posts
) {}
