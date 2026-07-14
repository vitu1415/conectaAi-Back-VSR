CREATE TABLE usuarios
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    nome VARCHAR(120) NOT NULL,

    email VARCHAR(150) NOT NULL UNIQUE,

    senha TEXT,

    provider VARCHAR(50),

    provider_id VARCHAR(150),

    data_nascimento DATE,

    genero genero_enum,

    bio TEXT,

    foto_perfil TEXT,

    cidade VARCHAR(120),

    estado VARCHAR(120),

    status usuario_status_enum DEFAULT 'ATIVO',

    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    ultimo_login TIMESTAMP
);

CREATE INDEX idx_usuario_email
    ON usuarios(email);

CREATE INDEX idx_usuario_status
    ON usuarios(status);