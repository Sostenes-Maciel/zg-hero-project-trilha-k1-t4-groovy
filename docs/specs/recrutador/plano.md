# Plano de Implementação: Recrutador

## Premissas

- A implementação só começa após a aprovação da especificação e deste plano.
- A ordem é estrita: banco de dados, back-end e, depois, front-end.
- O Princípio do Anonimato é obrigatório em todas as camadas; filtros no
  front-end não substituem autorização no servidor.
- O match permanece único por candidato–Empresa–vaga, mesmo com curtidas de
  vários Recrutadores.
- Cada tarefa concluída deve atualizar `CHANGELOG.md` e ser validada antes da
  próxima.
- Toda alteração de banco será uma migração incremental aplicada
  explicitamente no PostgreSQL.

## Tarefa 1: Estruturar o modelo de dados e a migração do Recrutador

- **Objetivo:** Criar o modelo persistente do Recrutador, o vínculo
  `1 Empresa -> N Recrutadores`, áreas, permissões, inativação e autoria de
  curtidas, preservando matches e dados legados.
- **Camada afetada:** Banco de dados.
- **Arquivos previstos:**
  - `BancoDeDados/Migracao_Recrutador.sql`
  - `BancoDeDados/LinkeTinder_BD.sql`, somente se for necessário atualizar o
    schema inicial
  - `CHANGELOG.md`
- **Implementação prevista:**
  - Criar `recrutador` com ID próprio, `empresa_id`, `nome`,
    `area_atuacao`, `nivel_permissao`, `descricao`, `email`, `cpf`,
    `data_nascimento`, `senha`/`senha_hash` e `ativo`.
  - Criar FK obrigatória para `empresa` e unicidade global de e-mail e CPF.
  - Restringir `nivel_permissao` a `Total` e `Limitado` e centralizar os
    valores de `area_atuacao`.
  - Adicionar `recrutador_id` a `curtida`, preservando registros legados.
  - Permitir múltiplas autorias de curtida sem duplicar `match_vaga`.
  - Preservar a unicidade de `match_vaga` por
    `(cpf_candidato, cnpj_empresa, id_vaga)`.
  - Adicionar à `vaga` a área necessária ao controle de escopo.
  - Preservar histórico ao inativar Recrutador.
- **Como validar:**
  - Aplicar schema e migração em PostgreSQL de teste.
  - Consultar FKs, constraints, índices, enums/domínios e unicidades.
  - Inserir vários Recrutadores para uma Empresa e tentar associação inválida.
  - Inserir duas curtidas para a mesma tupla e confirmar autorias distintas e
    um único match.
  - Confirmar que a inativação preserva curtidas e matches.
  - Executar `git diff --check`.
- **Critério de conclusão:** A migração suporta o relacionamento, múltiplas
  autorias, escopo por área, inativação e match único sem senha em texto puro.
- **Status:** [x] Concluída

## Tarefa 2: Implementar modelos e DAOs do Recrutador

- **Objetivo:** Criar os modelos e a persistência JDBC para Recrutador,
  incluindo vínculo único com Empresa, inativação e armazenamento seguro dos
  campos.
- **Camada afetada:** Back-end.
- **Arquivos previstos:**
  - `Backend/src/main/groovy/br/com/zg/acelera/model/Recrutador.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/interfaces/IRecrutadorDAO.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/dao/RecrutadorDAO.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/dao/CurtidaDAO.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/dao/MatchDAO.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/model/RecrutadorSpec.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/dao/RecrutadorDAOSpec.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/dao/CurtidaDAOSpec.groovy`
  - `CHANGELOG.md`
- **Implementação prevista:**
  - Mapear campos, enums, Empresa associada e estado `ativo`.
  - Implementar cadastro, busca, atualização, inativação e autenticação por
    e-mail.
  - Usar statements parametrizados e nunca retornar senha ou hash.
  - Adaptar curtidas para persistir `recrutador_id` e permitir mais de uma
    curtida da Empresa na mesma vaga.
  - Manter consultas de match agrupadas pela tupla da Empresa.
- **Como validar:**
  - Executar `cd Backend && ./gradlew test --tests '*Recrutador*'`.
  - Executar os testes direcionados de `CurtidaDAO`.
  - Testar CRUD, Empresa inexistente, unicidade, inativação e autoria.
  - Confirmar que mapeamentos e consultas não retornam senha/hash.
- **Critério de conclusão:** Modelos e DAOs persistem e consultam Recrutadores
  e autorias corretamente, com testes passando e sem regressão do match.
