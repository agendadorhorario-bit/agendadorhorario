# Agendador API

API Spring Boot para manipular a tabela `usuario` no PostgreSQL.

## Requisitos
- Java 17+
- PostgreSQL com a base `agendador` e tabela `usuario`

## Configuracao da conexao
A aplicacao usa estas variaveis (com fallback padrao):
- `DB_URL` (padrao: `jdbc:postgresql://localhost:5433/agendador`)
- `DB_USER` (padrao: `agend`)
- `DB_PASSWORD` (padrao: `troque_esta_senha`)

## Rede corporativa (proxy Maven)
Se houver bloqueio para baixar dependencias, edite `\.mvn\settings.xml` e ative o proxy:
- trocar `host`, `port`, `username`, `password`
- mudar `<active>false</active>` para `true`

## Subir aplicacao
```powershell
.\mvnw.cmd spring-boot:run
```

## Compilar projeto
```powershell
.\mvnw.cmd -DskipTests compile
```

## Swagger / OpenAPI
Com a aplicacao rodando, acesse:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Endpoints da tabela `usuario`
### Inserir
- `POST /usuarios`

Body:
```json
{
  "nomeUsuario": "Maria Silva",
  "enderecoUsuario": "Rua A, 123",
  "celularUsuario": "11999990000",
  "obsUsuario": "cliente VIP"
}
```

### Editar
- `PUT /usuarios/{idUsuario}`

Body:
```json
{
  "nomeUsuario": "Maria Silva",
  "enderecoUsuario": "Rua B, 456",
  "celularUsuario": "11999990001",
  "obsUsuario": "atualizado"
}
```

### Deletar
- `DELETE /usuarios/{idUsuario}`

