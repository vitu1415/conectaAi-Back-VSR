package com.example.conectaaivrs.controller;

import com.example.conectaaivrs.domain.storage.dto.UploadResponse;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.domain.usuario.UsuarioRepository;
import com.example.conectaaivrs.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Storage", description = "Upload de imagens para S3")
@RequestMapping("/storage")
public class StorageController {

    private final StorageService storageService;
    private final UsuarioRepository usuarioRepository;

    public StorageController(StorageService storageService, UsuarioRepository usuarioRepository) {
        this.storageService = storageService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Upload de imagem para S3",
            description = "Faz upload de uma imagem para o Amazon S3 e retorna a URL de acesso")
    public ResponseEntity<UploadResponse> uploadImagem(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "pasta", defaultValue = "profile") String pasta,
            @AuthenticationPrincipal Usuario usuario) {

            UploadResponse response = storageService.uploadImagem(file, pasta);

            usuario.setFotoPerfil(response.url());
            usuarioRepository.save(usuario);

            return ResponseEntity.ok(response);
    }
}
