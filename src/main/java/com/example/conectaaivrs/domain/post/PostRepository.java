package com.example.conectaaivrs.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
