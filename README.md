# Sistema de Gestão de Tarefas

Aplicação web desenvolvida como desafio técnico para gerenciamento de tarefas com funcionalidades de criação, atualização, remoção, listagem e conclusão de tarefas.

---

## Itens implementados

| Item | Descrição | Status |
|------|-----------|--------|
| **a** | Aplicação Java Web com JavaServer Faces (JSF) | ✅ Implementado |
| **b** | Persistência em banco de dados PostgreSQL | ✅ Implementado |
| **c** | JPA com Hibernate | ✅ Implementado |
| **d** | Testes de unidade (JUnit 5 + Mockito) | ✅ Implementado |
| **e** | Publicação em ambiente cloud | ⏳ Pendente |
| **f** | Diferenciais adicionais (detalhados abaixo) | ✅ Implementado |

### Diferenciais adicionais (item f)

- **PrimeFaces 13** — componentes ricos de UI: tabela com paginação e ordenação, calendário, dropdown, diálogos modais com animação e confirmação antes de ações destrutivas
- **Weld CDI** — injeção de dependências via `@Inject` e `@ApplicationScoped`, sem XML de configuração manual
- **Arquitetura em camadas** — separação clara entre Model → Repository → Service → ManagedBean → View
- **Regras de negócio isoladas na camada de serviço** — validação de campos obrigatórios, bloqueio de edição de tarefa concluída, rejeição de deadlines passados
- **Feedback visual** — mensagens de sucesso e erro com `p:growl`, badges coloridos por prioridade (alta/média/baixa), título riscado nas tarefas concluídas
- **Confirmação de ações destrutivas** — diálogo de confirmação antes de concluir ou remover uma tarefa

---

## Tecnologias utilizadas

| Tecnologia | Versão |
|---|---|
| Java | 25 |
| Jakarta Faces (JSF / Mojarra) | 4.0.4 |
| PrimeFaces | 13.0.10 |
| Weld CDI | 5.1.2 |
| Hibernate ORM | 6.4.4 |
| Jakarta Persistence (JPA) | 3.1.0 |
| PostgreSQL JDBC Driver | 42.7.3 |
| JUnit Jupiter | 5.10.2 |
| Mockito | 5.11.0 |
| Maven | 3.6+ |

---

## Funcionalidades

- **Criar tarefa** — título, descrição, responsável, prioridade (alta/média/baixa) e deadline
- **Editar tarefa** — somente tarefas em andamento podem ser editadas
- **Concluir tarefa** — move a tarefa da lista "Em andamento" para "Concluídas"
- **Remover tarefa** — disponível tanto em andamento quanto concluídas
- **Listar tarefas em andamento** — ordenadas por deadline (mais próximo primeiro)
- **Listar tarefas concluídas** — ordenadas por data de criação (mais recente primeiro)

---

## Estrutura do projeto

```
src/
├── main/
│   ├── java/br/gustavo/gestaotarefas/
│   │   ├── bean/           # ManagedBean JSF (TarefaBean)
│   │   ├── exception/      # Exceção de negócio (NegocioException)
│   │   ├── model/          # Entidades JPA e enums (Tarefa, Prioridade, SituacaoTarefa)
│   │   ├── repository/     # Acesso ao banco (TarefaRepository)
│   │   ├── service/        # Regras de negócio (TarefaService)
│   │   └── util/           # EntityManagerFactory singleton
│   ├── resources/META-INF/
│   │   └── persistence.xml # Configuração JPA/Hibernate
│   └── webapp/
│       ├── index.xhtml     # Tela principal
│       ├── erro.xhtml      # Página de erro (404/500)
│       └── WEB-INF/        # web.xml, faces-config.xml, beans.xml
└── test/
    └── java/br/gustavo/gestaotarefas/
        ├── ConexaoTest.java              # Sanidade da conexão com o banco
        ├── model/TarefaTest.java         # Testes da entidade de domínio
        └── service/TarefaServiceTest.java # Testes unitários da camada de serviço
```

---

## Pré-requisitos

- Java 25+
- Maven 3.6+
- PostgreSQL 14+
- Apache Tomcat 10.1+

---

## Configuração do banco de dados

Execute os comandos abaixo no PostgreSQL (via `psql` ou pgAdmin):

```sql
-- Criar o usuário
CREATE USER gestao_user WITH PASSWORD 'gestao123';

-- Criar o banco
CREATE DATABASE gestao_tarefas OWNER gestao_user;

-- Conceder permissões
GRANT ALL PRIVILEGES ON DATABASE gestao_tarefas TO gestao_user;
```

> O Hibernate está configurado com `hbm2ddl.auto=update`, então a tabela `tarefa` é criada automaticamente na primeira execução.

---

## Como executar localmente

### 1. Clonar e compilar

```bash
git clone <url-do-repositorio>
cd gestao-tarefas
mvn clean package
```

O WAR será gerado em `target/gestao-tarefas.war`.

### 2. Fazer deploy no Tomcat

Copie o WAR para o diretório `webapps` do Tomcat:

```bash
cp target/gestao-tarefas.war /caminho/para/tomcat/webapps/
```

Inicie o Tomcat:

```bash
/caminho/para/tomcat/bin/startup.sh   # Linux/macOS
/caminho/para/tomcat/bin/startup.bat  # Windows
```

### 3. Acessar a aplicação

Abra no navegador:

```
http://localhost:8080/gestao-tarefas/
```

---

## Como executar os testes

```bash
mvn test
```

Resultado esperado:

```
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```

### O que é testado

- **`TarefaServiceTest`** (25 testes) — cobre toda a camada de serviço com mocks do repositório:
  - `criar()`: dados válidos, situação forçada, validação de cada campo obrigatório, deadline passado/hoje
  - `atualizar()`: tarefa não encontrada, edição de tarefa concluída, deadline inválido
  - `concluir()`: fluxo normal, tarefa inexistente, idempotência (já concluída)
  - `remover()`: tarefa existente e inexistente
  - `buscarPorId()`: encontrada e não encontrada
  - `listar*()`: delegação ao repositório

- **`TarefaTest`** (5 testes) — cobre a entidade de domínio:
  - Situação padrão ao criar, efeito de `concluir()`, construtor, setters

- **`ConexaoTest`** (1 teste) — sanidade da conexão com o PostgreSQL

> **Nota:** Os testes de serviço não precisam de banco de dados — o repositório é mockado com Mockito. Apenas o `ConexaoTest` requer o PostgreSQL em execução.

---

## Observação sobre Java 25 e Mockito

O Byte Buddy (usado internamente pelo Mockito) ainda não oferece suporte oficial ao Java 25. O projeto já está configurado com a flag `-Dnet.bytebuddy.experimental=true` no plugin Surefire para contornar essa limitação sem impacto nos testes.
