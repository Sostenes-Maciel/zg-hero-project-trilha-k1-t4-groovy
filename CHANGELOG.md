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
- Serviços de validação, autenticação, autorização por nível/área e curtidas
  com autoria de recrutador implementados na Tarefa 3.
- Validação de nomes e textos com letras e espaços adicionada à Tarefa 3.
- Controllers e servlets da Tarefa 4 adicionados, com autenticação de
  recrutadores, autorização de vagas e respostas anonimizadas antes do match.
- Testes web para autenticação, autorização HTTP e proteção de anonimato
  adicionados.
- Modelos TypeScript de Recrutador, curtida e match ampliados com autoria,
  além dos serviços de validação, cadastro e autenticação da Tarefa 5.
- Sessão de recrutador implementada sem persistir senha, hash, CPF ou data de
  nascimento no armazenamento do navegador.
- Telas de cadastro, login e perfil do Recrutador adicionadas com as classes
  visuais existentes e acesso de cadastro restrito à Empresa logada.
- Menu integrado ao fluxo de autenticação e perfil do Recrutador, mantendo
  CPF, data de nascimento e hash fora da interface.
- Migração `BancoDeDados/Migracao_CurtidaMultiplosRecrutadores.sql` para
  permitir múltiplas curtidas da mesma empresa por recrutadores distintos,
  preservando a compatibilidade com registros legados.
- Telas próprias do Recrutador para vagas, candidatos e matches, com escopo
  por área/empresa, controles de edição por permissão e deduplicação de
  matches por candidato, empresa e vaga.

### Modificado

### Corrigido

- Corrigida a aba “Vagas da minha área” do Recrutador: a listagem passou a
  consultar as vagas da empresa associada e comparar a área da vaga com a área
  do Recrutador, sem depender do objeto de empresa removido pela anonimização.
- Adicionado o campo independente de área da vaga no cadastro, com persistência
  em `vaga.area_atuacao`; o perfil do Recrutador agora filtra candidatos pelas
  competências das vagas da área do próprio Recrutador, sem reutilizar essa
  área para preencher ou restringir o cadastro da vaga.
- Ajustado o perfil do Recrutador para consultar e exibir o nome da empresa
  associada, sem revelar o CNPJ na interface.
- Corrigida a renderização de candidatos anonimizados no perfil da Empresa:
  CPF ausente não interrompe mais a criação das linhas nem do gráfico de
  competências; ações que exigem identificação permanecem desabilitadas.
- Corrigido o acesso à empresa anonimizada nas vagas do candidato e reforçada
  a validação da sessão e do identificador da vaga antes das ações de curtida.
- Corrigida a resposta de `CandidatoServlet` para preservar o envelope
  `{status, dados}` e evitar a iteração indevida sobre `Map.Entry`, restaurando
  a listagem de candidatos, o gráfico de competências e os status das curtidas.
- Normalizada a lista de competências recebida pelo perfil da Empresa para
  manter a renderização e as ações de candidatos estáveis.
- Testes legados de `GerenciadorDePerfisService` ajustados para refletir as
  validações de competências e isolar a validação de nomes.

### Removido
