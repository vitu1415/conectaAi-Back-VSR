package com.example.conectaaivrs.domain.inscricao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipanteEventoRepository extends JpaRepository<ParticipanteEvento, UUID> {
    Optional<ParticipanteEvento> findByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);
    List<ParticipanteEvento> findAllByEventoIdOrderByDataInscricaoAsc(UUID eventoId);
    List<ParticipanteEvento> findAllByUsuarioIdOrderByDataInscricaoDesc(UUID usuarioId);
    boolean existsByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);
    long countByEventoId(UUID eventoId);
}
