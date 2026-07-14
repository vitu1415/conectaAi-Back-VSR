CREATE TABLE refresh_tokens
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    usuario_id UUID NOT NULL,

    token TEXT NOT NULL,

    expira_em TIMESTAMP NOT NULL,

    revogado BOOLEAN NOT NULL DEFAULT FALSE,

    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_refresh_usuario
        FOREIGN KEY(usuario_id)
            REFERENCES usuarios(id)
            ON DELETE CASCADE
);