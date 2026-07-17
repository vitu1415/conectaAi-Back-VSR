package com.example.conectaaivrs.controller;

import com.example.conectaaivrs.domain.comentario.dto.ComentarioRequest;
import com.example.conectaaivrs.domain.comentario.dto.ComentarioResponse;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.service.ComentarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearer-key")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @GetMapping("/post/{id}/comentarios")
    public ResponseEntity<List<ComentarioResponse>> listar(@PathVariable UUID id) {
        return ResponseEntity.ok(comentarioService.listar(id));
    }

    @PostMapping("/post/{id}/comentarios")
    public ResponseEntity<ComentarioResponse> criar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id,
            @RequestBody @Valid ComentarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comentarioService.criar(usuario, id, request));
    }

    @PutMapping("/comentarios/{id}")
    public ResponseEntity<ComentarioResponse> atualizar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id,
            @RequestBody @Valid ComentarioRequest request) {
        return ResponseEntity.ok(comentarioService.atualizar(usuario, id, request));
    }

    @DeleteMapping("/comentarios/{id}")
    public ResponseEntity<Void> deletar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        comentarioService.deletar(usuario, id);
        return ResponseEntity.noContent().build();
    }
}
