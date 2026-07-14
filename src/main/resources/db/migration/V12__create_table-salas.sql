CREATE TABLE salas
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    canal_id UUID NOT NULL,

    autor_id UUID NOT NULL,

    titulo VARCHAR(200) NOT NULL,

    descricao TEXT,

    fixado BOOLEAN DEFAULT FALSE,

    fechado BOOLEAN DEFAULT FALSE,

    limite INT DEFAULT 0,

    criado_em TIMESTAMP DEFAULT NOW(),

    atualizado_em TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_sala_canal
        FOREIGN KEY(canal_id)
            REFERENCES canais(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_sala_autor
        FOREIGN KEY(autor_id)
            REFERENCES usuarios(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_sala_canal
    ON salas(canal_id);

CREATE INDEX idx_sala_data
    ON salas(criado_em DESC);

CREATE INDEX idx_sala_fixado
    ON salas(fixado);