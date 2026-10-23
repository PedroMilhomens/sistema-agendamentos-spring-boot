# API de Agendamentos com Spring Java

Uma API REST para consultar, criar, atualizar e excluir agendamentos
armazenados em um banco de dados MySQL.

## Tecnologias
- Java 17
- Spring Framework (Spring Boot)
- Docker
- MySQL

## Endpoints

- `GET /api/agendamentos` e `GET /api/agendamentos/{id}`
- `POST /api/agendamentos`, `PUT /api/agendamentos/{id}` e `DELETE /api/agendamentos/{id}`
- As mesmas rotas CRUD estão disponíveis em `/api/clientes` e `/api/doutores`.
- A criação de clientes retorna `201 Created`, sem corpo na resposta.
- As datas dos agendamentos devem usar o formato ISO (`yyyy-MM-dd`) e os horários, `HH:mm:ss` (por exemplo, `2026-10-21` e `09:30:00`). Os IDs do médico e do cliente são obrigatórios.

As rotas de recursos usam `{id}` para consultar, atualizar e excluir registros. Em instalações existentes, pode ser necessário migrar as colunas de data e hora dos agendamentos dos tipos de texto anteriores para `DATE` e `TIME` do SQL.

## Como executar

Inicie o MySQL com `docker compose up -d` e, em seguida, execute a aplicação com `./mvnw spring-boot:run`. O banco de dados configurado é `agendamentos`, em `localhost:3306`.