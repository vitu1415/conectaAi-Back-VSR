CREATE TABLE agendas
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    evento_id UUID NOT NULL,

    titulo VARCHAR(200) NOT NULL,

    descricao TEXT,

    categoria VARCHAR(100) NOT NULL,

    local VARCHAR(200),

    inicio TIMESTAMP NOT NULL,

    fim TIMESTAMP NOT NULL,

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_agendas_evento
        FOREIGN KEY (evento_id)
            REFERENCES eventos(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_agendas_evento
    ON agendas(evento_id);

CREATE INDEX idx_agendas_inicio
    ON agendas(inicio);

CREATE INDEX idx_agendas_evento_inicio
    ON agendas(evento_id, inicio);