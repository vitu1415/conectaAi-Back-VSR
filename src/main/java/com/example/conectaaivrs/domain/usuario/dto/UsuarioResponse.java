package com.example.conectaaivrs.domain.usuario.dto;

import com.example.conectaaivrs.domain.usuario.Genero;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.domain.usuario.UsuarioStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        String provider,
        LocalDate dataNascimento,
        Genero genero,
        String bio,
        String fotoPerfil,
        String cidade,
        String estado,
        UsuarioStatus status,
        LocalDateTime criadoEm,
        LocalDateTime ultimoLogin
) {
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getProvider(),
                usuario.getDataNascimento(),
                usuario.getGenero(),
                usuario.getBio(),
                usuario.getFotoPerfil(),
                usuario.getCidade(),
                usuario.getEstado(),
                usuario.getStatus(),
                usuario.getCriadoEm(),
                usuario.getUltimoLogin()
        );
    }
}
