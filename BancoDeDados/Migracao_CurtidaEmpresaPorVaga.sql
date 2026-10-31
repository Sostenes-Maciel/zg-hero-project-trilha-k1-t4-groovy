-- Permite que a curtida da empresa seja vinculada ao candidato e à vaga escolhida.
-- Curtidas antigas da empresa (id_vaga NULL) continuam válidas como registros legados.
ALTER TABLE curtida
    DROP CONSTRAINT IF EXISTS curtida_valida;

ALTER TABLE curtida
    ADD CONSTRAINT curtida_valida CHECK (
        (id_vaga IS NOT NULL AND cnpj_empresa IS NULL) OR
        (id_vaga IS NULL AND cnpj_empresa IS NOT NULL) OR
        (id_vaga IS NOT NULL AND cnpj_empresa IS NOT NULL)
    );

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'curtida_vaga_empresa_fk'
          AND conrelid = 'curtida'::regclass
    ) THEN
        ALTER TABLE curtida
            ADD CONSTRAINT curtida_vaga_empresa_fk
            FOREIGN KEY (id_vaga, cnpj_empresa)
            REFERENCES vaga (id_vaga, cnpj_empresa)
            ON DELETE CASCADE;
    END IF;
END
$$;

CREATE UNIQUE INDEX IF NOT EXISTS curtida_empresa_candidato_vaga_unica
    ON curtida (cpf_candidato, cnpj_empresa, id_vaga)
    WHERE cnpj_empresa IS NOT NULL AND id_vaga IS NOT NULL;