- **Status:** [x] Concluída

## Tarefa 3: Implementar serviços e regras de negócio

- **Objetivo:** Implementar validações, autenticação, autorização, escopo por
  área, permissões e regras de curtida/match no domínio.
- **Camada afetada:** Back-end.
- **Arquivos previstos:**
  - `Backend/src/main/groovy/br/com/zg/acelera/service/ValidarRecrutadorService.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/service/GerenciadorDeRecrutadoresService.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/service/GerenciadorDePerfisService.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/service/GerenciadorDeVagasService.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/service/ValidarRecrutadorSpec.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/service/GerenciadorDeRecrutadoresServiceSpec.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/service/GerenciadorDePerfisServiceSpec.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/service/GerenciadorDeVagasServiceSpec.groovy`
  - `CHANGELOG.md`
- **Implementação prevista:**
  - Validar e-mail contendo `@`, CPF básico normalizado, data ISO com idade
    mínima de 18 anos e senha de 6 a 8 caracteres.
  - Hashar e comparar senhas com BCrypt ou equivalente seguro.
  - Permitir cadastro somente ao perfil principal de Empresa existente.
  - Aplicar `Total` e `Limitado` no servidor.
  - Restringir vagas e candidatos à área e Empresa autorizadas.
  - Permitir múltiplas curtidas, preservar autoria e impedir remoção por outro
    Recrutador.
  - Criar um único match por candidato–Empresa–vaga.
  - Produzir dados anonimizados antes do match e liberados somente na tupla
    autorizada depois do match.
- **Como validar:**
  - Executar `cd Backend && ./gradlew test --tests '*Service*'`.
  - Testar cada validação, hash, login, inativação e autorização.
  - Testar operações de vaga para ambos os níveis e áreas incompatíveis.
  - Testar duas curtidas, match único e bloqueio de remoção.
  - Verificar que nenhum DTO de serviço contém dados proibidos antes do match.
- **Critério de conclusão:** Todas as regras de domínio estão centralizadas
  nos serviços, são rejeitadas no servidor quando inválidas e têm cobertura
  automatizada para permissões, área, match e anonimato.
- **Status:** [x] Concluída

## Tarefa 4: Implementar controllers, servlets e rotas

- **Objetivo:** Expor os fluxos de Recrutador e integrar as rotas existentes
  sem permitir bypass de autenticação, permissão, área ou anonimato.
- **Camada afetada:** Back-end.
- **Arquivos previstos:**
  - `Backend/src/main/groovy/br/com/zg/acelera/controller/RecrutadorController.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/servlet/RecrutadorServlet.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/servlet/CandidatoServlet.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/servlet/EmpresaServlet.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/servlet/VagaServlet.groovy`
  - `Backend/src/main/groovy/br/com/zg/acelera/servlet/MatchServlet.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/servlet/RecrutadorServletSpec.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/servlet/MatchServletSpec.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/servlet/CandidatoServletSpec.groovy`
  - `Backend/src/test/groovy/br/com/zg/aceleraspec/servlet/VagaServletSpec.groovy`
  - `CHANGELOG.md`
- **Implementação prevista:**
  - Criar rotas próprias para cadastro, edição, inativação, login e consultas.
  - Derivar Empresa e permissões da identidade autenticada, nunca de valores
    arbitrários do payload.
  - Serializar respostas sem senha/hash e com anonimato por estado do match.
  - Adaptar rotas de curtida/match para receber autoria sem duplicar match.
  - Corrigir endpoints antigos que expõem identificadores antes da autorização.
- **Como validar:**
  - Executar `cd Backend && ./gradlew test --tests '*Servlet*'`.
  - Testar respostas HTTP, códigos de erro, payloads, autorização e rotas
    inválidas.
  - Inspecionar respostas antes/depois do match e logs de erro.
  - Confirmar que chamadas diretas não bypassam área ou permissão.
- **Critério de conclusão:** Rotas próprias e legadas aplicam as regras
  uniformemente, retornam contratos seguros e têm testes de integração ou
  unidade passando.
- **Status:** [ ] Pendente

## Tarefa 5: Implementar modelos e serviços TypeScript

- **Objetivo:** Criar os contratos TypeScript e a camada de integração com a
  API do Recrutador, sem armazenar ou propagar dados sensíveis.
