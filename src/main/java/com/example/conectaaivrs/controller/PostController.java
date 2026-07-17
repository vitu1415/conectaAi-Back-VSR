package com.example.conectaaivrs.controller;

import com.example.conectaaivrs.domain.post.dto.FeedEventoResponse;
import com.example.conectaaivrs.domain.post.dto.PostRequest;
import com.example.conectaaivrs.domain.post.dto.PostResponse;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.service.PostService;
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
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/post/{usuarioId}")
    public ResponseEntity<List<FeedEventoResponse>> feed(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(postService.feed(usuarioId));
    }

    @GetMapping("/post/eventos/{eventoId}")
    public ResponseEntity<List<PostResponse>> listarPorEvento(@PathVariable UUID eventoId) {
        return ResponseEntity.ok(postService.listarPorEvento(eventoId));
    }

    @GetMapping("/post/usuarios/{usuarioId}")
    public ResponseEntity<List<PostResponse>> listarPorUsuario(
            @PathVariable UUID usuarioId,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(postService.listarPorUsuario(usuarioId, usuario.getId()));
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
