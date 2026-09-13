# ProvaSmart — Backend

API REST do **ProvaSmart**, plataforma web de apoio à preparação para o ENEM.

O backend é responsável pelas regras de negócio, gerenciamento do banco de questões, geração e realização de simulados e persistência das respostas dos estudantes.

## Funcionalidades concluídas

### Banco de Questões

- cadastro, consulta e atualização de questões;
- ativação e desativação de questões;
- associação por área, disciplina e assunto;
- cinco alternativas por questão;
- definição da alternativa correta;
- paginação das consultas.

### Simulado Geral

- geração de simulados com **40 questões**;
- seleção de **10 questões por área do ENEM**;
- seleção apenas de questões ativas;
- questões escolhidas aleatoriamente e sem repetição;
- apenas um simulado em andamento por aluno;
- registro e alteração de respostas enquanto o simulado estiver em andamento;
- validação da alternativa escolhida;
- finalização somente após responder todas as questões.

## Tecnologias

- Java 25
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Hibernate
- Bean Validation
- MapStruct
- Lombok
- PostgreSQL
- Flyway
- Maven

## Arquitetura

```text
React
  |
  | HTTP + JSON
  v
Controllers -> Services -> Repositories -> PostgreSQL
                 |
                 -> Regras de negócio
```

Os DTOs são utilizados para entrada e saída de dados, e os Mappers realizam a conversão entre DTOs e entidades.

## CORS

O backend possui configuração de CORS para permitir a comunicação com o frontend React.

A origem permitida é definida pela variável de ambiente:

```env
CORS_ORIGIN=http://localhost:5173
```

No `application.yml`:

```yaml
spring:
  application:
    name: api
    cors:
      allowed-origins: ${CORS_ORIGIN}
```

## Como executar

### Pré-requisitos

- Java 25
- PostgreSQL
- Git
- Maven ou Maven Wrapper

### 1. Clonar o projeto

```bash
git clone https://github.com/provasmart2026/provasmart-backend.git
cd provasmart-backend
git checkout develop
```

### 2. Criar o banco

Exemplo:

```sql
CREATE DATABASE provasmart;
```

As tabelas são criadas e atualizadas automaticamente pelo **Flyway**.

### 3. Configurar as variáveis de ambiente

```env
DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
CORS_ORIGIN=http://localhost:5173
```

No IntelliJ IDEA:

```text
Run -> Edit Configurations -> Environment variables
```

### 4. Executar a aplicação

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Ou com Maven instalado:

```bash
mvn spring-boot:run
```

A API será disponibilizada, por padrão, em:

```text
http://localhost:8080
```

## Próximas etapas

- correção e análise de desempenho;
- dashboard do aluno;
- prática direcionada;
- plano de estudos;
- autenticação e autorização com Spring Security e JWT;
- testes automatizados.

## Frontend

O frontend será desenvolvido em **React** e consumirá esta API REST.

## Equipe

Projeto Final de Curso do Bacharelado em Sistemas de Informação da Universidade de Mogi das Cruzes (UMC).

Desenvolvido por:

- Alyne Rodrigues de Campos
- Gustavo Gonçalves Baião

## Status

🚧 Em desenvolvimento

- ✅ Banco de Questões
- ✅ Simulado Geral
