INSERT INTO disciplines (name, exam_area)
SELECT discipline.name, discipline.exam_area
FROM (VALUES ('Língua Portuguesa', 'LINGUAGENS'),
             ('Literatura', 'LINGUAGENS'),
             ('Língua Inglesa', 'LINGUAGENS'),
             ('Língua Espanhola', 'LINGUAGENS'),
             ('Artes', 'LINGUAGENS'),
             ('Educação Física', 'LINGUAGENS'),
             ('Tecnologias da Informação e Comunicação', 'LINGUAGENS'),

             ('História', 'CIENCIAS_HUMANAS'),
             ('Geografia', 'CIENCIAS_HUMANAS'),
             ('Filosofia', 'CIENCIAS_HUMANAS'),
             ('Sociologia', 'CIENCIAS_HUMANAS'),

             ('Biologia', 'CIENCIAS_DA_NATUREZA'),
             ('Física', 'CIENCIAS_DA_NATUREZA'),
             ('Química', 'CIENCIAS_DA_NATUREZA'),

             ('Matemática', 'MATEMATICA')) AS discipline(name, exam_area);
