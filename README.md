
# ProvaSmart — Backend

API REST do **ProvaSmart**, uma plataforma web de apoio à preparação para o Exame Nacional do Ensino Médio (ENEM).

O backend é responsável pelas regras de negócio, gerenciamento do banco de questões, geração e realização de simulados, persistência das respostas dos estudantes e, futuramente, correção automática, análise de desempenho e geração de planos de estudo.

## Sobre o ProvaSmart

O ProvaSmart busca reunir, em um único ambiente, simulados, correção e acompanhamento de resultados.

A proposta é ajudar o estudante a identificar as áreas e os assuntos que precisam de maior atenção, sem substituir professores, materiais didáticos ou outras formas de preparação.

O sistema prevê dois perfis de acesso:

- **Aluno:** realiza simulados, consulta tentativas anteriores e acompanha seu desempenho.
- **Administrador:** gerencia o banco de questões e outras informações necessárias à plataforma.

## Funcionalidades

### Concluídas

#### Banco de Questões

Permite o gerenciamento das questões utilizadas pela plataforma.

Principais recursos:

- cadastro de questões;
- consulta de questões;
- atualização de questões;
- ativação e desativação;
- associação da questão a assunto e disciplina;
- classificação por área do ENEM;
- cadastro de cinco alternativas por questão;
- definição da alternativa correta;
- paginação das consultas.

#### Simulado Geral

Permite a criação e realização de simulados baseados nas quatro áreas do ENEM.

Principais regras:

- cada simulado possui **40 questões**;
- são selecionadas **10 questões de cada área do ENEM**;
- apenas questões ativas podem ser selecionadas;
- as questões são selecionadas aleatoriamente;
- uma questão não pode aparecer mais de uma vez no mesmo simulado;
- o aluno pode possuir apenas um simulado em andamento;
- as respostas podem ser alteradas enquanto o simulado estiver em andamento;
- a alternativa escolhida deve pertencer à questão;
- todas as questões devem ser respondidas antes da finalização;
- após a finalização, as respostas não podem mais ser alteradas.

### Em desenvolvimento

- correção automática do simulado;
- cálculo de desempenho geral;
- desempenho por área do ENEM;
- histórico de simulados;
- dashboard do aluno;
- prática direcionada por desempenho;
- plano de estudos.

## Tecnologias utilizadas

- Java 25;
- Spring Boot 4;
- Spring Web;
- Spring Data JPA;
- Hibernate;
- Bean Validation;
- MapStruct;
- Lombok;
- PostgreSQL;
- Flyway;
- Maven.

Tecnologias previstas para as próximas etapas:

- Spring Security;
- JWT;
- BCrypt;
- Springdoc OpenAPI / Swagger;
- JUnit;
- Mockito.

## Arquitetura

A aplicação utiliza arquitetura em camadas:

