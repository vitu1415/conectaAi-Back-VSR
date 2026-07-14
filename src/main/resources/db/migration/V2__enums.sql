CREATE TYPE genero_enum AS ENUM (
    'MASCULINO',
    'FEMININO',
    'OUTRO',
    'PREFIRO_NAO_INFORMAR'
);

CREATE TYPE usuario_status_enum AS ENUM (
    'ATIVO',
    'INATIVO',
    'BLOQUEADO'
);

CREATE TYPE evento_status_enum AS ENUM (
    'RASCUNHO',
    'PUBLICADO',
    'ENCERRADO',
    'CANCELADO'
);

CREATE TYPE participante_status_enum AS ENUM (
    'INSCRITO',
    'CHECKIN',
    'SAIU'
);

CREATE TYPE participante_papel_enum AS ENUM (
    'PARTICIPANTE',
    'ORGANIZADOR',
    'STAFF',
    'PALESTRANTE'
);

CREATE TYPE tipo_post_enum AS ENUM (
    'TEXTO',
    'IMAGEM',
    'VIDEO'
);

CREATE TYPE visibilidade_post_enum AS ENUM (
    'PUBLICO',
    'PARTICIPANTES'
);

CREATE TYPE conexao_status_enum AS ENUM (
    'PENDENTE',
    'ACEITA',
    'RECUSADA',
    'BLOQUEADA'
);

CREATE TYPE conversa_tipo_enum AS ENUM (
    'PRIVADA',
    'GRUPO'
);

CREATE TYPE mensagem_tipo_enum AS ENUM (
    'TEXTO',
    'IMAGEM',
    'VIDEO',
    'ARQUIVO'
);

CREATE TYPE notificacao_tipo_enum AS ENUM (
    'CURTIDA',
    'COMENTARIO',
    'CONEXAO',
    'MENSAGEM',
    'EVENTO'
);