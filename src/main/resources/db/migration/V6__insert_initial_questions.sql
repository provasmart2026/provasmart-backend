-- V6__insert_initial_questions.sql
-- Seed inicial para testes do Banco de Questões e do Simulado Geral.
-- 40 questões ativas: 10 por área de conhecimento.
-- Cada questão possui 5 alternativas e exatamente 1 alternativa correta.

-- ============================================================
-- LINGUAGENS
-- ============================================================

-- Questão 01 - Língua Portuguesa / Interpretação de Texto
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Em um texto argumentativo, o autor apresenta dados estatísticos para sustentar sua opinião. Qual é a principal função desses dados no texto?',
    'Os dados estatísticos funcionam como evidências que reforçam a argumentação e aumentam a credibilidade da tese apresentada.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Interpretação de Texto'
  AND discipline.name = 'Língua Portuguesa'
  AND discipline.exam_area = 'LINGUAGENS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Substituir completamente a opinião do autor.', FALSE),
        ('B', 'Apresentar informações sem relação com o tema.', FALSE),
        ('C', 'Sustentar a argumentação com evidências.', TRUE),
        ('D', 'Criar um efeito exclusivamente humorístico.', FALSE),
        ('E', 'Impedir a interpretação do leitor.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 02 - Língua Portuguesa / Gêneros Textuais
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Uma notícia jornalística tem como característica principal:',
    'A notícia busca informar sobre um fato relevante, geralmente com linguagem objetiva e organização que privilegia as informações essenciais.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Gêneros Textuais'
  AND discipline.name = 'Língua Portuguesa'
  AND discipline.exam_area = 'LINGUAGENS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Narrar fatos fictícios com linguagem poética.', FALSE),
        ('B', 'Informar sobre acontecimentos de interesse público.', TRUE),
        ('C', 'Defender necessariamente uma opinião pessoal.', FALSE),
        ('D', 'Apresentar apenas instruções ao leitor.', FALSE),
        ('E', 'Utilizar somente linguagem informal.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 03 - Língua Portuguesa / Figuras de Linguagem
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Na frase ''A cidade acordou triste naquela manhã'', ocorre principalmente:',
    'Atribuir uma característica ou ação humana a um elemento não humano caracteriza personificação ou prosopopeia.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Figuras de Linguagem'
  AND discipline.name = 'Língua Portuguesa'
  AND discipline.exam_area = 'LINGUAGENS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Metáfora.', FALSE),
        ('B', 'Hipérbole.', FALSE),
        ('C', 'Eufemismo.', FALSE),
        ('D', 'Personificação.', TRUE),
        ('E', 'Antítese.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 04 - Língua Portuguesa / Coesão e Coerência
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Em ''João estudou muito, portanto conseguiu bom resultado'', a palavra ''portanto'' estabelece relação de:',
    'O conectivo ''portanto'' introduz uma conclusão decorrente da informação anterior.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Coesão e Coerência'
  AND discipline.name = 'Língua Portuguesa'
  AND discipline.exam_area = 'LINGUAGENS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Causa.', FALSE),
        ('B', 'Conclusão.', TRUE),
        ('C', 'Oposição.', FALSE),
        ('D', 'Condição.', FALSE),
        ('E', 'Tempo.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 05 - Língua Portuguesa / Semântica
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Quando uma mesma palavra apresenta sentidos diferentes dependendo do contexto, ocorre:',
    'Polissemia é a propriedade de uma palavra possuir diferentes sentidos relacionados, definidos pelo contexto.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Semântica'
  AND discipline.name = 'Língua Portuguesa'
  AND discipline.exam_area = 'LINGUAGENS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Polissemia.', TRUE),
        ('B', 'Pleonasmo.', FALSE),
        ('C', 'Concordância.', FALSE),
        ('D', 'Aliteração.', FALSE),
        ('E', 'Regência.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 06 - Literatura / Modernismo
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Uma característica marcante da primeira fase do Modernismo brasileiro é:',
    'A primeira fase modernista valorizou a ruptura com modelos tradicionais, a experimentação e uma linguagem mais próxima do cotidiano.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Modernismo'
  AND discipline.name = 'Literatura'
  AND discipline.exam_area = 'LINGUAGENS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Valorização rígida dos modelos clássicos.', FALSE),
        ('B', 'Ruptura com padrões estéticos tradicionais.', TRUE),
        ('C', 'Retorno integral ao estilo medieval.', FALSE),
        ('D', 'Rejeição de temas nacionais.', FALSE),
        ('E', 'Uso obrigatório de versos alexandrinos.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 07 - Literatura / Literatura Brasileira
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'A literatura pode contribuir para a compreensão de uma sociedade porque:',
    'As obras literárias podem registrar valores, conflitos, visões de mundo e características históricas e culturais de diferentes épocas.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Literatura Brasileira'
  AND discipline.name = 'Literatura'
  AND discipline.exam_area = 'LINGUAGENS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Elimina as diferenças culturais.', FALSE),
        ('B', 'Registra aspectos históricos e culturais de diferentes épocas.', TRUE),
        ('C', 'Substitui integralmente documentos históricos.', FALSE),
        ('D', 'Apresenta somente fatos científicos.', FALSE),
        ('E', 'Impede interpretações distintas.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 08 - Língua Inglesa / Vocabulário em Contexto
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Na frase em inglês ''The project was challenging, but the team managed to finish it'', a palavra ''challenging'' indica que o projeto foi:',
    'No contexto, ''challenging'' significa algo difícil ou que exige esforço.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Vocabulário em Contexto'
  AND discipline.name = 'Língua Inglesa'
  AND discipline.exam_area = 'LINGUAGENS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Fácil.', FALSE),
        ('B', 'Desnecessário.', FALSE),
        ('C', 'Difícil.', TRUE),
        ('D', 'Curto.', FALSE),
        ('E', 'Barato.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 09 - Artes / História da Arte
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Uma das funções sociais da arte ao longo da história é:',
    'A arte pode expressar valores, crenças, conflitos e transformações culturais de diferentes sociedades.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'História da Arte'
  AND discipline.name = 'Artes'
  AND discipline.exam_area = 'LINGUAGENS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Representar e questionar aspectos culturais e sociais.', TRUE),
        ('B', 'Eliminar manifestações culturais locais.', FALSE),
        ('C', 'Servir apenas como decoração.', FALSE),
        ('D', 'Impedir mudanças de linguagem.', FALSE),
        ('E', 'Reproduzir sempre o mesmo padrão estético.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 10 - Tecnologias da Informação e Comunicação / Cultura Digital
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Em relação à circulação de informações nas redes sociais, uma atitude adequada é:',
    'Verificar a origem e a confiabilidade da informação ajuda a reduzir a disseminação de conteúdos falsos ou enganosos.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Cultura Digital'
  AND discipline.name = 'Tecnologias da Informação e Comunicação'
  AND discipline.exam_area = 'LINGUAGENS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Compartilhar imediatamente qualquer conteúdo recebido.', FALSE),
        ('B', 'Verificar a fonte e comparar informações antes de compartilhar.', TRUE),
        ('C', 'Considerar verdadeiro todo conteúdo com muitas curtidas.', FALSE),
        ('D', 'Ignorar a autoria da publicação.', FALSE),
        ('E', 'Compartilhar apenas pelo título.', FALSE)
) AS alternative(letter, text, correct);

