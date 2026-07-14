CREATE TABLE canais
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    evento_id UUID NOT NULL,

    nome VARCHAR(100) NOT NULL,

    descricao TEXT,

    ordem INTEGER DEFAULT 0,

    ativo BOOLEAN DEFAULT TRUE,

    criado_em TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_canal_evento
        FOREIGN KEY(evento_id)
            REFERENCES eventos(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_canal_evento
    ON canais(evento_id);