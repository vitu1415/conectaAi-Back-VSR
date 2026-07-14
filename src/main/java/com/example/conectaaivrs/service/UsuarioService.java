package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.auth.dto.AtualizarFotoRequest;
import com.example.conectaaivrs.domain.auth.dto.AtualizarUsuarioRequest;
import com.example.conectaaivrs.domain.auth.dto.InteresseResponse;
import com.example.conectaaivrs.domain.auth.dto.UsuarioResponse;
import com.example.conectaaivrs.domain.interesse.Interesse;
import com.example.conectaaivrs.domain.interesse.InteresseRepository;
import com.example.conectaaivrs.domain.refreshToken.RefreshTokenRepository;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.domain.usuario.UsuarioRepository;
import com.example.conectaaivrs.domain.usuario.UsuarioStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InteresseRepository interesseRepository;

    public UsuarioResponse buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return UsuarioResponse.fromEntity(usuario);
    }

    public UsuarioResponse atualizar(Usuario usuarioLogado, AtualizarUsuarioRequest request) {
        if (request.nome() != null) usuarioLogado.setNome(request.nome());
        if (request.email() != null) {
            if (usuarioRepository.findByEmail(request.email()).isPresent()
                    && !usuarioLogado.getEmail().equals(request.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
            }
            usuarioLogado.setEmail(request.email());
        }
        if (request.senha() != null) usuarioLogado.setSenha(passwordEncoder.encode(request.senha()));
        if (request.dataNascimento() != null) usuarioLogado.setDataNascimento(request.dataNascimento());
        if (request.genero() != null) usuarioLogado.setGenero(request.genero());
        if (request.bio() != null) usuarioLogado.setBio(request.bio());
        if (request.cidade() != null) usuarioLogado.setCidade(request.cidade());
        if (request.estado() != null) usuarioLogado.setEstado(request.estado());

        usuarioRepository.save(usuarioLogado);
        return UsuarioResponse.fromEntity(usuarioLogado);
    }

    public UsuarioResponse atualizarFoto(Usuario usuarioLogado, AtualizarFotoRequest request) {
        usuarioLogado.setFotoPerfil(request.fotoPerfil());
        usuarioRepository.save(usuarioLogado);
        return UsuarioResponse.fromEntity(usuarioLogado);
    }

    public void deletar(Usuario usuarioLogado) {
        refreshTokenRepository.findAllByUsuarioId(usuarioLogado.getId())
                .forEach(t -> t.setRevogado(true));
        usuarioLogado.setStatus(UsuarioStatus.INATIVO);
        usuarioRepository.save(usuarioLogado);
    }

    public List<InteresseResponse> listarInteresses(Usuario usuarioLogado) {
        return usuarioLogado.getInteresses().stream()
                .map(InteresseResponse::fromEntity)
                .toList();
    }

    @Transactional
    public List<InteresseResponse> atualizarInteresses(Usuario usuarioLogado, List<String> nomes) {
        List<Interesse> interesses = interesseRepository.findByNomeIn(nomes);
        if (interesses.size() != nomes.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um ou mais interesses não encontrados");
        }
        usuarioLogado.setInteresses(interesses);
        usuarioRepository.save(usuarioLogado);
        return interesses.stream()
                .map(InteresseResponse::fromEntity)
                .toList();
    }
}
