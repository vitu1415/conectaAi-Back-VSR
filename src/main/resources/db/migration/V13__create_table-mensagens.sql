CREATE TABLE mensagens
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    topico_id UUID NOT NULL,

    autor_id UUID NOT NULL,

    texto TEXT NOT NULL,

    criado_em TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_mensagem_topico
        FOREIGN KEY(topico_id)
            REFERENCES salas(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_mensagem_autor
        FOREIGN KEY(autor_id)
            REFERENCES usuarios(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_mensagem_topico
    ON mensagens(topico_id);

CREATE INDEX idx_mensagem_data
    ON mensagens(criado_em);

CREATE INDEX idx_mensagem_autor
    ON mensagens(autor_id);