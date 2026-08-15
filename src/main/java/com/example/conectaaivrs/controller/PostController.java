package com.example.conectaaivrs.controller;

import com.example.conectaaivrs.domain.post.dto.FeedEventoResponse;
import com.example.conectaaivrs.domain.post.dto.PostRequest;
import com.example.conectaaivrs.domain.post.dto.PostResponse;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.infra.paginacao.PageResponse;
import com.example.conectaaivrs.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearer-key")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/post/feed/{usuarioId}")
    @Operation(summary = "Feed de posts por usuário paginado por cursor",
            description = "Retorna posts dos eventos em que o usuário participa. Use o `nextCursor` da resposta anterior na próxima chamada.")
    public ResponseEntity<PageResponse<FeedEventoResponse>> feed(
            @PathVariable UUID usuarioId,
            @Parameter(description = "Id do último post recebido (vem de nextCursor.id)")
            @RequestParam(required = false) UUID cursorId,
            @Parameter(description = "Data de criação do último post recebido (vem de nextCursor.data)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorData,
            @Parameter(description = "Quantidade de itens por página (padrão 5, máximo 50)")
            @RequestParam(required = false) Integer limite) {
        return ResponseEntity.ok(postService.feed(usuarioId, cursorId, cursorData, limite));
    }

    @GetMapping("/post/eventos/{eventoId}")
    @Operation(summary = "Listar posts de um evento paginados por cursor")
    public ResponseEntity<PageResponse<PostResponse>> listarPorEvento(
            @PathVariable UUID eventoId,
            @Parameter(description = "Id do último post recebido (vem de nextCursor.id)")
            @RequestParam(required = false) UUID cursorId,
            @Parameter(description = "Data de criação do último post recebido (vem de nextCursor.data)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorData,
            @Parameter(description = "Quantidade de itens por página (padrão 5, máximo 10)")
            @RequestParam(required = false) Integer limite) {
        return ResponseEntity.ok(postService.listarPorEvento(eventoId, cursorId, cursorData, limite));
    }

    @GetMapping("/post/usuarios/{usuarioId}")
    @Operation(summary = "Listar posts de um usuário paginados por cursor")
    public ResponseEntity<PageResponse<PostResponse>> listarPorUsuario(
            @PathVariable UUID usuarioId,
            @AuthenticationPrincipal Usuario usuario,
            @Parameter(description = "Id do último post recebido (vem de nextCursor.id)")
            @RequestParam(required = false) UUID cursorId,
            @Parameter(description = "Data de criação do último post recebido (vem de nextCursor.data)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorData,
            @Parameter(description = "Quantidade de itens por página (padrão 5, máximo 10)")
            @RequestParam(required = false) Integer limite) {
        return ResponseEntity.ok(postService.listarPorUsuario(usuarioId, usuario.getId(), cursorId, cursorData, limite));
    }

    @PostMapping("/post")
    public ResponseEntity<PostResponse> criar(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.criar(usuario, request));
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostResponse> buscarPorId(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(postService.buscarPorId(id, usuario.getId()));
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<PostResponse> atualizar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id,
            @RequestBody @Valid PostRequest request) {
        return ResponseEntity.ok(postService.atualizar(usuario, id, request));
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> deletar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        postService.deletar(usuario, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/post/{id}/curtir")
    public ResponseEntity<PostResponse> curtir(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        return ResponseEntity.ok(postService.curtir(usuario, id));
    }

    @DeleteMapping("/post/{id}/curtir")
    public ResponseEntity<PostResponse> descurtir(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        return ResponseEntity.ok(postService.descurtir(usuario, id));
    }
}
