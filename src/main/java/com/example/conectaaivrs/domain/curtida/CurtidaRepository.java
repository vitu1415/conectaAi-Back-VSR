package com.example.conectaaivrs.domain.curtida;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CurtidaRepository extends JpaRepository<Curtida, UUID> {
    boolean existsByPostIdAndUsuarioId(UUID postId, UUID usuarioId);
    Optional<Curtida> findByPostIdAndUsuarioId(UUID postId, UUID usuarioId);
    int countByPostId(UUID postId);
    void deleteByPostIdAndUsuarioId(UUID postId, UUID usuarioId);
}
