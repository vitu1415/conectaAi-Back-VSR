package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.auth.dto.*;
import com.example.conectaaivrs.domain.google.GoogleUserInfoDTO;
import com.example.conectaaivrs.domain.refreshToken.RefreshToken;
import com.example.conectaaivrs.domain.refreshToken.RefreshTokenRepository;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.domain.usuario.UsuarioRepository;
import com.example.conectaaivrs.infra.config.RefreshTokenCookieProperties;
import com.example.conectaaivrs.infra.security.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenCookieProperties cookieProperties;

    public TokenResponse register(RegisterRequest request, HttpServletResponse response) {
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
                .fotoPerfil("https://d38sp318d0ruxp.cloudfront.net/profile/72a4c371-863f-400b-93c0-5c314858fa5a.png")
                .cidade(request.cidade())
                .estado(request.estado())
                .build();

        usuarioRepository.save(usuario);

        return gerarTokenResponse(usuario, response);
    }

    public TokenResponse login(LoginRequest request, HttpServletResponse response) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        var authentication = authenticationManager.authenticate(authToken);

        Usuario usuario = (Usuario) authentication.getPrincipal();
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return gerarTokenResponse(usuario, response);
    }

    public TokenResponse refreshToken(String refreshTokenValue, HttpServletResponse response) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token não fornecido");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token não encontrado"));

        if (!refreshToken.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado");
        }

        refreshToken.setRevogado(true);
        refreshTokenRepository.save(refreshToken);

        return gerarTokenResponse(refreshToken.getUsuario(), response);
    }

    public void logout(String refreshTokenValue, HttpServletResponse response) {
        if (refreshTokenValue != null && !refreshTokenValue.isBlank()) {
            refreshTokenRepository.findByToken(refreshTokenValue)
                    .ifPresent(token -> {
                        token.setRevogado(true);
                        refreshTokenRepository.save(token);
                    });
        }

        limparCookie(response);
    }

    public void redefinirSenha(RedefinirSenhaRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);

        revokeAllTokens(usuario.getId());
    }

    public TokenResponse acessoAuthGoogle(GoogleUserInfoDTO googleUserInfoDTO, HttpServletResponse response) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(googleUserInfoDTO.email());

        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            var authToken = new UsernamePasswordAuthenticationToken(googleUserInfoDTO.email(), null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            return gerarTokenResponse(usuario, response);
        }

        Usuario usuario = usuarioService.criarUsuarioGoogle(googleUserInfoDTO.providerId(), googleUserInfoDTO.email());
        var authToken = new UsernamePasswordAuthenticationToken(googleUserInfoDTO.email(), null, usuario.getAuthorities());
        var authentication = authenticationManager.authenticate(authToken);
        usuario = (Usuario) authentication.getPrincipal();
        return gerarTokenResponse(usuario, response);
    }

    private void revokeAllTokens(UUID usuarioId) {
        var tokens = refreshTokenRepository.findAllByUsuarioId(usuarioId);
        tokens.forEach(t -> t.setRevogado(true));
        refreshTokenRepository.saveAll(tokens);
    }

    private TokenResponse gerarTokenResponse(Usuario usuario, HttpServletResponse response) {
        String accessToken = tokenService.gerarToken(usuario);

        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .token(UUID.randomUUID().toString())
                .expiraEm(LocalDateTime.now().plusDays(30))
                .build();

        refreshTokenRepository.save(refreshToken);

        adicionarCookie(response, refreshToken.getToken());

        return new TokenResponse(accessToken);
    }

    private void adicionarCookie(HttpServletResponse response, String valor) {
        Cookie cookie = new Cookie(cookieProperties.getName(), valor);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieProperties.isSecure());
        cookie.setPath(cookieProperties.getPath());
        cookie.setMaxAge(cookieProperties.getMaxAge());
        response.addCookie(cookie);
    }

    private void limparCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieProperties.getName(), "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieProperties.isSecure());
        cookie.setPath(cookieProperties.getPath());
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