```text
Cliente React
     │
     │ HTTP/HTTPS + JSON
     ▼
Controllers ──► Services ──► Repositories ──► PostgreSQL
                    │
                    └── regras de negócio e validações

    Controllers: recebem e respondem às requisições da API REST.

    Services: concentram regras de negócio e validações.

    Repositories: realizam o acesso aos dados utilizando Spring Data JPA.

    Mappers: realizam a conversão entre entidades e DTOs.

Estrutura atual

Entre as principais estruturas existentes no backend estão:

    Questão;

    Alternativa;

    Assunto;

    Disciplina;

    Área do ENEM;

    Simulado;

    Questão do simulado;

    Resposta do simulado.

Recursos da API
Questões

Responsável pelo gerenciamento administrativo do banco de questões.

Exemplos de operações:

POST    /questions
GET     /questions
GET     /questions/{id}
PUT     /questions/{id}
PATCH   /questions/{id}/deactivate

Simulados

Responsável pela geração, realização e finalização dos simulados.

POST   /simulations/student/{studentId}

GET    /simulations/{simulationId}

PUT    /simulations/{simulationId}/questions/{simulationQuestionId}/answer

PATCH  /simulations/{simulationId}/finish

Como executar
Pré-requisitos

É necessário possuir:

    Java 25;

    PostgreSQL;

    Maven ou Maven Wrapper.

Clonar o projeto

git clone https://github.com/provasmart2026/provasmart-backend.git
cd provasmart-backend

Banco de dados

Crie um banco PostgreSQL para o projeto.

As migrations são executadas automaticamente pelo Flyway ao iniciar a aplicação.
Variáveis de ambiente

Configure:

DB_URL=
DB_USERNAME=
DB_PASSWORD=

Exemplo:

DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=senha

Executar

Windows:

mvnw.cmd spring-boot:run

Linux/macOS:

./mvnw spring-boot:run

Ou utilizando Maven instalado:

mvn spring-boot:run

Por padrão, a API será disponibilizada em:

http://localhost:8080

Banco de dados

O PostgreSQL é utilizado como banco de dados relacional.

O versionamento da estrutura do banco é realizado pelo Flyway, através das migrations disponíveis em:

src/main/resources/db/migration

Segurança e privacidade

A camada de autenticação e autorização será implementada utilizando:

    Spring Security;

    JWT;

    BCrypt;

    controle de acesso por perfil.

Também estão previstos mecanismos relacionados à proteção dos dados pessoais e aos princípios aplicáveis da LGPD.
Testes

Estão previstos testes unitários e de integração utilizando:

    JUnit;

    Mockito.

As principais regras de negócio das funcionalidades serão cobertas pelos testes automatizados durante a evolução do projeto.
Frontend

O frontend do ProvaSmart será desenvolvido utilizando React e consumirá esta API REST.
Equipe

Projeto Final de Curso do Bacharelado em Sistemas de Informação da Universidade de Mogi das Cruzes (UMC).

Desenvolvido por:

    Alyne Rodrigues de Campos

    Gustavo Gonçalves Baião

Status

🚧 Em desenvolvimento

Funcionalidades concluídas:

    ✅ Banco de Questões

    ✅ Simulado Geral

Próximas etapas:

    ⏳ Correção e análise de desempenho

    ⏳ Dashboard do aluno

    ⏳ Prática direcionada

    ⏳ Plano de estudos

    ⏳ Autenticação e autorização


Eu gosto mais dessa versão porque ela mostra claramente **o que o projeto já faz hoje**, sem deixar o README parecendo apenas um planejamento inicial.

Tem só um ponto que eu confirmaria antes de você subir: no README atual aparecem `Spring Security`, `JWT` e `BCrypt` como planejados. Se o login/autenticação já estiver implementado na `develop`, aí precisamos mover esses itens para **tecnologias utilizadas** e também adicionar a feature de autenticação como concluída. 


faltou falar o que a pessoa precisa fazer para rodar o sistema

Sim, faltou deixar a parte de execução bem mais prática, pensando em alguém que acabou de clonar o projeto e quer subir a API pela primeira vez.

Eu trocaria a seção Como executar por algo assim:

## Como executar o projeto

### 1. Pré-requisitos

Antes de executar o projeto, é necessário ter instalado:

- Java 25;
- PostgreSQL;
- Git;
- Maven, ou utilizar o Maven Wrapper incluído no projeto.

Para verificar as instalações:

```bash
java -version
git --version

Se estiver utilizando Maven instalado:

mvn -version

2. Clonar o repositório

git clone https://github.com/provasmart2026/provasmart-backend.git

Entre na pasta do projeto:

cd provasmart-backend

Como o desenvolvimento principal ocorre na branch develop, altere para ela:

git checkout develop

Atualize o projeto:

git pull origin develop

3. Criar o banco de dados

Com o PostgreSQL em execução, crie um banco de dados para o ProvaSmart.

Exemplo:

CREATE DATABASE provasmart;

Não é necessário criar as tabelas manualmente.

O projeto utiliza Flyway, que executará automaticamente as migrations ao iniciar a aplicação.

As migrations estão localizadas em:

src/main/resources/db/migration

4. Configurar as variáveis de ambiente

A aplicação precisa das informações de conexão com o PostgreSQL.

Configure as seguintes variáveis de ambiente:

DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

