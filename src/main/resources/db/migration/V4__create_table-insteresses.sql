CREATE TABLE interesses
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    nome VARCHAR(80) NOT NULL UNIQUE,

    categoria VARCHAR(80),

    icone VARCHAR(120)
);

CREATE TABLE usuario_interesse
(
    usuario_id UUID NOT NULL,

    interesse_id UUID NOT NULL,

    PRIMARY KEY(usuario_id, interesse_id),

    CONSTRAINT fk_usuario_interesse_usuario
        FOREIGN KEY(usuario_id)
            REFERENCES usuarios(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_usuario_interesse_interesse
        FOREIGN KEY(interesse_id)
            REFERENCES interesses(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_usuario_interesse_usuario
    ON usuario_interesse(usuario_id);

CREATE INDEX idx_usuario_interesse_interesse
    ON usuario_interesse(interesse_id);