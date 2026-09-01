package com.example.conectaaivrs.controller;

import com.example.conectaaivrs.domain.auth.dto.TokenResponse;
import com.example.conectaaivrs.domain.google.GoogleUserInfoDTO;
import com.example.conectaaivrs.service.AuthService;
import com.example.conectaaivrs.service.google.LoginGoogleService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("auth/login/google")
public class LoginGoogleController {

    private final LoginGoogleService loginGoogleService;
    private final AuthService authService;

    public LoginGoogleController(LoginGoogleService loginGoogleService, AuthService authService) {
        this.loginGoogleService = loginGoogleService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<Void> redirecionarGoogle(){
        var url = loginGoogleService.gerarUrl();

        var headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/autorizado")
    public void autenticarUsuarioOAuth(
            @RequestParam String code,
            HttpServletResponse response
    ) throws IOException {

        GoogleUserInfoDTO userInfo = loginGoogleService.captureInfosUserGoogle(code);

        TokenResponse tokenDTO = authService.acessoAuthGoogle(userInfo);

        response.sendRedirect("http://localhost:5173/login/success?refreshToken=" +
                URLEncoder.encode(tokenDTO.refreshToken(), StandardCharsets.UTF_8));
    }
}
