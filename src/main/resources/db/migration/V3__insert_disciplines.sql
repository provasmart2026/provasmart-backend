INSERT INTO disciplines (name, exam_area_id)
SELECT discipline.name, exam_area.id
FROM (VALUES ('Língua Portuguesa', 'Linguagens'),
             ('Literatura', 'Linguagens'),
             ('Língua Inglesa', 'Linguagens'),
             ('Língua Espanhola', 'Linguagens'),
             ('Artes', 'Linguagens'),
             ('Educação Física', 'Linguagens'),
             ('Tecnologias da Informação e Comunicação', 'Linguagens'),

             ('História', 'Ciências Humanas'),
             ('Geografia', 'Ciências Humanas'),
             ('Filosofia', 'Ciências Humanas'),
             ('Sociologia', 'Ciências Humanas'),

             ('Biologia', 'Ciências da Natureza'),
             ('Física', 'Ciências da Natureza'),
             ('Química', 'Ciências da Natureza'),

             ('Matemática', 'Matemática')) AS discipline(name, area_name)
         JOIN exam_areas exam_area
              ON exam_area.name = discipline.area_name;