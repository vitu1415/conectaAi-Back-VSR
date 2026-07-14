CREATE TABLE eventos
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organizador_id UUID NOT NULL,

    nome VARCHAR(150) NOT NULL,

    descricao TEXT,

    banner TEXT,

    cidade VARCHAR(100),

    estado VARCHAR(100),

    endereco TEXT,

    latitude NUMERIC(9,6),

    longitude NUMERIC(9,6),

    inicio TIMESTAMP NOT NULL,

    fim TIMESTAMP NOT NULL,

    capacidade INTEGER,

    privado BOOLEAN DEFAULT FALSE,

    status evento_status_enum DEFAULT 'PUBLICADO',

    criado_em TIMESTAMP DEFAULT NOW(),

    atualizado_em TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_evento_organizador
        FOREIGN KEY (organizador_id)
            REFERENCES usuarios(id)
);

CREATE INDEX idx_evento_inicio
    ON eventos(inicio);

CREATE INDEX idx_evento_status
    ON eventos(status);

CREATE INDEX idx_evento_cidade
    ON eventos(cidade);