- **Camada afetada:** Front-end.
- **Arquivos previstos:**
  - `Frontend/src/model/Recrutador.ts`
  - `Frontend/src/model/Curtida.ts`
  - `Frontend/src/model/Match.ts`
  - `Frontend/src/service/ValidarRecrutador.ts`
  - `Frontend/src/service/AutenticacaoRecrutador.ts`
  - `Frontend/src/service/CadastroRecrutador.ts`
  - `Frontend/src/service/CalcularAfinidade.ts`, se necessário
  - `Frontend/src/dao/BancodeDados.ts`, somente para integração aprovada
  - `CHANGELOG.md`
- **Implementação prevista:**
  - Modelar área, nível, estado de sessão e respostas anonimizadas.
  - Implementar chamadas de cadastro, login, edição, inativação, vagas,
    candidatos, curtidas e matches.
  - Validar campos no cliente sem substituir validação do back-end.
  - Não persistir senha, hash, CPF ou data de nascimento em localStorage.
  - Tratar respostas pré e pós-match com tipos distintos e seguros.
- **Como validar:**
  - Executar `cd Frontend && npm run build`.
  - Inspecionar payloads de rede e localStorage.
  - Testar erros de API, sessão inativa, área incompatível e respostas
    anonimizadas.
- **Critério de conclusão:** Modelos e serviços compilam, integram a API e não
  expõem ou armazenam dados proibidos.
- **Status:** [ ] Pendente

## Tarefa 6: Implementar telas de autenticação e cadastro

- **Objetivo:** Criar os fluxos visuais de login, cadastro, edição e
  inativação de Recrutadores para o perfil principal da Empresa.
- **Camada afetada:** Front-end.
- **Arquivos previstos:**
  - `Frontend/src/view/LoginRecrutador.ts`
  - `Frontend/src/view/CadastroRecrutador.ts`
  - `Frontend/src/view/PerfilRecrutador.ts`
  - `Frontend/src/components/Menu.ts`
  - `Frontend/src/main.ts`
  - `Frontend/src/style.css`
  - `CHANGELOG.md`
- **Implementação prevista:**
  - Criar formulários com área predefinida, permissão e data ISO.
  - Exibir validações e erros sem revelar dados sensíveis.
  - Exibir ações administrativas apenas ao perfil principal da Empresa.
  - Não preencher, mostrar ou enviar senha salvo no formulário de autenticação.
  - Esconder CPF, data de nascimento e hash em todas as telas.
- **Como validar:**
  - Executar `cd Frontend && npm run build`.
  - Validar visualmente login, cadastro, edição, inativação e mensagens de
    erro.
  - Confirmar que Recrutador não acessa ações do perfil principal e que
    sessão inativa é encerrada.
  - Inspecionar DOM, rede e localStorage.
- **Critério de conclusão:** Os fluxos de autenticação e gestão funcionam,
  respeitam autorização e não exibem dados permanentemente privados.
- **Status:** [ ] Pendente

## Tarefa 7: Front-end - Telas próprias do Recrutador

- **Objetivo:** Implementar as telas próprias para operação diária do
  Recrutador, com listagem de vagas, análise de candidatos, curtidas e
  matches dentro da área e da Empresa autorizadas.
- **Camada afetada:** Front-end.
- **Arquivos previstos:**
  - `Frontend/src/view/ListaVagasRecrutador.ts`
  - `Frontend/src/view/ListaCandidatosRecrutador.ts`
  - `Frontend/src/view/ListaMatchesRecrutador.ts`
  - `Frontend/src/style.css`
  - `CHANGELOG.md`
- **Implementação prevista:**
  - Mostrar somente vagas e candidatos da área/Empresa autorizadas.
  - Exibir antes do match apenas competências, descrição profissional,
    Estado/Cidade e afinidade.
  - Permitir curtidas múltiplas sem criar matches duplicados na interface.
  - Exibir controles de vaga conforme `Total` ou `Limitado`.
- **Como validar:**
  - Executar `cd Frontend && npm run build`.
  - Validar visualmente listas por área e Empresa para os níveis `Total` e
    `Limitado`.
  - Testar curtidas de dois Recrutadores para a mesma vaga e confirmar que a
    interface não duplica o Match.
  - Inspecionar payloads, DOM, console e localStorage para confirmar que
    nenhum dado proibido é recebido ou renderizado antes do match.
  - Executar `git diff --check`.
- **Critério de conclusão:** As três telas próprias funcionam com o escopo
  correto, exibem controles conforme a permissão, integram curtidas e matches
  sem duplicação e preservam o anonimato pré-match.
- **Status:** [ ] Pendente

## Tarefa 8: Front-end - Telas de Match e Perfil

- **Objetivo:** Adaptar as telas de perfil e match para revelar somente os
  dados autorizados depois do match e manter o anonimato quando ele ainda não
  existir.
