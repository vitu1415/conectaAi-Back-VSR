CREATE TABLE curtidas
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    post_id UUID NOT NULL,

    usuario_id UUID NOT NULL,

    criado_em TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_curtida_post
        FOREIGN KEY(post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_curtida_usuario
        FOREIGN KEY(usuario_id)
            REFERENCES usuarios(id)
            ON DELETE CASCADE,

    CONSTRAINT uk_curtida
        UNIQUE(post_id, usuario_id)
);

CREATE INDEX idx_curtida_post
    ON curtidas(post_id);

CREATE INDEX idx_curtida_usuario
    ON curtidas(usuario_id);