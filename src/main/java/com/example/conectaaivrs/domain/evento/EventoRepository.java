package com.example.conectaaivrs.domain.evento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventoRepository extends JpaRepository<Evento, UUID> {
    List<Evento> findAllByOrderByCriadoEmDesc();
    List<Evento> findByInicioAfterOrderByInicioAsc(LocalDateTime data);
    List<Evento> findByStatusOrderByCriadoEmDesc(EventoStatus status);
}
