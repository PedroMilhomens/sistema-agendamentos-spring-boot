# Sistema de Agendamentos

Aplicação Spring Boot com portais autenticados para clientes e profissionais, usando MySQL e uma interface responsiva em português.

## Portais

Abra `http://localhost:8080/` para entrar ou criar uma conta de cliente. O login pede o tipo de conta e encaminha cada pessoa ao portal correspondente.

- **Cliente:** resumo de atendimentos, agenda com histórico e horários, busca de profissionais por especialidade e disponibilidade, agendamento e cancelamento de consultas, configurações de perfil.
- **Doutor:** resumo e agenda dos próprios atendimentos, além de configurações de perfil.
- Alterações no perfil exigem a senha atual. Senhas novas precisam ter pelo menos 10 caracteres.
- A disponibilidade corresponde ao horário exato selecionado; o sistema não modela duração de consultas nem expediente. A reserva é revalidada no servidor e serializada por profissional para evitar duas reservas simultâneas no mesmo horário.

Clientes podem criar a própria conta. Contas de doutores são provisionadas pela clínica; não há cadastro público de profissionais. A atualização do esquema adiciona e-mail e senha às linhas existentes de doutores, mas esses campos começam vazios até o provisionamento.

Configure um segredo forte fora do código antes de provisionar profissionais:

```sh
export DOCTOR_PROVISIONING_TOKEN='use-um-segredo-aleatorio-longo'
./mvnw spring-boot:run
```

Obtenha um token CSRF em `GET /api/auth/csrf` e envie o valor `token` como `X-XSRF-TOKEN`, mantendo o cookie `XSRF-TOKEN`. O endpoint `POST /api/auth/provision-doctor` também exige o header `X-Clinic-Provisioning-Token` com o segredo configurado. Envie `name`, `areaDeAtuacao`, `email` e `password`; para associar credenciais a um registro de doutor já existente, inclua também seu `doctorId`. Não exponha o segredo em código, logs ou no navegador.

Para produção, sirva a aplicação exclusivamente por HTTPS e configure `SESSION_COOKIE_SECURE=true`. A autenticação usa sessão no servidor, cookies HttpOnly e proteção CSRF. As rotas CRUD administrativas antigas (`/api/clientes`, `/api/doutores` e `/api/agendamentos`) exigem a função `ADMIN`; o acesso do portal usa endpoints próprios com autorização e escopo por conta.

## API do portal

- `POST /api/auth/login`, `POST /api/auth/register` (somente cliente), `GET /api/auth/me`, `POST /api/auth/logout`, `GET /api/auth/csrf`
- `GET /api/doutores/directory?especialidade=...&data=YYYY-MM-DD&hora=HH:mm:ss`
- `GET/PUT /api/cliente/perfil`, `GET/POST /api/cliente/agendamentos`, `DELETE /api/cliente/agendamentos/{id}`
- `GET/PUT /api/doutor/perfil`, `GET /api/doutor/agendamentos`

Datas usam `yyyy-MM-dd`; horários usam `HH:mm:ss`. Clientes só podem consultar e cancelar os próprios atendimentos, e doutores só podem consultar os atendimentos vinculados à própria conta.

## Tecnologias e execução

- Java 17+, Spring Boot, Spring Security, HTML/CSS/JavaScript, Docker e MySQL.
- Inicie o banco com `docker compose up -d`.
- Execute a aplicação com `./mvnw spring-boot:run`.
- O banco configurado é `agendamentos`, em `localhost:3306`.

Em instalações existentes, pode ser necessário migrar colunas antigas de data e hora dos tipos de texto para `DATE` e `TIME` do SQL antes de iniciar a aplicação.