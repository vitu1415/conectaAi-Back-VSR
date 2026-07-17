package com.example.conectaaivrs.controller;

import com.example.conectaaivrs.domain.auth.dto.EventoRequest;
import com.example.conectaaivrs.domain.auth.dto.EventoResponse;
import com.example.conectaaivrs.domain.auth.dto.ParticipanteResponse;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.service.EventoService;
import com.example.conectaaivrs.service.InscricaoService;
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
@RequestMapping("/eventos")
@SecurityRequirement(name = "bearer-key")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private InscricaoService inscricaoService;

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarTodos() {
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    @GetMapping("/destaques")
    public ResponseEntity<List<EventoResponse>> listarDestaques() {
        return ResponseEntity.ok(eventoService.listarDestaques());
    }

    @GetMapping("/proximos")
    public ResponseEntity<List<EventoResponse>> listarProximos() {
        return ResponseEntity.ok(eventoService.listarProximos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<EventoResponse> criar(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid EventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventoService.criar(usuario, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> atualizar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id,
            @RequestBody @Valid EventoRequest request) {
        return ResponseEntity.ok(eventoService.atualizar(usuario, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        eventoService.deletar(usuario, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/participar")
    public ResponseEntity<ParticipanteResponse> participar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscricaoService.participar(usuario, id));
    }

    @DeleteMapping("/{id}/participar")
    public ResponseEntity<Void> cancelarParticipacao(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        inscricaoService.cancelarParticipacao(usuario, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participantes")
    public ResponseEntity<List<ParticipanteResponse>> listarParticipantes(@PathVariable UUID id) {
        return ResponseEntity.ok(inscricaoService.listarParticipantes(id));
    }
}