-- ============================================================
-- CIÊNCIAS HUMANAS
-- ============================================================

-- Questão 01 - História / Brasil Colônia
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Durante o período colonial brasileiro, a economia açucareira concentrou-se principalmente:',
    'A produção de açúcar se destacou sobretudo no litoral nordestino, favorecida por condições naturais e pela estrutura colonial voltada à exportação.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Brasil Colônia'
  AND discipline.name = 'História'
  AND discipline.exam_area = 'CIENCIAS_HUMANAS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'No litoral nordestino.', TRUE),
        ('B', 'Na região amazônica.', FALSE),
        ('C', 'No interior do Sul.', FALSE),
        ('D', 'No Centro-Oeste.', FALSE),
        ('E', 'No extremo oeste paulista.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 02 - História / Era Vargas
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'A criação da Consolidação das Leis do Trabalho (CLT), em 1943, está associada ao governo de:',
    'A CLT foi instituída durante o governo de Getúlio Vargas e reuniu diversas normas de proteção ao trabalhador.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Era Vargas'
  AND discipline.name = 'História'
  AND discipline.exam_area = 'CIENCIAS_HUMANAS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Juscelino Kubitschek.', FALSE),
        ('B', 'Getúlio Vargas.', TRUE),
        ('C', 'Jânio Quadros.', FALSE),
        ('D', 'João Goulart.', FALSE),
        ('E', 'Eurico Gaspar Dutra.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 03 - História / Ditadura Militar no Brasil
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'O Ato Institucional nº 5 (AI-5), de 1968, representou:',
    'O AI-5 ampliou poderes do regime militar e intensificou mecanismos de repressão e restrição de direitos políticos.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Ditadura Militar no Brasil'
  AND discipline.name = 'História'
  AND discipline.exam_area = 'CIENCIAS_HUMANAS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Ampliação das liberdades políticas.', FALSE),
        ('B', 'Fim imediato da ditadura.', FALSE),
        ('C', 'Endurecimento do regime e aumento da repressão.', TRUE),
        ('D', 'Criação da Constituição de 1988.', FALSE),
        ('E', 'Retorno das eleições presidenciais diretas.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 04 - História / Revolução Francesa
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'O lema ''Liberdade, Igualdade e Fraternidade'' está associado historicamente à:',
    'O lema tornou-se um dos símbolos da Revolução Francesa e dos ideais políticos ligados à cidadania e à igualdade jurídica.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Revolução Francesa'
  AND discipline.name = 'História'
  AND discipline.exam_area = 'CIENCIAS_HUMANAS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Revolução Francesa.', TRUE),
        ('B', 'Revolução Russa.', FALSE),
        ('C', 'Reforma Protestante.', FALSE),
        ('D', 'Guerra Fria.', FALSE),
        ('E', 'Expansão marítima portuguesa.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 05 - História / Segunda Guerra Mundial
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'A Segunda Guerra Mundial terminou em 1945 após:',
    'O conflito terminou na Europa com a derrota da Alemanha nazista e, posteriormente, no Pacífico com a rendição japonesa.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Segunda Guerra Mundial'
  AND discipline.name = 'História'
  AND discipline.exam_area = 'CIENCIAS_HUMANAS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'A vitória das Potências do Eixo.', FALSE),
        ('B', 'A derrota das forças do Eixo.', TRUE),
        ('C', 'A dissolução imediata da ONU.', FALSE),
        ('D', 'O início da Primeira Guerra Mundial.', FALSE),
        ('E', 'A independência dos Estados Unidos.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 06 - Geografia / Geopolítica
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'O termo geopolítica refere-se principalmente ao estudo:',
    'A geopolítica analisa as relações entre poder, território, recursos e estratégias dos Estados e de outros atores.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Geopolítica'
  AND discipline.name = 'Geografia'
  AND discipline.exam_area = 'CIENCIAS_HUMANAS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Da formação de palavras.', FALSE),
        ('B', 'Das relações de poder associadas ao espaço geográfico.', TRUE),
        ('C', 'Apenas das condições climáticas.', FALSE),
        ('D', 'Somente da composição química do solo.', FALSE),
        ('E', 'Exclusivamente das espécies animais.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 07 - Geografia / Globalização
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Uma característica da globalização contemporânea é:',
    'A globalização intensifica os fluxos de mercadorias, capitais, informações e pessoas entre diferentes regiões do planeta.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Globalização'
  AND discipline.name = 'Geografia'
  AND discipline.exam_area = 'CIENCIAS_HUMANAS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Redução completa das trocas internacionais.', FALSE),
        ('B', 'Maior integração de fluxos econômicos e informacionais.', TRUE),
        ('C', 'Fim das tecnologias de comunicação.', FALSE),
        ('D', 'Isolamento crescente de todos os países.', FALSE),
        ('E', 'Desaparecimento das empresas multinacionais.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 08 - Geografia / Questões Ambientais
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'O desmatamento de grandes áreas florestais pode contribuir para:',
    'A remoção da cobertura vegetal reduz habitats, favorece perda de biodiversidade e pode alterar ciclos ambientais.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Questões Ambientais'
  AND discipline.name = 'Geografia'
  AND discipline.exam_area = 'CIENCIAS_HUMANAS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Aumento garantido da biodiversidade.', FALSE),
        ('B', 'Redução da erosão em qualquer situação.', FALSE),
        ('C', 'Perda de biodiversidade e alteração dos ecossistemas.', TRUE),
        ('D', 'Eliminação do efeito estufa.', FALSE),
        ('E', 'Aumento permanente da fertilidade dos solos.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 09 - Filosofia / Ética
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'A ética, como campo da filosofia, estuda principalmente:',
    'A ética investiga valores, princípios e critérios relacionados às ações humanas e à avaliação do que é considerado moralmente adequado.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Ética'
  AND discipline.name = 'Filosofia'
  AND discipline.exam_area = 'CIENCIAS_HUMANAS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Somente fenômenos meteorológicos.', FALSE),
        ('B', 'Princípios e valores relacionados à ação humana.', TRUE),
        ('C', 'A estrutura dos átomos.', FALSE),
        ('D', 'A formação de continentes.', FALSE),
        ('E', 'Exclusivamente técnicas de produção industrial.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 10 - Sociologia / Desigualdade Social
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'A desigualdade social pode ser entendida como:',
    'A desigualdade social envolve distribuição desigual de renda, oportunidades, serviços, direitos e recursos entre grupos sociais.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Desigualdade Social'
  AND discipline.name = 'Sociologia'
  AND discipline.exam_area = 'CIENCIAS_HUMANAS'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Distribuição igual de recursos entre todos.', FALSE),
        ('B', 'Diferenças no acesso a recursos e oportunidades entre grupos sociais.', TRUE),
        ('C', 'Ausência de diferenças econômicas.', FALSE),
        ('D', 'Fenômeno exclusivamente biológico.', FALSE),
        ('E', 'Situação restrita apenas ao ambiente escolar.', FALSE)
) AS alternative(letter, text, correct);

-- ============================================================
-- CIÊNCIAS DA NATUREZA
-- ============================================================

-- Questão 01 - Biologia / Ecologia
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Em uma cadeia alimentar, os produtores são organismos que:',
    'Produtores são seres autotróficos capazes de produzir matéria orgânica, geralmente por fotossíntese ou quimiossíntese.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Ecologia'
  AND discipline.name = 'Biologia'
  AND discipline.exam_area = 'CIENCIAS_DA_NATUREZA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Produzem seu próprio alimento.', TRUE),
        ('B', 'Alimentam-se exclusivamente de animais.', FALSE),
        ('C', 'Decompõem apenas metais.', FALSE),
        ('D', 'Não participam do fluxo de energia.', FALSE),
        ('E', 'São sempre fungos.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 02 - Biologia / Genética
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'O DNA tem como principal função:',
    'O DNA armazena e transmite informações genéticas utilizadas no desenvolvimento e funcionamento dos organismos.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Genética'
  AND discipline.name = 'Biologia'
  AND discipline.exam_area = 'CIENCIAS_DA_NATUREZA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Armazenar informações genéticas.', TRUE),
        ('B', 'Produzir energia luminosa.', FALSE),
        ('C', 'Transportar oxigênio diretamente.', FALSE),
        ('D', 'Digerir proteínas.', FALSE),
        ('E', 'Regular exclusivamente a temperatura corporal.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 03 - Biologia / Evolução
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'A seleção natural favorece, ao longo das gerações:',
    'Indivíduos com características hereditárias que aumentam sua aptidão tendem a deixar mais descendentes, tornando essas características mais frequentes.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Evolução'
  AND discipline.name = 'Biologia'
  AND discipline.exam_area = 'CIENCIAS_DA_NATUREZA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Características que aumentam a chance de sobrevivência e reprodução.', TRUE),
        ('B', 'Somente características adquiridas pelo uso.', FALSE),
        ('C', 'A eliminação de toda variabilidade genética.', FALSE),
        ('D', 'A permanência obrigatória de todas as características.', FALSE),
        ('E', 'A ausência de mudanças populacionais.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 04 - Biologia / Fisiologia Humana
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'A principal função das hemácias no sangue humano é:',
    'As hemácias possuem hemoglobina, proteína responsável principalmente pelo transporte de oxigênio.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Fisiologia Humana'
  AND discipline.name = 'Biologia'
  AND discipline.exam_area = 'CIENCIAS_DA_NATUREZA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Produzir anticorpos.', FALSE),
        ('B', 'Transportar oxigênio.', TRUE),
        ('C', 'Realizar digestão.', FALSE),
        ('D', 'Produzir hormônios.', FALSE),
        ('E', 'Formar neurônios.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 05 - Física / Cinemática
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Um carro percorre 120 km em 2 horas, mantendo velocidade média constante. Sua velocidade média é:',
    'A velocidade média é calculada pela razão entre distância e tempo: 120 km / 2 h = 60 km/h.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Cinemática'
  AND discipline.name = 'Física'
  AND discipline.exam_area = 'CIENCIAS_DA_NATUREZA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', '30 km/h.', FALSE),
        ('B', '40 km/h.', FALSE),
        ('C', '60 km/h.', TRUE),
        ('D', '120 km/h.', FALSE),
        ('E', '240 km/h.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 06 - Física / Dinâmica
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'De acordo com a segunda lei de Newton, a força resultante sobre um corpo é igual:',
    'A segunda lei de Newton é expressa por F = m·a, relacionando força resultante, massa e aceleração.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Dinâmica'
  AND discipline.name = 'Física'
  AND discipline.exam_area = 'CIENCIAS_DA_NATUREZA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'À massa dividida pela aceleração.', FALSE),
        ('B', 'Ao produto da massa pela aceleração.', TRUE),
        ('C', 'À velocidade multiplicada pelo tempo.', FALSE),
        ('D', 'À energia dividida pela massa.', FALSE),
        ('E', 'Ao volume multiplicado pela densidade.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 07 - Física / Eletricidade
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Em um circuito elétrico simples, a unidade de medida da corrente elétrica no Sistema Internacional é:',
    'A unidade de corrente elétrica no SI é o ampère (A).',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Eletricidade'
  AND discipline.name = 'Física'
  AND discipline.exam_area = 'CIENCIAS_DA_NATUREZA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Volt.', FALSE),
        ('B', 'Ohm.', FALSE),
        ('C', 'Watt.', FALSE),
        ('D', 'Ampère.', TRUE),
        ('E', 'Joule.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 08 - Química / Estrutura Atômica
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'A partícula subatômica que possui carga elétrica negativa é:',
    'O elétron possui carga elétrica negativa; prótons são positivos e nêutrons não possuem carga líquida.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Estrutura Atômica'
  AND discipline.name = 'Química'
  AND discipline.exam_area = 'CIENCIAS_DA_NATUREZA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Próton.', FALSE),
        ('B', 'Nêutron.', FALSE),
        ('C', 'Elétron.', TRUE),
        ('D', 'Núcleo.', FALSE),
        ('E', 'Íon neutro.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 09 - Química / Estequiometria
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'A estequiometria é utilizada principalmente para:',
    'A estequiometria permite calcular relações quantitativas entre reagentes e produtos em uma reação química.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Estequiometria'
  AND discipline.name = 'Química'
  AND discipline.exam_area = 'CIENCIAS_DA_NATUREZA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Calcular relações quantitativas em reações químicas.', TRUE),
        ('B', 'Classificar seres vivos.', FALSE),
        ('C', 'Medir distâncias astronômicas apenas.', FALSE),
        ('D', 'Determinar exclusivamente a cor das substâncias.', FALSE),
        ('E', 'Analisar movimentos sociais.', FALSE)
) AS alternative(letter, text, correct);

