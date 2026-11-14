# Changelog

Todas as mudanças relevantes deste projeto serão documentadas neste arquivo.

O formato é baseado no [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto segue o versionamento semântico.

## [Não lançado]

### Adicionado

- Especificação inicial da feature Recrutador em `docs/specs/recrutador/spec.md`,
  incluindo modelo de dados, permissões, anonimato e critérios de aceite.
- Plano de implementação da feature Recrutador em
  `docs/specs/recrutador/plano.md`, organizado por banco, back-end e front-end.
- Plano da feature Recrutador detalhado em sete tarefas incrementais,
  separando banco, modelos/DAOs, serviços, rotas, modelos/serviços de front-end
  e telas.
- Script `BancoDeDados/Migracao_Recrutador.sql` criado e aplicado com sucesso,
  adicionando a tabela de Recrutadores, vínculo com Empresas, autoria de
  curtidas e área de atuação das vagas.
- Modelos, contratos e DAOs de Recrutador implementados, com consultas sem
  `senha_hash`, autoria opcional em curtidas e proteção contra duplicidade de
  matches.
- Testes Spock de modelo, `RecrutadorDAO` e autoria de curtidas adicionados e
  executados com sucesso.

### Modificado

### Corrigido

### Removido
