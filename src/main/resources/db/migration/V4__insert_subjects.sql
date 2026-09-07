INSERT INTO subjects (name, discipline_id)
SELECT subject.name, discipline.id
FROM (
         VALUES
             ('Interpretação de Texto', 'Língua Portuguesa'),
             ('Gêneros Textuais', 'Língua Portuguesa'),
             ('Funções da Linguagem', 'Língua Portuguesa'),
             ('Figuras de Linguagem', 'Língua Portuguesa'),
             ('Variação Linguística', 'Língua Portuguesa'),
             ('Coesão e Coerência', 'Língua Portuguesa'),
             ('Semântica', 'Língua Portuguesa'),
             ('Gramática Aplicada ao Texto', 'Língua Portuguesa'),

             ('Escolas Literárias', 'Literatura'),
             ('Literatura Brasileira', 'Literatura'),
             ('Modernismo', 'Literatura'),
             ('Romantismo', 'Literatura'),
             ('Realismo e Naturalismo', 'Literatura'),
             ('Poesia', 'Literatura'),
             ('Interpretação de Textos Literários', 'Literatura'),


             ('Interpretação de Texto', 'Língua Inglesa'),
             ('Vocabulário em Contexto', 'Língua Inglesa'),
             ('Gêneros Textuais', 'Língua Inglesa'),
             ('Estratégias de Leitura', 'Língua Inglesa'),
             ('Aspectos Culturais', 'Língua Inglesa'),

             ('Interpretação de Texto', 'Língua Espanhola'),
             ('Vocabulário em Contexto', 'Língua Espanhola'),
             ('Gêneros Textuais', 'Língua Espanhola'),
             ('Estratégias de Leitura', 'Língua Espanhola'),
             ('Aspectos Culturais', 'Língua Espanhola'),


             ('História da Arte', 'Artes'),
             ('Arte Brasileira', 'Artes'),
             ('Arte Contemporânea', 'Artes'),
             ('Artes Visuais', 'Artes'),
             ('Música', 'Artes'),
             ('Teatro e Dança', 'Artes'),


             ('Esporte e Sociedade', 'Educação Física'),
             ('Corpo e Cultura', 'Educação Física'),
             ('Saúde e Qualidade de Vida', 'Educação Física'),
             ('Práticas Corporais', 'Educação Física'),
             ('Lazer', 'Educação Física'),


             ('Cultura Digital', 'Tecnologias da Informação e Comunicação'),
             ('Comunicação Digital', 'Tecnologias da Informação e Comunicação'),
             ('Mídias e Informação', 'Tecnologias da Informação e Comunicação'),
             ('Linguagens Digitais', 'Tecnologias da Informação e Comunicação'),
             ('Tecnologia e Sociedade', 'Tecnologias da Informação e Comunicação'),


             ('Brasil Colônia', 'História'),
             ('Brasil Império', 'História'),
             ('Primeira República', 'História'),
             ('Era Vargas', 'História'),
             ('Ditadura Militar no Brasil', 'História'),
             ('Redemocratização do Brasil', 'História'),
             ('Antiguidade', 'História'),
             ('Idade Média', 'História'),
             ('Idade Moderna', 'História'),
             ('Revolução Francesa', 'História'),
             ('Revolução Industrial', 'História'),
             ('Primeira Guerra Mundial', 'História'),
             ('Segunda Guerra Mundial', 'História'),
             ('Guerra Fria', 'História'),


             ('Cartografia', 'Geografia'),
             ('Geopolítica', 'Geografia'),
             ('Globalização', 'Geografia'),
             ('Urbanização', 'Geografia'),
             ('População e Demografia', 'Geografia'),
             ('Climatologia', 'Geografia'),
             ('Relevo', 'Geografia'),
             ('Questões Ambientais', 'Geografia'),
             ('Agropecuária', 'Geografia'),
             ('Industrialização', 'Geografia'),
             ('Fontes de Energia', 'Geografia'),


             ('Filosofia Antiga', 'Filosofia'),
             ('Filosofia Medieval', 'Filosofia'),
             ('Filosofia Moderna', 'Filosofia'),
             ('Filosofia Contemporânea', 'Filosofia'),
             ('Ética', 'Filosofia'),
             ('Filosofia Política', 'Filosofia'),
             ('Teoria do Conhecimento', 'Filosofia'),


             ('Cultura e Sociedade', 'Sociologia'),
             ('Trabalho e Sociedade', 'Sociologia'),
             ('Desigualdade Social', 'Sociologia'),
             ('Movimentos Sociais', 'Sociologia'),
             ('Cidadania', 'Sociologia'),
             ('Poder e Política', 'Sociologia'),
             ('Identidade e Diversidade', 'Sociologia'),


             ('Ecologia', 'Biologia'),
             ('Genética', 'Biologia'),
             ('Evolução', 'Biologia'),
             ('Citologia', 'Biologia'),
             ('Fisiologia Humana', 'Biologia'),
             ('Botânica', 'Biologia'),
             ('Zoologia', 'Biologia'),
             ('Microbiologia', 'Biologia'),
             ('Biotecnologia', 'Biologia'),
             ('Saúde e Doenças', 'Biologia'),


             ('Cinemática', 'Física'),
             ('Dinâmica', 'Física'),
             ('Trabalho e Energia', 'Física'),
             ('Gravitação', 'Física'),
             ('Hidrostática', 'Física'),
             ('Termologia', 'Física'),
             ('Ondulatória', 'Física'),
             ('Óptica', 'Física'),
             ('Eletricidade', 'Física'),
             ('Magnetismo', 'Física'),


             ('Estrutura Atômica', 'Química'),
             ('Tabela Periódica', 'Química'),
             ('Ligações Químicas', 'Química'),
             ('Funções Inorgânicas', 'Química'),
             ('Reações Químicas', 'Química'),
             ('Estequiometria', 'Química'),
             ('Soluções', 'Química'),
             ('Termoquímica', 'Química'),
             ('Eletroquímica', 'Química'),
             ('Química Orgânica', 'Química'),


             ('Matemática Básica', 'Matemática'),
             ('Razão e Proporção', 'Matemática'),
             ('Porcentagem', 'Matemática'),
             ('Equações', 'Matemática'),
             ('Funções', 'Matemática'),
             ('Progressões', 'Matemática'),
             ('Geometria Plana', 'Matemática'),
             ('Geometria Espacial', 'Matemática'),
             ('Geometria Analítica', 'Matemática'),
             ('Trigonometria', 'Matemática'),
             ('Probabilidade', 'Matemática'),
             ('Estatística', 'Matemática'),
             ('Análise Combinatória', 'Matemática'),
             ('Matemática Financeira', 'Matemática')

     ) AS subject(name, discipline_name)

         JOIN disciplines discipline
              ON discipline.name = subject.discipline_name;