-- Questão 10 - Química / Química Orgânica
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Os hidrocarbonetos são compostos formados exclusivamente por:',
    'Hidrocarbonetos são compostos orgânicos constituídos apenas por átomos de carbono e hidrogênio.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Química Orgânica'
  AND discipline.name = 'Química'
  AND discipline.exam_area = 'CIENCIAS_DA_NATUREZA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'Carbono e oxigênio.', FALSE),
        ('B', 'Carbono e hidrogênio.', TRUE),
        ('C', 'Hidrogênio e nitrogênio.', FALSE),
        ('D', 'Carbono e sódio.', FALSE),
        ('E', 'Oxigênio e enxofre.', FALSE)
) AS alternative(letter, text, correct);

-- ============================================================
-- MATEMÁTICA
-- ============================================================

-- Questão 01 - Matemática / Matemática Básica
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Qual é o resultado de 15 + 27?',
    'Somando as unidades e dezenas, 15 + 27 = 42.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Matemática Básica'
  AND discipline.name = 'Matemática'
  AND discipline.exam_area = 'MATEMATICA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', '32', FALSE),
        ('B', '40', FALSE),
        ('C', '42', TRUE),
        ('D', '52', FALSE),
        ('E', '57', FALSE)
) AS alternative(letter, text, correct);

