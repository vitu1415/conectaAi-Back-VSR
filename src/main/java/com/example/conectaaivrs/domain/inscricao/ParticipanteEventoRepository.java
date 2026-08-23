package com.example.conectaaivrs.domain.inscricao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipanteEventoRepository extends JpaRepository<ParticipanteEvento, UUID> {
    Optional<ParticipanteEvento> findByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);
    List<ParticipanteEvento> findAllByEventoIdOrderByDataInscricaoAsc(UUID eventoId);

    @Query("""
            SELECT p FROM ParticipanteEvento p
            WHERE p.evento.id = :eventoId
              AND (p.dataInscricao > :cursorData OR (p.dataInscricao = :cursorData AND p.id > :cursorId))
            ORDER BY p.dataInscricao ASC, p.id ASC
            """)
    List<ParticipanteEvento> findPaginaPorEvento(@Param("eventoId") UUID eventoId,
                                                 @Param("cursorData") LocalDateTime cursorData,
                                                 @Param("cursorId") UUID cursorId,
                                                 Pageable pageable);
    List<ParticipanteEvento> findAllByUsuarioIdOrderByDataInscricaoDesc(UUID usuarioId);
    boolean existsByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);
    long countByEventoId(UUID eventoId);
}
