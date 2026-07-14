package com.example.conectaaivrs.domain.interesse;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InteresseRepository extends JpaRepository<Interesse, UUID> {
    List<Interesse> findByNomeIn(List<String> nomes);
}
