package com.example.conectaaivrs.controller;

import com.example.conectaaivrs.domain.auth.dto.ConexaoResponse;
import com.example.conectaaivrs.domain.auth.dto.RelacionamentoResponse;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.service.ConexaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearer-key")
public class ConexaoController {

    @Autowired
    private ConexaoService conexaoService;

    @PostMapping("/usuarios/{id}/conexoes")
    public ResponseEntity<ConexaoResponse> enviarSolicitacao(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        return ResponseEntity.ok(conexaoService.enviarSolicitacao(usuario, id));
    }

    @GetMapping("/usuarios/me/conexoes/recebidas")
    public ResponseEntity<List<ConexaoResponse>> listarRecebidas(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(conexaoService.listarRecebidas(usuario));
    }

    @GetMapping("/usuarios/me/conexoes/enviadas")
    public ResponseEntity<List<ConexaoResponse>> listarEnviadas(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(conexaoService.listarEnviadas(usuario));
    }

    @PatchMapping("/conexoes/{id}/aceitar")
    public ResponseEntity<ConexaoResponse> aceitar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        return ResponseEntity.ok(conexaoService.aceitar(usuario, id));
    }

    @PatchMapping("/conexoes/{id}/recusar")
    public ResponseEntity<ConexaoResponse> recusar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        return ResponseEntity.ok(conexaoService.recusar(usuario, id));
    }

    @DeleteMapping("/conexoes/{id}")
    public ResponseEntity<Void> deletar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        conexaoService.deletar(usuario, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuarios/{id}/conexao")
    public ResponseEntity<RelacionamentoResponse> consultarRelacionamento(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable UUID id) {
        return ResponseEntity.ok(conexaoService.consultarRelacionamento(usuario, id));
    }

    @GetMapping("/usuarios/{id}/conexoes")
    public ResponseEntity<List<ConexaoResponse>> listarConexoes(
            @PathVariable UUID id) {
        return ResponseEntity.ok(conexaoService.listarConexoes(id));
    }
}
