package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.comentario.Comentario;
import com.example.conectaaivrs.domain.comentario.ComentarioRepository;
import com.example.conectaaivrs.domain.comentario.dto.ComentarioRequest;
import com.example.conectaaivrs.domain.comentario.dto.ComentarioResponse;
import com.example.conectaaivrs.domain.evento.EventoRepository;
import com.example.conectaaivrs.domain.inscricao.ParticipanteEventoRepository;
import com.example.conectaaivrs.domain.post.Post;
import com.example.conectaaivrs.domain.post.PostRepository;
import com.example.conectaaivrs.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ParticipanteEventoRepository participanteRepository;

    public List<ComentarioResponse> listar(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado");
        }
        return comentarioRepository.findAllByPostIdOrderByCriadoEmAsc(postId)
                .stream()
                .map(ComentarioResponse::fromEntity)
                .toList();
    }

    public ComentarioResponse criar(Usuario autor, UUID postId, ComentarioRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        boolean isParticipante = participanteRepository
                .existsByEventoIdAndUsuarioId(post.getEvento().getId(), autor.getId());
        if (!isParticipante) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas participantes do evento podem comentar");
        }

        Comentario.ComentarioBuilder builder = Comentario.builder()
                .post(post)
                .usuario(autor)
                .texto(request.texto());

        if (request.comentarioPaiId() != null) {
            Comentario pai = comentarioRepository.findById(request.comentarioPaiId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário pai não encontrado"));
            builder.comentarioPai(pai);
        }

        Comentario comentario = builder.build();
        comentarioRepository.save(comentario);
        return ComentarioResponse.fromEntity(comentario);
    }

    public ComentarioResponse atualizar(Usuario usuarioLogado, UUID comentarioId, ComentarioRequest request) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        if (!comentario.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o autor pode editar o comentário");
        }

        comentario.setTexto(request.texto());
        comentarioRepository.save(comentario);
        return ComentarioResponse.fromEntity(comentario);
    }

    public void deletar(Usuario usuarioLogado, UUID comentarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        if (!comentario.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o autor pode excluir o comentário");
        }

        comentarioRepository.delete(comentario);
    }
}
