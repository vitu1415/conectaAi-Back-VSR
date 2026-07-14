package com.example.conectaaivrs.domain.interesse;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "interesses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class Interesse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 80, unique = true)
    private String nome;

    @Column(length = 80)
    private String categoria;

    @Column(length = 120)
    private String icone;
}
