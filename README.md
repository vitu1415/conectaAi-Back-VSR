# ConectaAI - VRS API

API do ConectaAI (VRS), uma rede social focada em eventos. Construída com Spring Boot 4.1, Spring Security + JWT, JPA + Flyway, PostgreSQL e AWS S3.

## Sumário

- [Início rápido](#início-rápido)
- [Stack](#stack)
- [Autenticação](#autenticação)
- [Paginação por cursor](#paginação-por-cursor)
- [Endpoints](#endpoints)

---

## Início rápido

Pré-requisitos: JDK 21 e PostgreSQL rodando localmente em `localhost:5432`.

```bash
# configurar banco (conectaai-vrs) e credenciais em src/main/resources/application.properties
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## Stack

| Componente             | Tecnologia                                    |
|------------------------|-----------------------------------------------|
| Framework              | Spring Boot 4.1                               |
| Autenticação           | Spring Security + JWT (com.auth0/java-jwt)    |
| ORM                    | Spring Data JPA                               |
| Migrations             | Flyway                                        |
| Banco de dados         | PostgreSQL                                    |
| Upload de arquivos     | AWS S3 + CloudFront                           |
| Login social           | Google OAuth 2.0                              |
| Documentação           | SpringDoc OpenAPI (Swagger UI)                |
| Logging HTTP           | Zalando Logbook                               |
| Build                  | Maven (wrapper)                               |

---

## Autenticação

Todos os endpoints de leitura/escrita (exceto `/auth/**`) exigem o header `Authorization: Bearer <token>`.

### Login com e-mail/senha

```http
POST /auth/login
Content-Type: application/json

{ "email": "user@teste.com", "senha": "sua-senha" }
```

Resposta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "3b16ecd9-c3f1-4648-8f21-495174855d42"
}
```

### Endpoints de autenticação

| Método   | Rota                                | Descrição                                      |
|----------|-------------------------------------|-------------------------------------------------|
| `POST`   | `/auth/login`                       | Login com e-mail e senha                        |
| `POST`   | `/auth/register`                    | Registra usuário e retorna token                |
| `POST`   | `/auth/refresh-token`               | Renova token (via cookie `refreshToken`)        |
| `POST`   | `/auth/logout`                      | Revoga refresh token e limpa cookie             |
| `POST`   | `/auth/redefinir-senha`             | Solicita redefinição de senha                   |
| `GET`    | `/auth/me`                          | Retorna o perfil do usuário autenticado         |

### Login com Google

```http
GET /auth/login/google
```

Redireciona o browser para a tela de consentimento do Google. Após autorização, o callback em `/auth/login/google/autorizado` troca o código por um token JWT e redireciona para o frontend com o `accessToken` na URL.

Variáveis de ambiente necessárias:

```
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
```

### Refresh Token via Cookie

O refresh token é enviado automaticamente via cookie HTTP-only. Configurações em `application.properties`:

| Property                         | Padrão          | Descrição                        |
|----------------------------------|-----------------|----------------------------------|
| `app.security.cookie.name`       | `refreshToken`  | Nome do cookie                   |
| `app.security.cookie.max-age`    | `604800`        | TTL em segundos (7 dias)         |
| `app.security.cookie.secure`     | `true`          | Enviar apenas via HTTPS          |
| `app.security.cookie.same-site`  | `None`          | Políticas SameSite               |
| `app.security.cookie.path`       | `/auth`         | Path do cookie                   |

---

## Paginação por cursor

Todas as listagens usam **paginação por cursor (keyset)** em vez de paginação por número de página. Isso evita saltos/duplicações quando novos itens são inseridos entre requisições.

### Parâmetros de requisição

| Parâmetro     | Tipo           | Obrigatório | Padrão | Descrição                                                                 |
|---------------|----------------|-------------|--------|---------------------------------------------------------------------------|
| `cursorId`    | `UUID`         | Não         | —      | Id do último item recebido (vem de `nextCursor.id`)                       |
| `cursorData`  | `datetime`     | Não         | —      | Data de ordenação do último item (vem de `nextCursor.data`)               |
| `limite`      | `int`          | Não         | `5`    | Quantidade de itens por página (mín. 1, máx. 50)                          |

### Estrutura da resposta

Todos os endpoints de listagem retornam o envelope:

```json
{
  "content": [ ...itens... ],
  "nextCursor": { "id": "3d7c84da-...", "data": "2026-08-15T13:48:38" },
  "hasNext": true
}
```

| Campo         | Descrição                                                               |
|---------------|-------------------------------------------------------------------------|
| `content`     | Itens da página atual (respeitando `limite`)                             |
| `nextCursor`  | Cursor para buscar a próxima página; `null` quando não há mais resultados |
| `hasNext`     | `true` se existem mais itens após esta página                            |

### Como navegar

1. **Primeira chamada** — sem `cursorId`/`cursorData`:

   ```http
   GET /eventos?limite=5
   Authorization: Bearer <token>
   ```

2. **Próximas páginas** — copie os campos de `nextCursor` para os query params:

   ```http
   GET /eventos?limite=5&cursorId=3d7c84da-b7c4-4c27-976f-70e5f197db43&cursorData=2026-08-15T13%3A48%3A38
   Authorization: Bearer <token>
   ```

3. **Fim da lista** — quando `hasNext` for `false` (e `nextCursor` for `null`).

### Regras e detalhes

- **Limite padrão:** 5 itens por requisição; máximo 50 (valores maiores são truncados).
- **Ordenação estável:** os resultados são ordenados por um campo de data (descendente, em geral) e desempatados pelo `id`, garantindo consistência.
- **Data do cursor:** em endpoints de eventos ordenados por `criadoEm`, `nextCursor.data` é a data de criação. Em `/eventos/proximos`, que ordena por início, `nextCursor.data` é a data de início do evento.
- **Custo:** keyset pagination evita `OFFSET`, mantendo a consulta eficiente mesmo em bases grandes.

---

## Endpoints

### Auth (`/auth`)

| Método   | Rota                                | Descrição                                      |
|----------|-------------------------------------|-------------------------------------------------|
| `POST`   | `/auth/login`                       | Login com e-mail e senha                        |
| `POST`   | `/auth/register`                    | Registra usuário e retorna token                |
| `POST`   | `/auth/refresh-token`               | Renova token de acesso                          |
| `POST`   | `/auth/logout`                      | Revoga refresh token                            |
| `POST`   | `/auth/redefinir-senha`             | Solicita redefinição de senha                   |
| `GET`    | `/auth/me`                          | Perfil do usuário autenticado                   |
| `GET`    | `/auth/login/google`                | Redireciona para Google OAuth                   |
| `GET`    | `/auth/login/google/autorizado`     | Callback do Google OAuth                        |

### Eventos (`/eventos`)

| Método   | Rota                           | Descrição                                         | Paginado |
|----------|--------------------------------|---------------------------------------------------|----------|
| `GET`    | `/eventos`                     | Lista todos os eventos (por criação, desc)        | Sim      |
| `GET`    | `/eventos/proximos`            | Próximos eventos (por data de início, asc)        | Sim      |
| `GET`    | `/eventos/{id}`                | Busca evento por id                               | Não      |
| `POST`   | `/eventos`                     | Cria evento (apenas autenticado)                  | Não      |
| `PUT`    | `/eventos/{id}`                | Atualiza evento (apenas o organizador)            | Não      |
| `DELETE` | `/eventos/{id}`                | Remove evento (apenas o organizador)              | Não      |
| `POST`   | `/eventos/{id}/participar`     | Participar do evento                              | Não      |
| `DELETE` | `/eventos/{id}/participar`     | Cancelar participação                             | Não      |
| `GET`    | `/eventos/{id}/participantes`  | Lista participantes                               | Sim      |

### Posts (`/post`)

Os endpoints de criação e atualização de posts aceitam **multipart/form-data** para upload de mídia (imagens e vídeos).

**Campos do multipart:**

| Campo         | Tipo              | Obrigatório | Descrição                                      |
|---------------|-------------------|-------------|-------------------------------------------------|
| `texto`       | `String`          | Não         | Conteúdo textual do post                        |
| `tipo`        | `String`          | Não         | Tipo do post                                    |
| `visibilidade`| `String`          | Não         | Visibilidade do post                            |
| `midias`      | `List<MultipartFile>` | Não     | Arquivos de imagem ou vídeo (max 50MB cada)     |

| Método   | Rota                          | Descrição                                      | Paginado |
|----------|-------------------------------|------------------------------------------------|----------|
| `GET`    | `/post/feed/{usuarioId}`     | Feed de posts dos eventos do usuário           | Sim      |
| `GET`    | `/post/eventos/{eventoId}`   | Posts de um evento                             | Sim      |
| `GET`    | `/post/usuarios/{usuarioId}` | Posts de um usuário                            | Sim      |
| `POST`   | `/post`                      | Cria post com texto e mídia (multipart)        | Não      |
| `GET`    | `/post/{id}`                 | Busca post por id                              | Não      |
| `PUT`    | `/post/{id}`                 | Atualiza post (multipart, apenas o autor)      | Não      |
| `DELETE` | `/post/{id}`                 | Remove post (apenas o autor)                   | Não      |
| `POST`   | `/post/{id}/curtir`          | Curte um post                                  | Não      |
| `DELETE` | `/post/{id}/curtir`          | Descurte um post                               | Não      |

### Comentários (`/comentarios`)

| Método   | Rota                          | Descrição                  |
|----------|-------------------------------|----------------------------|
| `GET`    | `/comentarios/post/{id}`      | Comentários de um post     |
| `POST`   | `/comentarios/post/{id}`      | Cria comentário no post    |
| `PUT`    | `/comentarios/{id}`           | Atualiza comentário        |
| `DELETE` | `/comentarios/{id}`           | Remove comentário          |

### Conexões (`/conexoes` e `/usuarios/{id}/conexoes`)

| Método   | Rota                              | Descrição                          |
|----------|-----------------------------------|------------------------------------|
| `POST`   | `/usuarios/{id}/conexoes`         | Envia solicitação de conexão       |
| `GET`    | `/usuarios/me/conexoes/recebidas` | Solicitações recebidas             |
| `GET`    | `/usuarios/me/conexoes/enviadas`  | Solicitações enviadas              |
| `PATCH`  | `/conexoes/{id}/aceitar`          | Aceita solicitação                 |
| `PATCH`  | `/conexoes/{id}/recusar`          | Recusa solicitação                 |
| `DELETE` | `/conexoes/{id}`                  | Remove/desfaz conexão              |
| `GET`    | `/usuarios/{id}/conexao`          | Status da relação com o usuário    |
| `GET`    | `/usuarios/{id}/conexoes`         | Lista conexões do usuário          |

### Usuários (`/usuarios`)

| Método   | Rota                       | Descrição                     |
|----------|----------------------------|-------------------------------|
| `GET`    | `/usuarios/me`             | Perfil do usuário logado      |
| `PUT`    | `/usuarios/me`             | Atualiza perfil               |
| `PATCH`  | `/usuarios/me/foto`        | Atualiza foto de perfil       |
| `DELETE` | `/usuarios/me`             | Remove a conta                |
| `GET`    | `/usuarios/{id}`           | Busca usuário por id          |
| `GET`    | `/usuarios/me/interesses`  | Lista interesses do usuário   |
| `PUT`    | `/usuarios/me/interesses`  | Atualiza interesses           |
| `GET`    | `/usuarios/me/eventos`     | Eventos em que o usuário participa |

### Agenda (`/agenda`)

| Método   | Rota                      | Descrição                                  |
|----------|---------------------------|--------------------------------------------|
| `GET`    | `/agenda?eventoId={id}`   | Agenda de um evento (obrigatório `eventoId`) |
| `POST`   | `/agenda`                 | Cria item de agenda                        |
| `PUT`    | `/agenda/{id}`            | Atualiza item de agenda                    |
| `DELETE` | `/agenda/{id}`            | Remove item de agenda                      |

### Storage (`/storage`)

| Método   | Rota              | Descrição                                             |
|----------|-------------------|-------------------------------------------------------|
| `POST`   | `/storage/upload` | Upload de imagem para S3 (multipart); atualiza foto de perfil do usuário automaticamente |

---

## Infraestrutura

### AWS S3 + CloudFront

Uploads de imagens (posts e fotos de perfil) são armazenados no S3 e servidos via CloudFront.

Variáveis de ambiente:

```
AWS_ACCESS_KEY=...
AWS_SECRET_KEY=...
AWS_REGION=us-east-1
AWS_BUCKET_NAME=s3-conectaai
AWS_CLOUDFRONT_DOMAIN_NAME=d38sp318d0ruxp.cloudfront.net
```

### CORS

A origem permitida para requests é configurável via variável de ambiente `CORS_ORIGINS` (padrão: `http://localhost:5173`).

### Flyway

Migrations do banco de dados estão em `src/main/resources/db/migration/` (18 versões: V1 até V18).
