package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.auth.dto.*;
import com.example.conectaaivrs.domain.refreshToken.RefreshToken;
import com.example.conectaaivrs.domain.refreshToken.RefreshTokenRepository;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.domain.usuario.UsuarioRepository;
import com.example.conectaaivrs.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public TokenResponse register(RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .provider(request.provider())
                .providerId(request.providerId())
                .dataNascimento(request.dataNascimento())
                .genero(request.genero())
                .bio(request.bio())
                .fotoPerfil(request.fotoPerfil())
                .cidade(request.cidade())
                .estado(request.estado())
                .build();

        usuarioRepository.save(usuario);

        return gerarTokenResponse(usuario);
    }

    public TokenResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        var authentication = authenticationManager.authenticate(authToken);

        Usuario usuario = (Usuario) authentication.getPrincipal();
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return gerarTokenResponse(usuario);
    }

    public TokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token não encontrado"));

        if (!refreshToken.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado");
        }

        refreshToken.setRevogado(true);
        refreshTokenRepository.save(refreshToken);

        return gerarTokenResponse(refreshToken.getUsuario());
    }

    public void logout(LogoutRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token não encontrado"));

        refreshToken.setRevogado(true);
        refreshTokenRepository.save(refreshToken);
    }

    public void redefinirSenha(RedefinirSenhaRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);

        revokeAllTokens(usuario.getId());
    }

    private void revokeAllTokens(UUID usuarioId) {
        var tokens = refreshTokenRepository.findAllByUsuarioId(usuarioId);
        tokens.forEach(t -> t.setRevogado(true));
        refreshTokenRepository.saveAll(tokens);
    }

    private TokenResponse gerarTokenResponse(Usuario usuario) {
        String accessToken = tokenService.gerarToken(usuario);

        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .token(UUID.randomUUID().toString())
                .expiraEm(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(accessToken, refreshToken.getToken());
    }
}
