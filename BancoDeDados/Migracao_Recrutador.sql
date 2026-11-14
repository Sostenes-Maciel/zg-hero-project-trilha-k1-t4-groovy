-- Migração incremental da feature Recrutador.
-- Aplicar após BancoDeDados/LinkeTinder_BD.sql.

CREATE TABLE IF NOT EXISTS recrutador (
    id_recrutador SERIAL PRIMARY KEY,
    empresa_id VARCHAR(18) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    area_atuacao VARCHAR(50) NOT NULL,
    nivel_permissao VARCHAR(10) NOT NULL DEFAULT 'Limitado',
    descricao TEXT,
    email VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    data_nascimento DATE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT recrutador_empresa_fk
        FOREIGN KEY (empresa_id)
        REFERENCES empresa(cnpj)
        ON DELETE RESTRICT,
    CONSTRAINT recrutador_email_unico UNIQUE (email),
    CONSTRAINT recrutador_cpf_unico UNIQUE (cpf),
    CONSTRAINT recrutador_nivel_permissao_valido
        CHECK (nivel_permissao IN ('Total', 'Limitado')),
    CONSTRAINT recrutador_area_atuacao_valida
        CHECK (area_atuacao IN ('TI', 'RH', 'Marketing', 'Vendas', 'Segurança'))
);

ALTER TABLE recrutador
    ADD COLUMN IF NOT EXISTS ativo BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE curtida
    ADD COLUMN IF NOT EXISTS recrutador_id INTEGER;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'curtida_recrutador_fk'
          AND conrelid = 'curtida'::regclass
    ) THEN
        ALTER TABLE curtida
            ADD CONSTRAINT curtida_recrutador_fk
            FOREIGN KEY (recrutador_id)
            REFERENCES recrutador(id_recrutador)
            ON DELETE SET NULL;
    END IF;
END
$$;

ALTER TABLE vaga
    ADD COLUMN IF NOT EXISTS area_atuacao VARCHAR(50);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'vaga_area_atuacao_valida'
          AND conrelid = 'vaga'::regclass
    ) THEN
        ALTER TABLE vaga
            ADD CONSTRAINT vaga_area_atuacao_valida
            CHECK (
                area_atuacao IS NULL
                OR area_atuacao IN ('TI', 'RH', 'Marketing', 'Vendas', 'Segurança')
            );
    END IF;
END
$$;
