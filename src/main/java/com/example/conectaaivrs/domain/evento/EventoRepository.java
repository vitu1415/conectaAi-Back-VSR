package com.example.conectaaivrs.domain.evento;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventoRepository extends JpaRepository<Evento, UUID> {

    @Query("""
            SELECT e FROM Evento e
            WHERE e.criadoEm < :cursorData OR (e.criadoEm = :cursorData AND e.id < :cursorId)
            ORDER BY e.criadoEm DESC, e.id DESC
            """)
    List<Evento> findPaginaTodos(@Param("cursorData") LocalDateTime cursorData,
                                 @Param("cursorId") UUID cursorId,
                                 Pageable pageable);

    @Query("""
            SELECT e FROM Evento e
            WHERE e.status = :status
              AND (e.criadoEm < :cursorData OR (e.criadoEm = :cursorData AND e.id < :cursorId))
            ORDER BY e.criadoEm DESC, e.id DESC
            """)
    List<Evento> findPaginaPorStatus(@Param("status") EventoStatus status,
                                     @Param("cursorData") LocalDateTime cursorData,
                                     @Param("cursorId") UUID cursorId,
                                     Pageable pageable);

    @Query("""
            SELECT e FROM Evento e
            WHERE e.inicio > :agora
              AND (e.inicio > :cursorData OR (e.inicio = :cursorData AND e.id > :cursorId))
            ORDER BY e.inicio ASC, e.id ASC
            """)
    List<Evento> findPaginaProximos(@Param("agora") LocalDateTime agora,
                                    @Param("cursorData") LocalDateTime cursorData,
                                    @Param("cursorId") UUID cursorId,
                                    Pageable pageable);

}