- **Camada afetada:** Front-end.
- **Arquivos previstos:**
  - `Frontend/src/view/PerfilCandidato.ts`
  - `Frontend/src/view/PerfilEmpresa.ts`
  - `Frontend/src/view/PerfilVaga.ts`
  - `Frontend/src/view/ListaMatchesCandidato.ts`
  - `Frontend/src/view/ListaMatchesEmpresa.ts`
  - `Frontend/src/style.css`
  - `CHANGELOG.md`
- **Implementação prevista:**
  - Usar o estado de Match autorizado para decidir os campos renderizados.
  - Manter antes do match somente competências, descrição profissional,
    Estado/Cidade e afinidade.
  - Após o match, mostrar ao Recrutador apenas os dados liberados do candidato
    para a tupla candidato–Empresa–vaga.
  - Após o match, mostrar ao candidato nome, área de atuação e e-mail do
    Recrutador, nunca CPF, data de nascimento, senha ou hash.
  - Manter o nome da Empresa oculto na vaga antes do match e revelá-lo somente
    após autorização da mesma tupla.
  - Escapar valores interpolados e tratar respostas incompletas sem fallback
    identificável.
- **Como validar:**
  - Executar `cd Frontend && npm run build`.
  - Validar visualmente perfis e cards antes e depois de Match válido.
  - Inspecionar respostas de rede, DOM, atributos HTML, console e localStorage.
  - Confirmar que acesso a outra vaga, Empresa ou candidato não libera dados.
  - Confirmar que CPF, data de nascimento, senha e hash nunca aparecem.
  - Executar `git diff --check`.
- **Critério de conclusão:** As telas de perfil e Match exibem exatamente os
  dados permitidos em cada estado, liberam contato somente após Match
  autorizado e escapam conteúdo sem expor dados sensíveis.
- **Status:** [ ] Pendente

## Tarefa 9: Front-end - Correção de anonimato nas listagens legadas

- **Objetivo:** Corrigir as telas legadas para garantir anonimato antes do
  Match e escaping seguro de todo conteúdo renderizado.
- **Camada afetada:** Front-end.
- **Arquivos previstos:**
  - `Frontend/src/view/ListaCandidatos.ts`
  - `Frontend/src/view/ListaEmpresas.ts`
  - `Frontend/src/view/ListaVagas.ts`
  - `Frontend/src/components/Menu.ts`
  - `Frontend/src/style.css`
  - `CHANGELOG.md`
- **Implementação prevista:**
  - Remover das listagens pré-match nome, CPF, CNPJ, e-mail, CEP/endereço e
    outros identificadores da parte sem Match autorizado.
  - Aplicar anonimato também aos perfis abertos a partir das listagens.
  - Manter em vagas pré-match somente título, descrição, localidade permitida,
    competências e afinidade; ocultar a identidade da Empresa.
  - Impedir que Menu, atributos `data-*`, filtros, URLs ou estado local revelem
    identificadores protegidos.
  - Escapar nomes, descrições, competências, localidades e mensagens antes de
    inserir conteúdo no DOM.
  - Impedir fallbacks de API que usem dados legados para contornar anonimato.
- **Como validar:**
  - Executar `cd Frontend && npm run build`.
  - Validar visualmente cada listagem e abertura de perfil antes e depois do
    Match.
  - Inspecionar DOM, atributos, URLs, payloads, console e localStorage em
    busca por CPF, CNPJ, e-mail, nome e endereço indevidos.
  - Testar valores com caracteres HTML para confirmar escaping e ausência de
    injeção no DOM.
  - Confirmar que dados só aparecem após Match da tupla exata.
  - Executar `git diff --check`.
- **Critério de conclusão:** As listagens legadas não expõem dados
  identificáveis antes do Match, todas as entradas são escapadas e nenhuma
  rota visual ou estado local contorna o anonimato.
- **Status:** [ ] Pendente

## Validação final

- **Comandos:**
  - `cd Backend && ./gradlew test`
  - `cd CadastroMicroService && ./gradlew test`, se alterado
  - `cd Frontend && npm run build`
  - `git diff --check`
- **Resultado esperado:** Banco, back-end e front-end integrados, com
  `1 Empresa -> N Recrutadores`, níveis de permissão, escopo por área,
  curtidas múltiplas, match único e Princípio do Anonimato validados sem
  falhas novas; as telas próprias do Recrutador, telas de perfil e Match e
  listagens legadas exibem somente os dados autorizados em cada estado.
