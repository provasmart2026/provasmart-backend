# ProvaSmart — Backend

API REST do **ProvaSmart**, uma plataforma web de apoio à preparação para o Exame Nacional do Ensino Médio (ENEM). O backend será responsável pelas regras de negócio, autenticação e autorização, gerenciamento de questões e simulados, correção automática e persistência do histórico de desempenho dos estudantes.

> O projeto está em fase inicial. As tecnologias, funcionalidades e estruturas descritas abaixo representam a arquitetura planejada e serão atualizadas conforme a implementação evoluir.

## Sobre o ProvaSmart

O ProvaSmart busca reunir, em um único ambiente, simulados, correção e acompanhamento de resultados. A proposta é ajudar o estudante a identificar as áreas e os assuntos que precisam de maior atenção, sem substituir professores, materiais didáticos ou outras formas de preparação.

O sistema prevê dois perfis de acesso:

- **Aluno:** realiza simulados, consulta tentativas anteriores e acompanha o desempenho por área e assunto.
- **Administrador:** gerencia o banco de questões e outras informações necessárias à plataforma.

## Responsabilidades do backend

- cadastrar e autenticar usuários;
- autorizar operações de acordo com o perfil do usuário;
- gerenciar o banco de questões;
- gerar simulados automaticamente;
- receber respostas e realizar a correção automática;
- armazenar tentativas, resultados e histórico de desempenho;
- identificar áreas e assuntos com maior incidência de erros;
- fornecer dados para dashboards e práticas direcionadas;
- apoiar a geração de planos de estudo personalizados;
- disponibilizar documentação interativa da API.

## Tecnologias planejadas

- Java;
- Spring Boot;
- Spring Web;
- Spring Security;
- JWT (JSON Web Token);
- Spring Data JPA;
- Hibernate;
- Bean Validation;
- MapStruct;
- Lombok;
- PostgreSQL;
- BCrypt;
- Springdoc OpenAPI / Swagger;
- JUnit e Mockito;
- Maven ou Gradle, conforme definido na criação do projeto.

## Arquitetura

A aplicação seguirá uma arquitetura em camadas:

```text
Cliente React
     │
     │ HTTP/HTTPS + JSON
     ▼
Controllers ──► Services ──► Repositories ──► PostgreSQL
                    │
                    └── regras de negócio e segurança
```

- **Controllers:** recebem e respondem às requisições da API REST.
- **Services:** concentram regras de negócio, validações e fluxos da aplicação.
- **Repositories:** realizam o acesso aos dados por meio de JPA/Hibernate.
- **Security:** controla autenticação, tokens JWT e permissões por perfil.

## Entidades previstas

- Usuário;
- Perfil de acesso;
- Questão;
- Simulado;
- Tentativa;
- Resultado e histórico de desempenho;
- Prática direcionada;
- Plano de estudos;
- Registro de auditoria.

## Recursos da API previstos

Os caminhos definitivos serão registrados aqui após a implementação dos controllers.

| Recurso | Responsabilidade |
| --- | --- |
| Autenticação | cadastro, login e emissão de token |
| Usuários | consulta e gerenciamento de perfil |
| Questões | CRUD administrativo do banco de questões |
| Simulados | geração, consulta e finalização de simulados |
| Tentativas | registro de respostas e correção automática |
| Desempenho | resultados por área, assunto e período |
| Plano de estudos | recomendações baseadas nas dificuldades identificadas |

## Como executar

O código da aplicação ainda não foi adicionado ao repositório. Quando o projeto Spring Boot for criado, esta seção deverá informar, com base nos arquivos efetivamente versionados:

1. versão necessária do Java;
2. gerenciador de dependências adotado (Maven ou Gradle);
3. criação e migração do banco PostgreSQL;
4. variáveis de ambiente obrigatórias;
5. comando para executar a API;
6. endereço local e caminho da documentação Swagger.

Exemplo do fluxo esperado, sujeito a alteração:

```bash
git clone https://github.com/weblyne/provasmart-backend.git
cd provasmart-backend
```

## Configuração

As credenciais não devem ser versionadas. Os nomes definitivos das propriedades serão definidos durante a implementação. A configuração deverá contemplar, no mínimo:

```dotenv
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=
JWT_SECRET=
```

Um arquivo de exemplo, como `.env.example` ou `application-example.properties`, deverá ser adicionado sem valores sensíveis.

## Segurança e privacidade

A solução prevê:

- proteção de senhas com BCrypt;
- autenticação baseada em JWT;
- autorização por perfil de acesso;
- validação dos dados recebidos pela API;
- exclusão e tratamento de dados pessoais em conformidade com os princípios aplicáveis da LGPD;
- ausência de credenciais e segredos no histórico do Git.

## Testes

Estão previstos testes unitários e de integração com JUnit e Mockito, com foco nas principais regras de negócio. Os comandos de teste serão adicionados quando a ferramenta de build estiver definida.

## Repositório relacionado

- [Frontend do ProvaSmart](https://github.com/weblyne/provasmart-frontend)

## Equipe

Projeto Final de Curso do Bacharelado em Sistemas de Informação da Universidade de Mogi das Cruzes (UMC), desenvolvido por Alyne Rodrigues de Campos e Gustavo Gonçalves Baião.

## Status

🚧 Em desenvolvimento.
