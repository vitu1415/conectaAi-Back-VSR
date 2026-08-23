package com.example.conectaaivrs.controller;

import com.example.conectaaivrs.domain.evento.dto.EventoRequest;
import com.example.conectaaivrs.domain.evento.dto.EventoResponse;
import com.example.conectaaivrs.domain.inscricao.dto.ParticipanteResponse;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.infra.paginacao.PageResponse;
import com.example.conectaaivrs.service.EventoService;
import com.example.conectaaivrs.service.InscricaoService;
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
@RequestMapping("/eventos")
@SecurityRequirement(name = "bearer-key")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private InscricaoService inscricaoService;

    @GetMapping
    @Operation(summary = "Listar eventos paginados por cursor",
            description = "Retorna uma página de eventos. Use o `nextCursor` da resposta anterior na próxima chamada. `cursorData` é a data de criação do último item recebido e `cursorId` o seu id.")
    public ResponseEntity<PageResponse<EventoResponse>> listarTodos(
            @Parameter(description = "Id do último evento recebido (vem de nextCursor.id)")
            @RequestParam(required = false) UUID cursorId,
            @Parameter(description = "Data de criação do último evento recebido (vem de nextCursor.data)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorData,
            @Parameter(description = "Quantidade de itens por página (padrão 5, máximo 10)")
            @RequestParam(required = false) Integer limite) {
        return ResponseEntity.ok(eventoService.listarTodos(cursorId, cursorData, limite));
    }

    @GetMapping("/proximos")
    @Operation(summary = "Listar próximos eventos paginados por cursor",
            description = "Ordenados por data de início. Use `nextCursor` da resposta anterior na próxima chamada.")
    public ResponseEntity<PageResponse<EventoResponse>> listarProximos(
            @Parameter(description = "Id do último evento recebido (vem de nextCursor.id)")
            @RequestParam(required = false) UUID cursorId,
            @Parameter(description = "Data de início do último evento recebido (vem de nextCursor.data)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorData,
            @Parameter(description = "Quantidade de itens por página (padrão 5, máximo 10)")
            @RequestParam(required = false) Integer limite) {
        return ResponseEntity.ok(eventoService.listarProximos(cursorId, cursorData, limite));
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
    @Operation(summary = "Listar participantes do evento paginados por cursor",
            description = "Ordenados por data de inscrição. Use o `nextCursor` da resposta anterior na próxima chamada. `cursorData` é a data de inscrição do último participante recebido e `cursorId` o seu id.")
    public ResponseEntity<PageResponse<ParticipanteResponse>> listarParticipantes(
            @PathVariable UUID id,
            @Parameter(description = "Id do último participante recebido (vem de nextCursor.id)")
            @RequestParam(required = false) UUID cursorId,
            @Parameter(description = "Data de inscrição do último participante recebido (vem de nextCursor.data)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorData,
            @Parameter(description = "Quantidade de itens por página (padrão 5, máximo 10)")
            @RequestParam(required = false) Integer limite) {
        return ResponseEntity.ok(inscricaoService.listarParticipantes(id, cursorId, cursorData, limite));
    }
}
