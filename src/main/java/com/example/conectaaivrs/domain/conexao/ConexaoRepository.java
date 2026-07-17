package com.example.conectaaivrs.domain.conexao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConexaoRepository extends JpaRepository<Conexao, UUID> {

    Optional<Conexao> findByUsuarioOrigemIdAndUsuarioDestinoId(UUID origemId, UUID destinoId);

    List<Conexao> findAllByUsuarioDestinoIdAndStatus(UUID destinoId, ConexaoStatus status);

    List<Conexao> findAllByUsuarioOrigemIdAndStatus(UUID origemId, ConexaoStatus status);

    List<Conexao> findAllByUsuarioOrigemIdOrUsuarioDestinoIdAndStatus(
            UUID origemId, UUID destinoId, ConexaoStatus status);
}
