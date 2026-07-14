CREATE TABLE conexoes
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    usuario_origem UUID NOT NULL,

    usuario_destino UUID NOT NULL,

    status conexao_status_enum NOT NULL DEFAULT 'PENDENTE',

    origem VARCHAR(50),

    criado_em TIMESTAMP DEFAULT NOW(),

    atualizado_em TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_conexao_origem
        FOREIGN KEY(usuario_origem)
            REFERENCES usuarios(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_conexao_destino
        FOREIGN KEY(usuario_destino)
            REFERENCES usuarios(id)
            ON DELETE CASCADE,

    CONSTRAINT uk_conexao
        UNIQUE(usuario_origem, usuario_destino),

    CONSTRAINT ck_conexao_diferente
        CHECK(usuario_origem <> usuario_destino)
);

CREATE INDEX idx_conexao_origem
    ON conexoes(usuario_origem);

CREATE INDEX idx_conexao_destino
    ON conexoes(usuario_destino);

CREATE INDEX idx_conexao_status
    ON conexoes(status);