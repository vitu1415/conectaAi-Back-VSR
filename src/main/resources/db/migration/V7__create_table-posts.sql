CREATE TABLE posts
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    evento_id UUID NOT NULL,

    autor_id UUID NOT NULL,

    texto TEXT NOT NULL,

    imagem_url TEXT,

    tipo tipo_post_enum NOT NULL DEFAULT 'TEXTO',

    visibilidade visibilidade_post_enum NOT NULL DEFAULT 'PARTICIPANTES',

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_post_evento
        FOREIGN KEY (evento_id)
            REFERENCES eventos(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_post_autor
        FOREIGN KEY (autor_id)
            REFERENCES usuarios(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_posts_evento
    ON posts(evento_id);

CREATE INDEX idx_posts_autor
    ON posts(autor_id);

CREATE INDEX idx_posts_data
    ON posts(criado_em DESC);