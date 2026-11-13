# Feature com Spec

## Objetivo

Implementar uma feature seguindo um fluxo controlado de **spec → plano → implementação**. A skill deve transformar uma solicitação de mudança em uma entrega rastreável, incremental, validada e limitada ao escopo aprovado.

## Entradas

- **Descrição da feature:** comportamento desejado, regras de negócio, critérios de aceite e restrições conhecidas.
- **Projeto onde será implementada:** repositório, estrutura, tecnologias, convenções e arquivos relevantes.

## Passo a passo

### Etapa 2: Contexto para o agente (`AGENTS.md`)

**O que revisar:**

- Leia `AGENTS.md` na raiz e os arquivos `AGENTS.md` aplicáveis aos diretórios envolvidos.
- Identifique arquitetura, tecnologias, comandos de validação, convenções de nomenclatura, idioma, organização de testes e restrições de segurança.
- Confirme o estado atual do repositório e preserve alterações não relacionadas.

**O que gerar:**

- Normalmente, nenhum arquivo novo.
- Registre mentalmente ou no contexto de trabalho os contratos, restrições e comandos que orientarão a spec e o plano.

**Antes de avançar:**

- Confirme que as regras do agente foram compreendidas.
- Identifique os arquivos de produção, testes e documentação que podem ser afetados.
- Não escreva código de produção nem testes ainda.

### Etapa 3: Especificação (`docs/specs/<nome-da-feature>/`)

**O que gerar:**

- Crie ou atualize a especificação estritamente em `docs/specs/<nome-da-feature>/`.
- Não armazene specs em `docs/spec.md` ou em qualquer outro diretório.
- Documente:
  - contexto e objetivo;
  - escopo e fora do escopo;
  - regras de negócio;
  - entradas, saídas e contratos relevantes;
  - critérios de aceite verificáveis;
  - impacto no que já existe;
  - riscos, limitações e dependências.

**O que revisar:**

- Compare a descrição da feature com o código e a arquitetura reais.
- Não invente APIs, classes, endpoints, persistência ou comportamentos.
- Diferencie claramente comportamento existente de comportamento novo.
- Verifique se os critérios de aceite são testáveis e se não há requisitos contraditórios.

**Antes de avançar:**

- A spec deve estar aprovada pelo responsável pelo projeto.
- Nenhum código deve ser escrito antes dessa aprovação.

### Etapa 4: Plano de implementação (`docs/plano.md`)

**O que gerar:**

- Crie ou atualize `docs/specs/<nome-da-feature>/plano.md`.
- Divida a execução em tarefas pequenas, ordenadas por dependência e com uma única responsabilidade.
- Para cada tarefa, informe:
  - objetivo;
  - arquivos previstos;
  - comportamento a implementar;
  - validação específica;
  - dependências;
  - critério de conclusão.

**O que revisar:**

- Garanta que cada tarefa esteja coberta pela spec e que o plano não introduza escopo novo.
- Confirme contratos reais, pontos de integração, riscos e estratégia de testes.
- Defina explicitamente o que não será alterado.
- Separe tarefas bloqueadas ou canceladas quando a implementação real não suportar o requisito.

**Antes de avançar:**

- O plano deve estar aprovado.
- Escolha somente a próxima tarefa pronta; não implemente tarefas futuras antecipadamente.

### Etapa 5: Implementação incremental

**O que gerar:**

- Implemente apenas uma tarefa do plano por vez.
- Crie ou altere os arquivos previstos, incluindo código de produção, testes e documentação diretamente relacionados quando aprovados.
- Adicione testes automatizados para o comportamento novo ou alterado, seguindo as convenções do projeto.
- Faça um commit separado para cada tarefa concluída, com mensagem clara e específica.

**O que revisar:**

- Leia os contratos existentes antes de editar e reutilize padrões, helpers e abstrações já presentes.
- Verifique tipos, tratamento explícito de erros, compatibilidade e impactos nas integrações.
- Confirme que nenhum arquivo fora do escopo foi alterado.
- Se descobrir um bug ou precisar mudar a abordagem, pare a tarefa, atualize primeiro a spec correspondente em
  `docs/specs/<nome-da-feature>/` e `docs/specs/<nome-da-feature>/plano.md`, aguarde aprovação e só então prossiga.

### Etapa 6: Validação

**O que gerar:**

- Execute os menores comandos que validem a tarefa e, ao final, a suíte completa definida pelo projeto.
- Gere apenas artefatos temporários de build/teste esperados pela ferramenta; não crie documentação de planejamento adicional.
- Registre no relatório final os comandos executados, resultados e eventuais limitações.

**O que revisar:**

- Confirme testes unitários, integração, lint, build ou type-check conforme aplicável.
- Diferencie falhas novas de falhas preexistentes e não mascare nenhuma delas.
- Verifique diff, status do Git e se os commits contêm somente os arquivos esperados.
- Confirme que os critérios de aceite da spec foram atendidos.
- Faça obrigatoriamente a verificação do **Princípio do Anonimato**: confirme que nenhum dado sensível,
  identificável ou de contato de candidato, ou empresa é exposto antes do match, por endpoint, payload,
  consulta, log ou interface. Dados de identificação e contato só podem ser exibidos depois do match.

## Regras

1. Não escrever código antes da spec estar aprovada.
2. Executar uma tarefa do plano por vez.
3. Criar um commit por tarefa concluída.
4. Não alterar arquivos ou comportamentos fora do escopo aprovado.
5. Se houver mudança de rota, atualizar primeiro a spec e depois o plano; somente após aprovação retomar a implementação.
6. Não inventar contratos: confirme classes, métodos, endpoints, formatos e exceções no código existente.
7. Preservar alterações não relacionadas no diretório de trabalho.
8. Não mascarar falhas de testes, build, lint ou validação.
9. Atualizar documentação diretamente relacionada quando o comportamento aprovado mudar.
10. Atualizar `CHANGELOG.md` a cada tarefa concluída, registrando objetivamente a mudança e a validação realizada.
11. Tratar o **Princípio do Anonimato** como requisito obrigatório de toda a tarefa: nenhum dado sensível,
    identificável ou de contato pode ser exposto antes do match.

## Modelos

### Estrutura esperada de uma spec em `docs/specs/<nome-da-feature>/`

```markdown
# Especificação: [nome da feature]

## Contexto e Objetivo
[Problema, motivação e resultado esperado.]

## Escopo
- [Comportamentos incluídos.]

## Fora do Escopo
- [Comportamentos explicitamente excluídos.]

## Regras de Negócio
1. [Regra verificável.]

## Contratos e Integrações
- [Entradas, saídas, APIs, eventos, persistência e dependências.]

## Critérios de Aceite
- [ ] Dado [contexto], Quando [ação], Então [resultado observável].

## Impacto no que já existe
[Compatibilidade, migrações, riscos e arquivos afetados.]
```

### Estrutura esperada de `docs/specs/<nome-da-feature>/plano.md`

```markdown
# Plano de Implementação: [nome da feature]

## Premissas
- [Restrições e decisões aprovadas.]

## Tarefa 1: [ação]
- **O que será feito:** [mudança objetiva.]
- **Arquivos previstos:** [arquivos.]
- **Dependências:** [tarefas ou contratos.]
- **Como validar:** [comando e resultado esperado.]
- **Critério de conclusão:** [condição verificável.]
- [ ] Pendente

## Validação final
- **Comando:** [suíte completa, build, lint ou type-check.]
- **Resultado esperado:** [condição de sucesso.]
```
