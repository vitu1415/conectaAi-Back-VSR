ALTER TYPE visibilidade_post_enum RENAME TO visibilidade_post_enum_old;

CREATE TYPE visibilidade_post_enum AS ENUM ('PUBLICO', 'PRIVADO');

ALTER TABLE posts
    ALTER COLUMN visibilidade DROP DEFAULT,
    ALTER COLUMN visibilidade TYPE visibilidade_post_enum
        USING (CASE WHEN visibilidade::text = 'PARTICIPANTES' THEN 'PRIVADO'::visibilidade_post_enum
                    ELSE visibilidade::text::visibilidade_post_enum END),
    ALTER COLUMN visibilidade SET DEFAULT 'PUBLICO';

DROP TYPE visibilidade_post_enum_old;