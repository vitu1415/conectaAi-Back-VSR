package com.example.conectaaivrs.domain.agenda;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgendaRepository extends JpaRepository<Agenda, UUID> {
    List<Agenda> findAllByEventoIdOrderByInicioAsc(UUID eventoId);
}
