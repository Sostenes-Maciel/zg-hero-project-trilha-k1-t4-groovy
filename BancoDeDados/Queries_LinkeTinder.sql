
-- Query 1: Listar candidatos e suas respectivas competências
SELECT c.nome AS candidato, comp.nome AS competencia
FROM candidato c
JOIN candidato_competencia cc ON c.cpf = cc.cpf_candidato
JOIN competencia comp ON cc.id_competencia = comp.id_competencia;

-- Variação query 1: Todas as competências respectivas ao condidato na mesma linha
SELECT
    c.nome AS candidato,
    STRING_AGG(comp.nome, ', ') AS competencias
FROM candidato c
JOIN candidato_competencia cc ON c.cpf = cc.cpf_candidato
JOIN competencia comp ON cc.id_competencia = comp.id_competencia
GROUP BY c.nome;

-- Query 2: Listar as vagas, incluindo nome da empresa e o país
SELECT v.titulo AS vaga, e.nome AS empresa, v.pais
FROM vaga v
JOIN empresa e ON v.cnpj_empresa = e.cnpj;

-- Query 3: Listar vagas que exigem a competência 'Java'
SELECT v.titulo AS vaga, e.nome AS empresa
FROM vaga v
JOIN vaga_competencia vc ON v.id_vaga = vc.id_vaga
JOIN competencia c ON vc.id_competencia = c.id_competencia
JOIN empresa e ON v.cnpj_empresa = e.cnpj
WHERE c.nome = 'Java';

-- Query 4: Exibir os matches realizados
SELECT c.nome AS candidato, e.nome AS empresa, v.titulo AS vaga
FROM match m
JOIN candidato c ON m.cpf_candidato = c.cpf
JOIN empresa e ON m.cnpj_empresa = e.cnpj
JOIN vaga v ON m.id_vaga = v.id_vaga;