-- Questão 02 - Matemática / Razão e Proporção
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Se 3 cadernos custam R$ 18,00, quanto custarão 5 cadernos, mantendo o mesmo preço unitário?',
    'Cada caderno custa R$ 6,00. Logo, 5 cadernos custam 5 × 6 = R$ 30,00.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Razão e Proporção'
  AND discipline.name = 'Matemática'
  AND discipline.exam_area = 'MATEMATICA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'R$ 24,00', FALSE),
        ('B', 'R$ 28,00', FALSE),
        ('C', 'R$ 30,00', TRUE),
        ('D', 'R$ 32,00', FALSE),
        ('E', 'R$ 36,00', FALSE)
) AS alternative(letter, text, correct);

-- Questão 03 - Matemática / Porcentagem
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Um produto de R$ 200,00 recebeu desconto de 10%. Qual é o novo preço?',
    '10% de R$ 200,00 corresponde a R$ 20,00. Subtraindo o desconto, o preço final é R$ 180,00.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Porcentagem'
  AND discipline.name = 'Matemática'
  AND discipline.exam_area = 'MATEMATICA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'R$ 160,00', FALSE),
        ('B', 'R$ 170,00', FALSE),
        ('C', 'R$ 180,00', TRUE),
        ('D', 'R$ 190,00', FALSE),
        ('E', 'R$ 220,00', FALSE)
) AS alternative(letter, text, correct);

