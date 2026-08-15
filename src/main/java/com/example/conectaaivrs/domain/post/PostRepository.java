package com.example.conectaaivrs.domain.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByEventoIdOrderByCriadoEmDesc(UUID eventoId);
    List<Post> findAllByAutorIdOrderByCriadoEmDesc(UUID autorId);

    @Query("""
            SELECT p FROM Post p
            WHERE p.evento.id IN (
                SELECT pe.evento.id FROM ParticipanteEvento pe
                WHERE pe.usuario.id = :usuarioId
            )
            ORDER BY p.criadoEm DESC
            """)
    List<Post> findFeedByUsuarioId(@Param("usuarioId") UUID usuarioId);

    @Query("""
            SELECT p FROM Post p
            WHERE p.evento.id = :eventoId
              AND (p.criadoEm < :cursorData OR (p.criadoEm = :cursorData AND p.id < :cursorId))
            ORDER BY p.criadoEm DESC, p.id DESC
            """)
    List<Post> findPaginaPorEvento(@Param("eventoId") UUID eventoId,
                                   @Param("cursorData") LocalDateTime cursorData,
                                   @Param("cursorId") UUID cursorId,
                                   Pageable pageable);

    @Query("""
            SELECT p FROM Post p
            WHERE p.autor.id = :autorId
              AND (p.criadoEm < :cursorData OR (p.criadoEm = :cursorData AND p.id < :cursorId))
            ORDER BY p.criadoEm DESC, p.id DESC
            """)
    List<Post> findPaginaPorUsuario(@Param("autorId") UUID autorId,
                                    @Param("cursorData") LocalDateTime cursorData,
                                    @Param("cursorId") UUID cursorId,
                                    Pageable pageable);

    @Query("""
            SELECT p FROM Post p
            WHERE p.evento.id IN (
                SELECT pe.evento.id FROM ParticipanteEvento pe
                WHERE pe.usuario.id = :usuarioId
            )
              AND (p.criadoEm < :cursorData OR (p.criadoEm = :cursorData AND p.id < :cursorId))
            ORDER BY p.criadoEm DESC, p.id DESC
            """)
    List<Post> findPaginaFeed(@Param("usuarioId") UUID usuarioId,
                              @Param("cursorData") LocalDateTime cursorData,
                              @Param("cursorId") UUID cursorId,
                              Pageable pageable);
}