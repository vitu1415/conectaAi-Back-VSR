package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.curtida.Curtida;
import com.example.conectaaivrs.domain.curtida.CurtidaRepository;
import com.example.conectaaivrs.domain.evento.dto.EventoResponse;
import com.example.conectaaivrs.domain.post.dto.FeedEventoResponse;
import com.example.conectaaivrs.domain.post.dto.PostRequest;
import com.example.conectaaivrs.domain.post.dto.PostResponse;
import com.example.conectaaivrs.domain.evento.Evento;
import com.example.conectaaivrs.domain.evento.EventoRepository;
import com.example.conectaaivrs.domain.inscricao.ParticipanteEventoRepository;
import com.example.conectaaivrs.domain.post.MidiaPost;
import com.example.conectaaivrs.domain.post.MidiaTipo;
import com.example.conectaaivrs.domain.post.Post;
import com.example.conectaaivrs.domain.post.PostRepository;
import com.example.conectaaivrs.domain.post.TipoPost;
import com.example.conectaaivrs.domain.post.VisibilidadePost;
import com.example.conectaaivrs.domain.storage.dto.UploadResponse;
import com.example.conectaaivrs.domain.usuario.Usuario;
import com.example.conectaaivrs.domain.usuario.UsuarioRepository;
import com.example.conectaaivrs.infra.paginacao.CursorInfo;
import com.example.conectaaivrs.infra.paginacao.PageResponse;
import com.example.conectaaivrs.infra.paginacao.PaginacaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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

    @Autowired
    private CurtidaRepository curtidaRepository;

    @Autowired
    private StorageService storageService;

    private static final int MAX_MIDIAS = 10;
    private static final Set<String> TIPOS_IMAGEM = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final Set<String> TIPOS_VIDEO = Set.of("video/mp4", "video/quicktime", "video/x-msvideo");
    private static final Set<String> CONTENT_TYPES_PERMITIDOS = new HashSet<>();
    static {
        CONTENT_TYPES_PERMITIDOS.addAll(TIPOS_IMAGEM);
        CONTENT_TYPES_PERMITIDOS.addAll(TIPOS_VIDEO);
    }

    public PageResponse<FeedEventoResponse> feed(UUID usuarioLogado, UUID cursorId, LocalDateTime cursorData, Integer limite) {
        int limit = PaginacaoHelper.normalizarLimite(limite);
        UUID id = cursorId != null ? cursorId : PaginacaoHelper.INICIO_DESC_ID;
        LocalDateTime data = cursorData != null ? cursorData : PaginacaoHelper.INICIO_DESC_DATA;
        List<Post> posts = postRepository.findPaginaFeed(usuarioLogado, data, id, PageRequest.of(0, limit + 1));

        boolean hasNext = posts.size() > limit;
        List<Post> pagePosts = hasNext ? posts.subList(0, limit) : posts;

        Map<UUID, List<PostResponse>> postsPorEvento = pagePosts.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEvento().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                p -> PostResponse.fromEntity(
                                        p,
                                        curtidaRepository.countByPostId(p.getId()),
                                        curtidaRepository.existsByPostIdAndUsuarioId(p.getId(), usuarioLogado)
                                ),
                                Collectors.toList()
                        )
                ));

        List<FeedEventoResponse> content = postsPorEvento.entrySet().stream()
                .map(entry -> {
                    Evento evento = pagePosts.stream()
                            .filter(p -> p.getEvento().getId().equals(entry.getKey()))
                            .findFirst()
                            .get().getEvento();
                    return new FeedEventoResponse(
                            EventoResponse.fromEntity(evento),
                            entry.getValue()
                    );
                })
                .toList();

        CursorInfo nextCursor = null;
        if (hasNext && !pagePosts.isEmpty()) {
            Post ultimo = pagePosts.get(pagePosts.size() - 1);
            nextCursor = new CursorInfo(ultimo.getId(), ultimo.getCriadoEm());
        }

        return new PageResponse<>(content, nextCursor, hasNext);
    }

    public PageResponse<PostResponse> listarPorEvento(UUID eventoId, UUID cursorId, LocalDateTime cursorData, Integer limite) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado");
        }
        int limit = PaginacaoHelper.normalizarLimite(limite);
        UUID id = cursorId != null ? cursorId : PaginacaoHelper.INICIO_DESC_ID;
        LocalDateTime data = cursorData != null ? cursorData : PaginacaoHelper.INICIO_DESC_DATA;
        List<Post> posts = postRepository.findPaginaPorEvento(eventoId, data, id, PageRequest.of(0, limit + 1));
        return PaginacaoHelper.montar(
                posts.stream().map(p -> PostResponse.fromEntity(
                        p,
                        curtidaRepository.countByPostId(p.getId()),
                        false
                )).toList(),
                limit,
                p -> new CursorInfo(p.id(), p.criadoEm())
        );
    }

    public PageResponse<PostResponse> listarPorUsuario(UUID usuarioId, UUID usuarioLogadoId, UUID cursorId, LocalDateTime cursorData, Integer limite) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        int limit = PaginacaoHelper.normalizarLimite(limite);
        UUID id = cursorId != null ? cursorId : PaginacaoHelper.INICIO_DESC_ID;
        LocalDateTime data = cursorData != null ? cursorData : PaginacaoHelper.INICIO_DESC_DATA;
        List<Post> posts = postRepository.findPaginaPorUsuario(usuarioId, data, id, PageRequest.of(0, limit + 1));
        return PaginacaoHelper.montar(
                posts.stream().map(p -> PostResponse.fromEntity(
                        p,
                        curtidaRepository.countByPostId(p.getId()),
                        curtidaRepository.existsByPostIdAndUsuarioId(p.getId(), usuarioLogadoId)
                )).toList(),
                limit,
                p -> new CursorInfo(p.id(), p.criadoEm())
        );
    }

    public PostResponse buscarPorId(UUID postId, UUID usuarioLogadoId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));
        return PostResponse.fromEntity(
                post,
                curtidaRepository.countByPostId(post.getId()),
                curtidaRepository.existsByPostIdAndUsuarioId(post.getId(), usuarioLogadoId)
        );
    }

    @Transactional
    public PostResponse criar(Usuario autor, PostRequest request, List<MultipartFile> midias) {
        if (request.eventoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eventoId é obrigatório");
        }

        Evento evento = eventoRepository.findById(request.eventoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));

        boolean isParticipante = participanteRepository
                .existsByEventoIdAndUsuarioId(evento.getId(), autor.getId());
        if (!isParticipante) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas participantes do evento podem postar");
        }

        if ((midias == null || midias.isEmpty()) && (request.texto() == null || request.texto().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O post deve ter texto e/ou mídias");
        }

        Post post = Post.builder()
                .evento(evento)
                .autor(autor)
                .texto(request.texto())
                .tipo(request.tipo() != null ? request.tipo() : TipoPost.TEXTO)
                .visibilidade(request.visibilidade() != null ? request.visibilidade() : VisibilidadePost.PUBLICO)
                .build();

        if (midias != null && !midias.isEmpty()) {
            validarMidias(midias);
            post.setMidias(new ArrayList<>());
            for (int i = 0; i < midias.size(); i++) {
                MultipartFile arquivo = midias.get(i);
                UploadResponse upload = storageService.uploadMidiaPost(arquivo);
                MidiaTipo tipoMidia = TIPOS_IMAGEM.contains(arquivo.getContentType()) ? MidiaTipo.IMAGEM : MidiaTipo.VIDEO;
                MidiaPost midia = MidiaPost.builder()
                        .post(post)
                        .url(upload.url())
                        .nomeArquivo(upload.nomeArquivo())
                        .contentType(upload.contentType())
                        .tipo(tipoMidia)
                        .ordem(i)
                        .build();
                post.getMidias().add(midia);
            }
            post.setTipo(definirTipoPost(midias));
        }

        postRepository.save(post);
        return PostResponse.fromEntity(post, 0, false);
    }

    @Transactional
    public PostResponse atualizar(Usuario usuarioLogado, UUID postId, PostRequest request, List<MultipartFile> midias) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        if (!post.getAutor().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o autor pode editar o post");
        }

        post.setTexto(request.texto());
        if (request.tipo() != null) post.setTipo(request.tipo());
        if (request.visibilidade() != null) post.setVisibilidade(request.visibilidade());

        if (midias != null) {
            validarMidias(midias);
            post.getMidias().clear();
            for (int i = 0; i < midias.size(); i++) {
                MultipartFile arquivo = midias.get(i);
                UploadResponse upload = storageService.uploadMidiaPost(arquivo);
                MidiaTipo tipoMidia = TIPOS_IMAGEM.contains(arquivo.getContentType()) ? MidiaTipo.IMAGEM : MidiaTipo.VIDEO;
                MidiaPost midia = MidiaPost.builder()
                        .post(post)
                        .url(upload.url())
                        .nomeArquivo(upload.nomeArquivo())
                        .contentType(upload.contentType())
                        .tipo(tipoMidia)
                        .ordem(i)
                        .build();
                post.getMidias().add(midia);
            }
            if (!midias.isEmpty()) {
                post.setTipo(definirTipoPost(midias));
            }
        }

        postRepository.save(post);
        return PostResponse.fromEntity(
                post,
                curtidaRepository.countByPostId(post.getId()),
                curtidaRepository.existsByPostIdAndUsuarioId(post.getId(), usuarioLogado.getId())
        );
    }

    public void deletar(Usuario usuarioLogado, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        if (!post.getAutor().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o autor pode excluir o post");
        }

        postRepository.delete(post);
    }

    public PostResponse curtir(Usuario usuario, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        boolean isParticipante = participanteRepository
                .existsByEventoIdAndUsuarioId(post.getEvento().getId(), usuario.getId());
        if (!isParticipante) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas participantes do evento podem curtir posts");
        }

        if (curtidaRepository.existsByPostIdAndUsuarioId(postId, usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já curtiu este post");
        }

        Curtida curtida = Curtida.builder()
                .post(post)
                .usuario(usuario)
                .build();
        curtidaRepository.save(curtida);

        return PostResponse.fromEntity(
                post,
                curtidaRepository.countByPostId(postId),
                true
        );
    }

    public PostResponse descurtir(Usuario usuario, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        Curtida curtida = curtidaRepository.findByPostIdAndUsuarioId(postId, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Você não curtiu este post"));

        curtidaRepository.delete(curtida);

        return PostResponse.fromEntity(
                post,
                curtidaRepository.countByPostId(postId),
                false
        );
    }

    private void validarMidias(List<MultipartFile> midias) {
        if (midias.size() > MAX_MIDIAS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O post pode ter no máximo " + MAX_MIDIAS + " mídias");
        }
        for (MultipartFile arquivo : midias) {
            if (arquivo.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo vazio não é permitido");
            }
            String contentType = arquivo.getContentType();
            if (contentType == null || !CONTENT_TYPES_PERMITIDOS.contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Tipo de arquivo não permitido: " + contentType
                                + ". Tipos aceitos: imagens (JPEG, PNG, GIF, WEBP) e vídeos (MP4, MOV, AVI)");
            }
        }
    }

    private TipoPost definirTipoPost(List<MultipartFile> midias) {
        boolean temVideo = midias.stream().anyMatch(m -> TIPOS_VIDEO.contains(m.getContentType()));
        if (temVideo) return TipoPost.VIDEO;
        boolean temImagem = midias.stream().anyMatch(m -> TIPOS_IMAGEM.contains(m.getContentType()));
        if (temImagem) return TipoPost.IMAGEM;
        return TipoPost.TEXTO;
    }
}