-- Questão 04 - Matemática / Equações
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Qual é a solução da equação 2x + 6 = 20?',
    'Subtraindo 6 dos dois lados: 2x = 14. Dividindo por 2: x = 7.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Equações'
  AND discipline.name = 'Matemática'
  AND discipline.exam_area = 'MATEMATICA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', '5', FALSE),
        ('B', '6', FALSE),
        ('C', '7', TRUE),
        ('D', '8', FALSE),
        ('E', '13', FALSE)
) AS alternative(letter, text, correct);

-- Questão 05 - Matemática / Funções
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Dada a função f(x) = 2x + 3, qual é o valor de f(4)?',
    'Substituindo x por 4: f(4) = 2·4 + 3 = 11.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Funções'
  AND discipline.name = 'Matemática'
  AND discipline.exam_area = 'MATEMATICA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', '8', FALSE),
        ('B', '9', FALSE),
        ('C', '10', FALSE),
        ('D', '11', TRUE),
        ('E', '14', FALSE)
) AS alternative(letter, text, correct);

-- Questão 06 - Matemática / Geometria Plana
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Um retângulo possui base de 8 cm e altura de 5 cm. Qual é sua área?',
    'A área do retângulo é base × altura: 8 × 5 = 40 cm².',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Geometria Plana'
  AND discipline.name = 'Matemática'
  AND discipline.exam_area = 'MATEMATICA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', '13 cm²', FALSE),
        ('B', '26 cm²', FALSE),
        ('C', '40 cm²', TRUE),
        ('D', '80 cm²', FALSE),
        ('E', '160 cm²', FALSE)
) AS alternative(letter, text, correct);

