package com.example.conectaaivrs.controller;

import com.example.conectaaivrs.domain.agenda.dto.AgendaRequest;
import com.example.conectaaivrs.domain.agenda.dto.AgendaResponse;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.service.AgendaService;
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
@RequestMapping("/agenda")
@SecurityRequirement(name = "bearer-key")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @GetMapping
    public ResponseEntity<List<AgendaResponse>> listar(@RequestParam UUID eventoId) {
        return ResponseEntity.ok(agendaService.listar(eventoId));
    }

    @PostMapping
    public ResponseEntity<AgendaResponse> criar(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid AgendaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(agendaService.criar(usuario, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaResponse> atualizar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id,
            @RequestBody @Valid AgendaRequest request) {
        return ResponseEntity.ok(agendaService.atualizar(usuario, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        agendaService.deletar(usuario, id);
        return ResponseEntity.noContent().build();
    }
}
