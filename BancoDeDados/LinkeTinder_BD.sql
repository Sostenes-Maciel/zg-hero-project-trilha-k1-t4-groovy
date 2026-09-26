-- 1 CRIAÇÃO DAS TABELAS

CREATE TABLE candidato (
    cpf VARCHAR(14) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    pais VARCHAR(100),
    estado VARCHAR(100),
    cep VARCHAR(20),
    descricao TEXT,
    idade INT
);

CREATE TABLE empresa (
    cnpj VARCHAR(18) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    pais VARCHAR(100),
    estado VARCHAR(100),
    cep VARCHAR(20),
    descricao TEXT
);

CREATE TABLE competencia (
    id_competencia SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE vaga (
    id_vaga SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    pais VARCHAR(100),
    estado VARCHAR(100),
    descricao TEXT,
    cnpj_empresa VARCHAR(18) NOT NULL,
    UNIQUE (id_vaga, cnpj_empresa),
    FOREIGN KEY (cnpj_empresa) REFERENCES empresa(cnpj) ON DELETE CASCADE
);

CREATE TABLE candidato_competencia (
    cpf_candidato VARCHAR(14),
    id_competencia INT,
    PRIMARY KEY (cpf_candidato, id_competencia),
    FOREIGN KEY (cpf_candidato) REFERENCES candidato(cpf) ON DELETE CASCADE,
    FOREIGN KEY (id_competencia) REFERENCES competencia(id_competencia) ON DELETE CASCADE
);

CREATE TABLE empresa_competencia (
    cnpj_empresa VARCHAR(18),
    id_competencia INT,
    PRIMARY KEY (cnpj_empresa, id_competencia),
    FOREIGN KEY (cnpj_empresa) REFERENCES empresa(cnpj) ON DELETE CASCADE,
    FOREIGN KEY (id_competencia) REFERENCES competencia(id_competencia) ON DELETE CASCADE
);

CREATE TABLE vaga_competencia (
    id_vaga INT,
    id_competencia INT,
    PRIMARY KEY (id_vaga, id_competencia),
    FOREIGN KEY (id_vaga) REFERENCES vaga(id_vaga) ON DELETE CASCADE,
    FOREIGN KEY (id_competencia) REFERENCES competencia(id_competencia) ON DELETE CASCADE
);

CREATE TABLE curtida (
    id_curtida SERIAL PRIMARY KEY,
    cpf_candidato VARCHAR(14) NOT NULL,
    id_vaga INT,
    cnpj_empresa VARCHAR(18),
    CONSTRAINT curtida_valida CHECK (
        (id_vaga IS NOT NULL AND cnpj_empresa IS NULL) OR 
        (id_vaga IS NULL AND cnpj_empresa IS NOT NULL)
    ),
    FOREIGN KEY (cpf_candidato) REFERENCES candidato(cpf) ON DELETE CASCADE,
    FOREIGN KEY (id_vaga) REFERENCES vaga(id_vaga) ON DELETE CASCADE,
    FOREIGN KEY (cnpj_empresa) REFERENCES empresa(cnpj) ON DELETE CASCADE
);

CREATE TABLE match (
    id_match SERIAL PRIMARY KEY,
    cpf_candidato VARCHAR(14) NOT NULL,
    cnpj_empresa VARCHAR(18) NOT NULL,
    id_vaga INT NOT NULL,
    UNIQUE (cpf_candidato, cnpj_empresa, id_vaga),
    FOREIGN KEY (cpf_candidato) REFERENCES candidato(cpf) ON DELETE CASCADE,
    FOREIGN KEY (cnpj_empresa) REFERENCES empresa(cnpj) ON DELETE CASCADE,
    FOREIGN KEY (id_vaga) REFERENCES vaga(id_vaga) ON DELETE CASCADE,
    FOREIGN KEY (id_vaga, cnpj_empresa) REFERENCES vaga(id_vaga, cnpj_empresa) ON DELETE CASCADE
);


--- 2 INSERT

-- Inserindo os 5 Candidatos
INSERT INTO candidato (cpf, nome, email, pais, estado, cep, descricao, idade) VALUES
('123.456.789-00', 'Sóstenes Maciel', 'sosmarques@hotmail.com', 'Brasil', 'PE', '55299-300', 'Estagiário ZG.', 29),
('111.222.333-49', 'José da Silva', 'jose@outlook.com', 'Brasil', 'SP', '11222-333', 'Estudante de ciências da computação', 21),
('123.222.433-00', 'Paulo André', 'paulo@hotmail.com', 'Brasil', 'RJ', '55210-300', 'Estagiário de TI.', 33),
('333.444.789-00', 'Ana Julia', 'anajj@hotmail.com', 'Brasil', 'BA', '88855-000', 'Recém formada em redes.', 20),
('333.666.768-00', 'Vitor Pereira', 'vitor@hotmail.com', 'Brasil', 'GO', '65432-300', 'Tutor acelera.', 40);

-- Inserindo as 5 Empresas
INSERT INTO empresa (cnpj, nome, email, pais, estado, cep, descricao) VALUES
('98.765.432/0001-11', 'Tech Global', 'vagastech@gmail.com', 'Brasil', 'RJ', '32133-77', 'Consultoria para TI internacional'),
('22.344.543/0001-11', 'S.O.S enterprises', 'sosss@gmail.com', 'Estados Unidos', 'Los Angeles', '89076', 'Consultoria para TI internacional'),
('45.678.901/0001-34', 'BlueSky Technologies', 'contato@blueskytech.com', 'Estados Unidos', 'California', '90210', 'Desenvolvimento de plataformas digitais e sistemas web'),
('56.789.012/0001-45', 'GreenCode Solutions', 'vagas@greencode.co.uk', 'Reino Unido', 'England', 'SW1A 1AA', 'Consultoria em tecnologia e desenvolvimento de software'),
('67.890.123/0001-56', 'Tokyo Digital Labs', 'jobs@tokyodigital.jp', 'Japão', 'Tokyo', '100-0001', 'Soluções de inteligência artificial e aplicações corporativas');

-- Inserindo as Competências (Unificadas)
INSERT INTO competencia (nome) VALUES 
('Java'), ( 'Groovy'), ('Python'), ('Web design'), ('Banco de dados'), 
('JavaScript'), ('Gradle'), ('Metodologias Ágeis'), ('Web'), ('Angular'), 
('TypeScript'), ('Node.js'), ('Linux'), ('Spring Boot'), ('MySQL'), 
('Django'), ('React'), ('AWS'), ('Git'), ('SQL'), ('English');

-- Inserindo as 6 Vagas
INSERT INTO vaga (titulo, pais, estado, descricao, cnpj_empresa) VALUES
('Desenvolvedor Full Stack', 'Brasil', 'PE', 'Atuação no desenvolvimento de aplicações web, integração com APIs e bancos de dados.', '98.765.432/0001-11'),
('Desenvolvedor Java Júnior', 'Brasil', 'SP', 'Profissional em início de carreira focado em criar, corrigir e manter sistemas back-end utilizando a linguagem Java.', '22.344.543/0001-11'),
('Desenvolvedor Backend Node.js', 'Brasil', 'PE', 'Atuação no desenvolvimento e manutenção de aplicações backend utilizando Node.js.', '45.678.901/0001-34'),
('Desenvolvedor Java Spring Boot', 'Brasil', 'PE', 'Desenvolvimento e manutenção de aplicações backend utilizando Java e Spring Boot.', '56.789.012/0001-45'),
('Desenvolvedor Python', 'Brasil', 'PE', 'Desenvolvimento de aplicações e soluções utilizando Python, trabalhando na criação de funcionalidades.', '67.890.123/0001-56'),
('Desenvolvedor TypeScript', 'Brasil', 'PE', 'Desenvolvimento de aplicações utilizando TypeScript e JavaScript.', '67.890.123/0001-56');

-- Vinculando Candidatos e Competências
INSERT INTO candidato_competencia (cpf_candidato, id_competencia) VALUES
('123.456.789-00', 1), ('123.456.789-00', 2), ('123.456.789-00', 3), -- Sóstenes: Java, Groovy, Python
('111.222.333-49', 4), ('111.222.333-49', 5), ('111.222.333-49', 6), -- José: Web design, BD, JS
('123.222.433-00', 1), ('123.222.433-00', 7), ('123.222.433-00', 8), -- Paulo: Java, Gradle, Ágil
('333.444.789-00', 3), ('333.444.789-00', 2), ('333.444.789-00', 9), -- Ana: Python, Groovy, Web
('333.666.768-00', 1), ('333.666.768-00', 6), ('333.666.768-00', 10);-- Vitor: Java, JS, Angular

-- Vinculando Empresas e Competências
INSERT INTO empresa_competencia (cnpj_empresa, id_competencia) VALUES
('98.765.432/0001-11', 10), ('98.765.432/0001-11', 6), ('98.765.432/0001-11', 11), -- Tech Global
('22.344.543/0001-11', 12), ('22.344.543/0001-11', 1), ('22.344.543/0001-11', 13), -- S.O.S
('45.678.901/0001-34', 1), ('45.678.901/0001-34', 14), ('45.678.901/0001-34', 15), -- BlueSky
('56.789.012/0001-45', 3), ('56.789.012/0001-45', 16), ('56.789.012/0001-45', 17), -- GreenCode
('67.890.123/0001-56', 6), ('67.890.123/0001-56', 11), ('67.890.123/0001-56', 18); -- Tokyo

-- Vinculando Vagas e Competências Exigidas
INSERT INTO vaga_competencia (id_vaga, id_competencia) VALUES
(1, 1), (1, 11), (1, 19), (1, 20), -- Vaga Full Stack (Tech Global)
(2, 1), (2, 10), (2, 17), (2, 21), -- Vaga Java Jr (S.O.S)
(3, 12), (3, 6), (3, 19),          -- Vaga Node.js (BlueSky)
(4, 1), (4, 14), (4, 19),          -- Vaga Spring Boot (GreenCode)
(5, 3), (5, 19), (5, 20),          -- Vaga Python (Tokyo)
(6, 11), (6, 6), (6, 19);          -- Vaga TS (Tokyo)

-- Testando interações: Algumas curtidas e matches para validação
INSERT INTO curtida (cpf_candidato, id_vaga, cnpj_empresa) VALUES 
('123.456.789-00', 1, NULL), -- Sóstenes curtiu a vaga Full Stack
('333.444.789-00', NULL, '45.678.901/0001-34'); -- Ana curtiu a empresa BlueSky

INSERT INTO match (cpf_candidato, cnpj_empresa, id_vaga) VALUES
('123.456.789-00', '98.765.432/0001-11', 1); -- Match entre Sóstenes e Tech Global na vaga 1