-- Questão 07 - Matemática / Probabilidade
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Ao lançar um dado comum de seis faces, qual é a probabilidade de obter o número 6?',
    'Há 1 resultado favorável entre 6 resultados igualmente possíveis, portanto a probabilidade é 1/6.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Probabilidade'
  AND discipline.name = 'Matemática'
  AND discipline.exam_area = 'MATEMATICA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', '1/2', FALSE),
        ('B', '1/3', FALSE),
        ('C', '1/4', FALSE),
        ('D', '1/6', TRUE),
        ('E', '5/6', FALSE)
) AS alternative(letter, text, correct);

-- Questão 08 - Matemática / Estatística
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Considere os valores 4, 6, 8 e 10. Qual é a média aritmética?',
    'A soma é 28 e existem 4 valores. Assim, 28 / 4 = 7.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Estatística'
  AND discipline.name = 'Matemática'
  AND discipline.exam_area = 'MATEMATICA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', '6', FALSE),
        ('B', '7', TRUE),
        ('C', '8', FALSE),
        ('D', '9', FALSE),
        ('E', '10', FALSE)
) AS alternative(letter, text, correct);

-- Questão 09 - Matemática / Análise Combinatória
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Uma pessoa possui 3 camisetas e 2 calças diferentes. Quantas combinações distintas de uma camiseta com uma calça podem ser formadas?',
    'Pelo princípio multiplicativo, existem 3 × 2 = 6 combinações.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Análise Combinatória'
  AND discipline.name = 'Matemática'
  AND discipline.exam_area = 'MATEMATICA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', '5', FALSE),
        ('B', '6', TRUE),
        ('C', '8', FALSE),
        ('D', '9', FALSE),
        ('E', '12', FALSE)
) AS alternative(letter, text, correct);

