package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.conexao.dto.ConexaoResponse;
import com.example.conectaaivrs.domain.conexao.dto.RelacionamentoResponse;
import com.example.conectaaivrs.domain.conexao.Conexao;
import com.example.conectaaivrs.domain.conexao.ConexaoRepository;
import com.example.conectaaivrs.domain.conexao.ConexaoStatus;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ConexaoService {

    @Autowired
    private ConexaoRepository conexaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public ConexaoResponse enviarSolicitacao(Usuario usuarioLogado, UUID destinoId) {
        if (usuarioLogado.getId().equals(destinoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível enviar solicitação para si mesmo");
        }

        Usuario destino = usuarioRepository.findById(destinoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        conexaoRepository.findByUsuarioOrigemIdAndUsuarioDestinoId(destinoId, usuarioLogado.getId())
                .ifPresent(c -> {
                    if (c.getStatus() == ConexaoStatus.PENDENTE) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Este usuário já enviou uma solicitação para você");
                    }
                    if (c.getStatus() == ConexaoStatus.ACEITA) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Vocês já estão conectados");
                    }
                });

        conexaoRepository.findByUsuarioOrigemIdAndUsuarioDestinoId(usuarioLogado.getId(), destinoId)
                .ifPresent(c -> {
                    if (c.getStatus() == ConexaoStatus.PENDENTE) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação já enviada");
                    }
                    if (c.getStatus() == ConexaoStatus.ACEITA) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Vocês já estão conectados");
                    }
                    if (c.getStatus() == ConexaoStatus.RECUSADA) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação recusada anteriormente");
                    }
                });

        Conexao conexao = Conexao.builder()
                .usuarioOrigem(usuarioLogado)
                .usuarioDestino(destino)
                .status(ConexaoStatus.PENDENTE)
                .build();

        conexaoRepository.save(conexao);
        return ConexaoResponse.fromEntity(conexao);
    }

    public List<ConexaoResponse> listarRecebidas(Usuario usuarioLogado) {
        return conexaoRepository.findAllByUsuarioDestinoIdAndStatus(
                        usuarioLogado.getId(), ConexaoStatus.PENDENTE)
                .stream()
                .map(c -> ConexaoResponse.fromEntity(c))
                .toList();
    }

    public List<ConexaoResponse> listarEnviadas(Usuario usuarioLogado) {
        return conexaoRepository.findAllByUsuarioOrigemIdAndStatus(
                        usuarioLogado.getId(), ConexaoStatus.PENDENTE)
                .stream()
                .map(c -> ConexaoResponse.fromEntity(c))
                .toList();
    }

    public ConexaoResponse aceitar(Usuario usuarioLogado, UUID conexaoId) {
        Conexao conexao = conexaoRepository.findById(conexaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conexão não encontrada"));

        if (!conexao.getUsuarioDestino().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o destinatário pode aceitar a solicitação");
        }
        if (conexao.getStatus() != ConexaoStatus.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solicitação não está mais pendente");
        }

        conexao.setStatus(ConexaoStatus.ACEITA);
        conexaoRepository.save(conexao);
        return ConexaoResponse.fromEntity(conexao);
    }

    public ConexaoResponse recusar(Usuario usuarioLogado, UUID conexaoId) {
        Conexao conexao = conexaoRepository.findById(conexaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conexão não encontrada"));

        if (!conexao.getUsuarioDestino().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o destinatário pode recusar a solicitação");
        }
        if (conexao.getStatus() != ConexaoStatus.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solicitação não está mais pendente");
        }

        conexao.setStatus(ConexaoStatus.RECUSADA);
        conexaoRepository.save(conexao);
        return ConexaoResponse.fromEntity(conexao);
    }

    public void deletar(Usuario usuarioLogado, UUID conexaoId) {
        Conexao conexao = conexaoRepository.findById(conexaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conexão não encontrada"));

        boolean isOrigem = conexao.getUsuarioOrigem().getId().equals(usuarioLogado.getId());
        boolean isDestino = conexao.getUsuarioDestino().getId().equals(usuarioLogado.getId());

        if (!isOrigem && !isDestino) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não faz parte desta conexão");
        }

        conexaoRepository.delete(conexao);
    }

    public RelacionamentoResponse consultarRelacionamento(Usuario usuarioLogado, UUID outroId) {
        return conexaoRepository.findByUsuarioOrigemIdAndUsuarioDestinoId(usuarioLogado.getId(), outroId)
                .map(c -> new RelacionamentoResponse(c.getStatus()))
                .orElseGet(() -> conexaoRepository
                        .findByUsuarioOrigemIdAndUsuarioDestinoId(outroId, usuarioLogado.getId())
                        .map(c -> new RelacionamentoResponse(c.getStatus()))
                        .orElse(new RelacionamentoResponse(null)));
    }

    public List<ConexaoResponse> listarConexoes(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        List<Conexao> comoOrigem = conexaoRepository
                .findAllByUsuarioOrigemIdAndStatus(usuarioId, ConexaoStatus.ACEITA);
        List<Conexao> comoDestino = conexaoRepository
                .findAllByUsuarioDestinoIdAndStatus(usuarioId, ConexaoStatus.ACEITA);

        return java.util.stream.Stream.concat(comoOrigem.stream(), comoDestino.stream())
                .map(ConexaoResponse::fromEntity)
                .toList();
    }
}
