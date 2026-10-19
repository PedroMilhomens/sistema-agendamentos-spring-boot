# API de Agendamentos com Java Spring

Uma API REST para consultar, criar, modificar e deletar agendemantos
resgistrados em um Banco de Dados MySQL

## Tecnologias Utilizaddas: 
- Java 21
- Spring Framework
- Docker
- MySQL
- HTML5 (Ainda nao implementado)
- CSS3 (Ainda nao implementado)
- JavaScript (Ainda nao implementado)

## Implementacoes:
- [x] Entidade Agendamentos;
  - [x] Domain;
  
  - [x] Controller:
    - [x] Get Mapping;
    - [x] Post Mapping;
    - [x] Delete Mapping;
    - [x] Put Mapping.

  - [x] Service:
    - [x] findById(Integer id)
    - [x] save()
    - [x] delete(Integer id)
    - [x] replace()

  - [x] Mapper
    - [x] Post Request;
    - [x] Put Request.
  
  - [ ] Spring Security