package com.example.conectaaivrs.controller;

import com.example.conectaaivrs.domain.inscricao.dto.MeuEventoResponse;
import com.example.conectaaivrs.domain.interesse.dto.InteresseResponse;
import com.example.conectaaivrs.domain.usuario.dto.AtualizarFotoRequest;
import com.example.conectaaivrs.domain.usuario.dto.AtualizarUsuarioRequest;
import com.example.conectaaivrs.domain.usuario.dto.UsuarioResponse;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.service.InscricaoService;
import com.example.conectaaivrs.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "bearer-key")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private InscricaoService inscricaoService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(UsuarioResponse.fromEntity(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizar(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid AtualizarUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.atualizar(usuario, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PatchMapping("/me/foto")
    public ResponseEntity<UsuarioResponse> atualizarFoto(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid AtualizarFotoRequest request) {
        return ResponseEntity.ok(usuarioService.atualizarFoto(usuario, request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deletar(@AuthenticationPrincipal Usuario usuario) {
        usuarioService.deletar(usuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/interesses")
    public ResponseEntity<List<InteresseResponse>> listarInteresses(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(usuarioService.listarInteresses(usuario));
    }

    @PutMapping("/me/interesses")
    public ResponseEntity<List<InteresseResponse>> atualizarInteresses(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody List<String> nomes) {
        return ResponseEntity.ok(usuarioService.atualizarInteresses(usuario, nomes));
    }

    @GetMapping("/me/eventos")
    public ResponseEntity<List<MeuEventoResponse>> meusEventos(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(inscricaoService.listarMeusEventos(usuario));
    }
}
