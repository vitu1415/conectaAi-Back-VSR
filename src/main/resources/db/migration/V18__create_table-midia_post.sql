CREATE TYPE midia_tipo_enum AS ENUM ('IMAGEM', 'VIDEO');

CREATE TABLE midia_post
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    post_id UUID NOT NULL,

    url TEXT NOT NULL,

    nome_arquivo TEXT NOT NULL,

    content_type TEXT NOT NULL,

    tipo midia_tipo_enum NOT NULL,

    ordem INT NOT NULL DEFAULT 0,

    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_midia_post
        FOREIGN KEY (post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_midia_post_post
    ON midia_post(post_id);

ALTER TABLE posts ALTER COLUMN texto DROP NOT NULL;
