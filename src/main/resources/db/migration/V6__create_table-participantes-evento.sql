CREATE TABLE participantes_evento
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    evento_id UUID NOT NULL,

    usuario_id UUID NOT NULL,

    papel participante_papel_enum DEFAULT 'PARTICIPANTE',

    status participante_status_enum DEFAULT 'INSCRITO',

    checkin BOOLEAN DEFAULT FALSE,

    data_inscricao TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_participante_evento
        FOREIGN KEY(evento_id)
            REFERENCES eventos(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_participante_usuario
        FOREIGN KEY(usuario_id)
            REFERENCES usuarios(id)
            ON DELETE CASCADE,

    CONSTRAINT uk_usuario_evento
        UNIQUE(usuario_id, evento_id)
);

CREATE INDEX idx_participante_evento
    ON participantes_evento(evento_id);

CREATE INDEX idx_participante_usuario
    ON participantes_evento(usuario_id);