Os valores devem ser ajustados de acordo com a configuração do PostgreSQL de cada desenvolvedor.
IntelliJ IDEA

No IntelliJ:

Run
→ Edit Configurations
→ Environment variables

Adicione:

DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

5. Instalar as dependências

O Maven fará o download automático das dependências do projeto.

No Windows:

mvnw.cmd clean install

Linux/macOS:

./mvnw clean install

Também é possível utilizar o Maven instalado:

mvn clean install

6. Executar a aplicação

No Windows:

mvnw.cmd spring-boot:run

Linux/macOS:

./mvnw spring-boot:run

Ou com Maven instalado:

mvn spring-boot:run

Também é possível executar diretamente pela IDE utilizando a classe principal da aplicação Spring Boot.
7. Verificar se a aplicação iniciou corretamente

Se tudo estiver configurado corretamente, a aplicação estará disponível em:

http://localhost:8080

Durante a inicialização, o Flyway aplicará automaticamente as migrations pendentes no banco de dados.

Caso ocorra erro de conexão com o banco, verifique:

    se o PostgreSQL está em execução;

    se o banco provasmart foi criado;

    se DB_URL está correta;

    se DB_USERNAME está correto;

    se DB_PASSWORD está correta.

Resumo rápido

Para quem já possui Java e PostgreSQL configurados:

git clone https://github.com/provasmart2026/provasmart-backend.git
cd provasmart-backend
git checkout develop

Criar o banco:

CREATE DATABASE provasmart;

Configurar:

DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

E executar:

mvnw.cmd spring-boot:run

No Linux/macOS:

./mvnw spring-boot:run


Assim fica bem mais útil porque quem entrar no GitHub entende o fluxo completo:

```text
Clonar
   ↓
Ir para develop
   ↓
Criar PostgreSQL
   ↓
Configurar variáveis
   ↓
Maven baixa dependências
   ↓
Flyway cria/atualiza tabelas
   ↓
Subir Spring Boot
   ↓
localhost:8080

Eu também manteria a observação de que não precisa executar os scripts SQL manualmente, porque isso evita alguém entrar no db/migration e tentar rodar V1, V2, V3 etc. pelo pgAdmin. O Flyway deve cuidar disso.

refaça o arquivo 

Analisado

from pathlib import Path

content = """# ProvaSmart — Backend

API REST do **ProvaSmart**, uma plataforma web de apoio à preparação para o Exame Nacional do Ensino Médio (ENEM).

O backend é responsável pelas regras de negócio, gerenciamento do banco de questões, geração e realização de simulados, persistência das respostas dos estudantes e, nas próximas etapas, correção automática, análise de desempenho, autenticação, histórico, prática direcionada e plano de estudos.

## Sobre o ProvaSmart

O ProvaSmart busca reunir, em um único ambiente, simulados, correção e acompanhamento de resultados.

A proposta é ajudar o estudante a identificar as áreas e os assuntos que precisam de maior atenção, sem substituir professores, materiais didáticos ou outras formas de preparação.

O sistema prevê dois perfis de acesso:

- **Aluno:** realiza simulados, consulta resultados e acompanha seu desempenho.
- **Administrador:** gerencia o banco de questões e outras informações necessárias à plataforma.

## Funcionalidades

### Concluídas

#### Banco de Questões

Permite o gerenciamento das questões utilizadas pela plataforma.

Principais recursos:

- cadastro de questões;
- consulta de questões;
- atualização de questões;
- ativação e desativação;
- associação da questão a assunto e disciplina;
- classificação por área do ENEM;
- cadastro de cinco alternativas por questão;
- definição da alternativa correta;
- paginação das consultas.

#### Simulado Geral

Permite a criação e realização de simulados baseados nas quatro áreas do ENEM.

Principais regras:

- cada simulado possui **40 questões**;
- são selecionadas **10 questões de cada área do ENEM**;
- apenas questões ativas podem ser selecionadas;
- as questões são selecionadas aleatoriamente;
- uma questão não pode aparecer mais de uma vez no mesmo simulado;
- o aluno pode possuir apenas um simulado em andamento;
- as respostas podem ser alteradas enquanto o simulado estiver em andamento;
- a alternativa escolhida deve pertencer à questão correspondente;
- todas as questões devem ser respondidas antes da finalização;
- após a finalização, as respostas não podem mais ser alteradas.

### Próximas etapas

- correção automática do simulado;
- cálculo de desempenho geral;
- desempenho por área e assunto;
- histórico de simulados;
- dashboard do aluno;
- prática direcionada por desempenho;
- plano de estudos;
- autenticação e autorização de usuários.

## Tecnologias utilizadas

- Java 25;
- Spring Boot 4.1.1;
- Spring Web MVC;
- Spring Data JPA;
- Hibernate;
- Bean Validation;
- MapStruct 1.6.3;
- Lombok;
- PostgreSQL;
- Flyway;
- Springdoc OpenAPI / Swagger;
- Maven.

### Tecnologias previstas para as próximas etapas

- Spring Security;
- JWT;
- BCrypt;
- JUnit;
- Mockito.

## Arquitetura

A aplicação utiliza arquitetura em camadas:

```text
Cliente React
     │
     │ HTTP/HTTPS + JSON
     ▼
