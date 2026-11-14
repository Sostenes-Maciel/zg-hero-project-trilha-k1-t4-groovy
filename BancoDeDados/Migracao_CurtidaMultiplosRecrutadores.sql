-- Remove a unicidade legada que não considerava a autoria da curtida.
DROP INDEX IF EXISTS curtida_empresa_candidato_vaga_unica;
ALTER TABLE curtida
    DROP CONSTRAINT IF EXISTS curtida_cpf_candidato_cnpj_empresa_id_vaga_key;

-- PostgreSQL permite múltiplos NULL em índices UNIQUE, preservando curtidas legadas.
CREATE UNIQUE INDEX IF NOT EXISTS curtida_empresa_candidato_vaga_recrutador_unica
    ON curtida (cpf_candidato, cnpj_empresa, id_vaga, recrutador_id);