-- Questão 10 - Matemática / Matemática Financeira
WITH inserted_question AS (
INSERT INTO questions (
    statement,
    explanation,
    subject_id,
    active,
    created_at,
    updated_at
)
SELECT
    'Um capital de R$ 1.000,00 aplicado a juros simples de 2% ao mês durante 3 meses gera quanto de juros?',
    'Em juros simples, J = C·i·t = 1000 × 0,02 × 3 = R$ 60,00.',
    subject.id,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
FROM subjects subject
         JOIN disciplines discipline
              ON discipline.id = subject.discipline_id
WHERE subject.name = 'Matemática Financeira'
  AND discipline.name = 'Matemática'
  AND discipline.exam_area = 'MATEMATICA'
    RETURNING id
)
INSERT INTO alternatives (
    question_id,
    letter,
    text,
    correct
)
SELECT
    inserted_question.id,
    alternative.letter,
    alternative.text,
    alternative.correct
FROM inserted_question
         CROSS JOIN (
    VALUES
        ('A', 'R$ 20,00', FALSE),
        ('B', 'R$ 40,00', FALSE),
        ('C', 'R$ 60,00', TRUE),
        ('D', 'R$ 80,00', FALSE),
        ('E', 'R$ 120,00', FALSE)
) AS alternative(letter, text, correct);