Controllers ──► Services ──► Repositories ──► PostgreSQL
                    │
                    └── regras de negócio e validações

    Controllers: recebem e respondem às requisições da API REST.

    Services: concentram regras de negócio, validações e fluxos da aplicação.

    Repositories: realizam o acesso aos dados utilizando Spring Data JPA.

    Mappers: realizam a conversão entre entidades e DTOs.

    Flyway: controla o versionamento da estrutura do banco de dados.

Estrutura atual

Entre as principais estruturas existentes no backend estão:

    Questão;

    Alternativa;

    Assunto;

    Disciplina;

    Área do ENEM;

    Simulado;

    Questão do simulado;

    Resposta do simulado.

Recursos da API
Questões

Responsável pelo gerenciamento administrativo do banco de questões.

Exemplos de operações:

POST    /questions
GET     /questions
GET     /questions/{id}
PUT     /questions/{id}
PATCH   /questions/{id}/deactivate

Simulados

Responsável pela geração, realização e finalização dos simulados.

POST   /simulations/student/{studentId}
GET    /simulations/{simulationId}
PUT    /simulations/{simulationId}/questions/{simulationQuestionId}/answer
PATCH  /simulations/{simulationId}/finish

Como executar o projeto
1. Pré-requisitos

Antes de executar o projeto, é necessário ter instalado:

    Java 25;

    PostgreSQL;

    Git;

    Maven instalado ou utilizar o Maven Wrapper incluído no projeto.

Para verificar as instalações:

java -version
git --version

Caso esteja utilizando Maven instalado:

mvn -version

2. Clonar o repositório

Clone o projeto:

git clone https://github.com/provasmart2026/provasmart-backend.git

Entre na pasta:

cd provasmart-backend

Como a branch utilizada para integração e desenvolvimento do projeto é a develop, altere para ela:

git checkout develop

Atualize a branch:

git pull origin develop

3. Criar o banco de dados

Com o PostgreSQL em execução, crie um banco para o ProvaSmart.

Exemplo:

CREATE DATABASE provasmart;

Não é necessário criar as tabelas manualmente.

O projeto utiliza Flyway, responsável por executar automaticamente as migrations ao iniciar a aplicação.

As migrations estão localizadas em:

src/main/resources/db/migration

4. Configurar as variáveis de ambiente

A aplicação utiliza variáveis de ambiente para realizar a conexão com o PostgreSQL.

Configure:

DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

Os valores devem ser ajustados de acordo com a configuração local do PostgreSQL.
Configuração no IntelliJ IDEA

Acesse:

Run
→ Edit Configurations
→ Environment variables

Adicione as variáveis:

DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

5. Baixar as dependências e compilar

O Maven será responsável por baixar as dependências do projeto.

No Windows:

mvnw.cmd clean install

No Linux/macOS:

./mvnw clean install

Ou, caso possua Maven instalado:

mvn clean install

6. Executar a aplicação

No Windows:

