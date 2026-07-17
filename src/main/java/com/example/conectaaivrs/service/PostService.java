package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.evento.dto.EventoResponse;
import com.example.conectaaivrs.domain.post.dto.FeedEventoResponse;
import com.example.conectaaivrs.domain.post.dto.PostRequest;
import com.example.conectaaivrs.domain.post.dto.PostResponse;
import com.example.conectaaivrs.domain.evento.Evento;
import com.example.conectaaivrs.domain.evento.EventoRepository;
import com.example.conectaaivrs.domain.inscricao.ParticipanteEventoRepository;
import com.example.conectaaivrs.domain.post.Post;
import com.example.conectaaivrs.domain.post.PostRepository;
import com.example.conectaaivrs.domain.post.TipoPost;
import com.example.conectaaivrs.domain.post.VisibilidadePost;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipanteEventoRepository participanteRepository;

    public List<FeedEventoResponse> feed(UUID usuarioLogado) {
        List<Post> posts = postRepository.findFeedByUsuarioId(usuarioLogado);

        Map<UUID, List<PostResponse>> postsPorEvento = posts.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEvento().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(PostResponse::fromEntity, Collectors.toList())
                ));

        return postsPorEvento.entrySet().stream()
                .map(entry -> {
                    Evento evento = posts.stream()
                            .filter(p -> p.getEvento().getId().equals(entry.getKey()))
                            .findFirst()
                            .get().getEvento();
                    return new FeedEventoResponse(
                            EventoResponse.fromEntity(evento),
                            entry.getValue()
                    );
                })
                .toList();
    }

    public List<PostResponse> listarPorEvento(UUID eventoId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado");
        }
        return postRepository.findAllByEventoIdOrderByCriadoEmDesc(eventoId)
                .stream()
                .map(PostResponse::fromEntity)
                .toList();
    }

    public List<PostResponse> listarPorUsuario(UUID usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        return postRepository.findAllByAutorIdOrderByCriadoEmDesc(usuarioId)
                .stream()
                .map(PostResponse::fromEntity)
                .toList();
    }

    public PostResponse buscarPorId(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));
        return PostResponse.fromEntity(post);
    }

    public PostResponse criar(Usuario autor, PostRequest request) {
        Evento evento = eventoRepository.findById(request.eventoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));

        boolean isParticipante = participanteRepository
                .existsByEventoIdAndUsuarioId(evento.getId(), autor.getId());
        if (!isParticipante) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas participantes do evento podem postar");
        }

        Post post = Post.builder()
                .evento(evento)
                .autor(autor)
                .texto(request.texto())
                .imagemUrl(request.imagemUrl())
                .tipo(request.tipo() != null ? request.tipo() : TipoPost.TEXTO)
                .visibilidade(request.visibilidade() != null ? request.visibilidade() : VisibilidadePost.PUBLICO)
                .build();

        postRepository.save(post);
        return PostResponse.fromEntity(post);
    }

    public PostResponse atualizar(Usuario usuarioLogado, UUID postId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        if (!post.getAutor().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o autor pode editar o post");
        }

        post.setTexto(request.texto());
        post.setImagemUrl(request.imagemUrl());
        if (request.tipo() != null) post.setTipo(request.tipo());
        if (request.visibilidade() != null) post.setVisibilidade(request.visibilidade());

        postRepository.save(post);
        return PostResponse.fromEntity(post);
    }

    public void deletar(Usuario usuarioLogado, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        if (!post.getAutor().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o autor pode excluir o post");
        }

        postRepository.delete(post);
    }
}
