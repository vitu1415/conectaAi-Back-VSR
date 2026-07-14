CREATE TABLE comentarios
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    post_id UUID NOT NULL,

    usuario_id UUID NOT NULL,

    comentario_pai UUID,

    texto TEXT NOT NULL,

    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_comentario_post
        FOREIGN KEY(post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_comentario_usuario
        FOREIGN KEY(usuario_id)
            REFERENCES usuarios(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_comentario_pai
        FOREIGN KEY(comentario_pai)
            REFERENCES comentarios(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_comentario_post
    ON comentarios(post_id);

CREATE INDEX idx_comentario_usuario
    ON comentarios(usuario_id);

CREATE INDEX idx_comentario_pai
    ON comentarios(comentario_pai);