mvnw.cmd spring-boot:run

No Linux/macOS:

./mvnw spring-boot:run

Ou utilizando Maven instalado:

mvn spring-boot:run

Também é possível iniciar diretamente pela IDE executando a classe principal da aplicação Spring Boot.
7. Verificar a execução

Por padrão, a API estará disponível em:

http://localhost:8080

Durante a inicialização, o Flyway aplicará automaticamente as migrations pendentes no banco de dados.

Caso ocorra erro de conexão com o PostgreSQL, verifique:

    se o PostgreSQL está em execução;

    se o banco provasmart foi criado;

    se DB_URL está correta;

    se DB_USERNAME está correto;

    se DB_PASSWORD está correta.

8. Swagger

A documentação interativa da API utiliza Springdoc OpenAPI.

Com a aplicação em execução, o Swagger pode ser acessado em:

http://localhost:8080/swagger-ui/index.html

Resumo rápido para execução

Para quem já possui Java 25, PostgreSQL e Git configurados:

git clone https://github.com/provasmart2026/provasmart-backend.git
cd provasmart-backend
git checkout develop

Crie o banco:

CREATE DATABASE provasmart;

Configure as variáveis de ambiente:

DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

Execute no Windows:

mvnw.cmd spring-boot:run

Ou no Linux/macOS:

./mvnw spring-boot:run

Fluxo resumido:

Clonar o repositório
        ↓
Acessar a branch develop
        ↓
Criar o banco PostgreSQL
        ↓
Configurar as variáveis de ambiente
        ↓
Executar o projeto
        ↓
Flyway aplica as migrations
        ↓
API disponível em localhost:8080

Banco de dados

O PostgreSQL é utilizado como banco de dados relacional.

O versionamento da estrutura do banco é realizado pelo Flyway, por meio das migrations disponíveis em:

src/main/resources/db/migration

As migrations já aplicadas não devem ser alteradas, pois o Flyway utiliza checksum para validar o histórico de execução.
Segurança e privacidade

A camada de autenticação e autorização será implementada utilizando:

    Spring Security;

    JWT;

    BCrypt;

    controle de acesso por perfil.

Também estão previstos mecanismos relacionados à proteção de dados pessoais e aos princípios aplicáveis da LGPD.
Testes

Estão previstos testes unitários e de integração com foco nas principais regras de negócio da aplicação.

As tecnologias planejadas para essa etapa são:

    JUnit;

    Mockito.

Frontend

O frontend do ProvaSmart será desenvolvido em React e consumirá esta API REST.
Equipe

Projeto Final de Curso do Bacharelado em Sistemas de Informação da Universidade de Mogi das Cruzes (UMC).

Desenvolvido por:

    Alyne Rodrigues de Campos

    Gustavo Gonçalves Baião

Status

🚧 Em desenvolvimento

Funcionalidades concluídas:

    ✅ Banco de Questões

    ✅ Simulado Geral

