package com.example.conectaaivrs.domain.comentario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComentarioRepository extends JpaRepository<Comentario, UUID> {
    List<Comentario> findAllByPostIdOrderByCriadoEmAsc(UUID postId);
}