Próximas etapas:

    ⏳ Correção e análise de desempenho

    ⏳ Dashboard do aluno

    ⏳ Prática direcionada

    ⏳ Plano de estudos

    ⏳ Autenticação e autorização
    """

path = Path("/mnt/data/README.md")
path.write_text(content, encoding="utf-8")
print(path)

STDOUT/STDERR

/mnt/data/README.md

Refiz o README.md completo, agora incluindo uma seção de execução passo a passo para quem clonar o projeto pela primeira vez.

Também alinhei o arquivo com o estado atual do backend: Java 25, Spring Boot 4.1.1, Maven, PostgreSQL, Flyway, Swagger e as duas features já concluídas. O pom.xml da develop confirma essas versões e dependências.

Baixar o novo README.md

Não alterei o repositório no GitHub diretamente. Se quiser, no próximo passo posso substituir o README.md da branch develop por esse arquivo.
README.mdDocumento


Biblioteca/
README.md
ProvaSmart — Backend

API REST do ProvaSmart, uma plataforma web de apoio à preparação para o Exame Nacional do Ensino Médio (ENEM).

O backend é responsável pelas regras de negócio, gerenciamento do banco de questões, geração e realização de simulados, persistência das respostas dos estudantes e, nas próximas etapas, correção automática, análise de desempenho, autenticação, histórico, prática direcionada e plano de estudos.
Sobre o ProvaSmart

O ProvaSmart busca reunir, em um único ambiente, simulados, correção e acompanhamento de resultados.

A proposta é ajudar o estudante a identificar as áreas e os assuntos que precisam de maior atenção, sem substituir professores, materiais didáticos ou outras formas de preparação.

O sistema prevê dois perfis de acesso:

    Aluno: realiza simulados, consulta resultados e acompanha seu desempenho.

    Administrador: gerencia o banco de questões e outras informações necessárias à plataforma.

Funcionalidades
Concluídas
Banco de Questões

Permite o gerenciamento das questões utilizadas pela plataforma.

Principais recursos:

    cadastro de questões;

    consulta de questões;

    atualização de questões;

    ativação e desativação;

    associação da questão a assunto e disciplina;

    classificação por área do ENEM;

    cadastro de cinco alternativas por questão;

    definição da alternativa correta;

    paginação das consultas.

Simulado Geral

Permite a criação e realização de simulados baseados nas quatro áreas do ENEM.

Principais regras:

    cada simulado possui 40 questões;

    são selecionadas 10 questões de cada área do ENEM;

    apenas questões ativas podem ser selecionadas;

    as questões são selecionadas aleatoriamente;

    uma questão não pode aparecer mais de uma vez no mesmo simulado;

    o aluno pode possuir apenas um simulado em andamento;

    as respostas podem ser alteradas enquanto o simulado estiver em andamento;

    a alternativa escolhida deve pertencer à questão correspondente;

    todas as questões devem ser respondidas antes da finalização;

    após a finalização, as respostas não podem mais ser alteradas.

Próximas etapas

    correção automática do simulado;

    cálculo de desempenho geral;

    desempenho por área e assunto;

    histórico de simulados;

    dashboard do aluno;

    prática direcionada por desempenho;

    plano de estudos;

    autenticação e autorização de usuários.

Tecnologias utilizadas

    Java 25;

    Spring Boot 4.1.1;

    Spring Web MVC;

    Spring Data JPA;

    Hibernate;

    Bean Validation;

    MapStruct 1.6.3;

    Lombok;

    PostgreSQL;

    Flyway;

    Springdoc OpenAPI / Swagger;

    Maven.

Tecnologias previstas para as próximas etapas

    Spring Security;

    JWT;

    BCrypt;

    JUnit;

    Mockito.

Arquitetura

A aplicação utiliza arquitetura em camadas:

Cliente React
     │
     │ HTTP/HTTPS + JSON
     ▼
Controllers ──► Services ──► Repositories ──► PostgreSQL
                    │
                    └── regras de negócio e validações

    Controllers: recebem e respondem às requisições da API REST.

    Services: concentram regras de negócio, validações e fluxos da aplicação.

    Repositories: realizam o acesso aos dados utilizando Spring Data JPA.

    Mappers: realizam a conversão entre entidades e DTOs.

    Flyway: controla o versionamento da estrutura do banco de dados.

Estrutura atual

Entre as principais estruturas existentes no backend estão:

    Questão;

    Alternativa;

    Assunto;

    Disciplina;

    Área do ENEM;

    Simulado;

    Questão do simulado;

    Resposta do simulado.

Recursos da API
Questões

Responsável pelo gerenciamento administrativo do banco de questões.

Exemplos de operações:

POST    /questions
GET     /questions
GET     /questions/{id}
PUT     /questions/{id}
PATCH   /questions/{id}/deactivate

Simulados

Responsável pela geração, realização e finalização dos simulados.

POST   /simulations/student/{studentId}
GET    /simulations/{simulationId}
PUT    /simulations/{simulationId}/questions/{simulationQuestionId}/answer
PATCH  /simulations/{simulationId}/finish

Como executar o projeto
1. Pré-requisitos

Antes de executar o projeto, é necessário ter instalado:

    Java 25;

    PostgreSQL;

    Git;

    Maven instalado ou utilizar o Maven Wrapper incluído no projeto.

Para verificar as instalações:

java -version
git --version

Caso esteja utilizando Maven instalado:

mvn -version

2. Clonar o repositório

Clone o projeto:

git clone https://github.com/provasmart2026/provasmart-backend.git

Entre na pasta:

cd provasmart-backend

Como a branch utilizada para integração e desenvolvimento do projeto é a develop, altere para ela:

git checkout develop

Atualize a branch:

git pull origin develop

3. Criar o banco de dados

Com o PostgreSQL em execução, crie um banco para o ProvaSmart.

Exemplo:

CREATE DATABASE provasmart;

Não é necessário criar as tabelas manualmente.

O projeto utiliza Flyway, responsável por executar automaticamente as migrations ao iniciar a aplicação.

As migrations estão localizadas em:

src/main/resources/db/migration

4. Configurar as variáveis de ambiente

A aplicação utiliza variáveis de ambiente para realizar a conexão com o PostgreSQL.

Configure:

DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

Os valores devem ser ajustados de acordo com a configuração local do PostgreSQL.
Configuração no IntelliJ IDEA

Acesse:

Run
→ Edit Configurations
→ Environment variables

Adicione as variáveis:

DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

Verificar a execução

Por padrão, a API estará disponível em:

http://localhost:8080

Durante a inicialização, o Flyway aplicará automaticamente as migrations pendentes no banco de dados.

Caso ocorra erro de conexão com o PostgreSQL, verifique:

    se o PostgreSQL está em execução;

    se o banco provasmart foi criado;

    se DB_URL está correta;

    se DB_USERNAME está correto;

    se DB_PASSWORD está correta.

Swagger

A documentação interativa da API utiliza Springdoc OpenAPI.

Com a aplicação em execução, o Swagger pode ser acessado em:

http://localhost:8080/swagger-ui/index.html

Resumo rápido para execução

Para quem já possui Java 25, PostgreSQL e Git configurados:

git clone https://github.com/provasmart2026/provasmart-backend.git
cd provasmart-backend
git checkout develop

Crie o banco:

CREATE DATABASE provasmart;

Configure as variáveis de ambiente:

DB_URL=jdbc:postgresql://localhost:5432/provasmart
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

Execute no Windows:

mvnw.cmd spring-boot:run

Ou no Linux/macOS:

./mvnw spring-boot:run

Fluxo resumido:

Clonar o repositório
        ↓
Acessar a branch develop
        ↓
Criar o banco PostgreSQL
        ↓
Configurar as variáveis de ambiente
        ↓
Executar o projeto
        ↓
Flyway aplica as migrations
        ↓
API disponível em localhost:8080

Banco de dados

O PostgreSQL é utilizado como banco de dados relacional.

O versionamento da estrutura do banco é realizado pelo Flyway, por meio das migrations disponíveis em:

src/main/resources/db/migration

As migrations já aplicadas não devem ser alteradas, pois o Flyway utiliza checksum para validar o histórico de execução.
Segurança e privacidade

A camada de autenticação e autorização será implementada utilizando:

    Spring Security;

    JWT;

    BCrypt;

    controle de acesso por perfil.

Também estão previstos mecanismos relacionados à proteção de dados pessoais e aos princípios aplicáveis da LGPD.
Testes

Estão previstos testes unitários e de integração com foco nas principais regras de negócio da aplicação.

As tecnologias planejadas para essa etapa são:

    JUnit;

    Mockito.

Equipe

Projeto Final de Curso do Bacharelado em Sistemas de Informação da Universidade de Mogi das Cruzes (UMC).

Desenvolvido por:

    Alyne Rodrigues de Campos

    Gustavo Gonçalves Baião

Status

🚧 Em desenvolvimento

Funcionalidades concluídas:

    ✅ Banco de Questões

    ✅ Simulado Geral

Próximas etapas:

    ⏳ Correção e análise de desempenho

    ⏳ Dashboard do aluno

    ⏳ Prática direcionada

    ⏳ Plano de estudos

    ⏳ Autenticação e autorização

