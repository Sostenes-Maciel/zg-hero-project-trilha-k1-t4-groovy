# Contexto do Linketinder

Este arquivo é a referência de contexto para alterações feitas por agentes neste
repositório. As descrições abaixo refletem a implementação encontrada no código,
nos scripts SQL e na documentação existente.

## 1. Visão geral, stack e estrutura

O Linketinder é uma plataforma de conexão entre candidatos e empresas. Há três
partes independentes:

- **Back-end principal (`Backend/`)**: Groovy 5.1.1, Gradle 9.6.0, JDBC com
  PostgreSQL, Gson e Spock/JUnit. É um WAR para Tomcat 10, com servlets REST em
  `/api/*`, e também contém a aplicação CLI inicializada por `Main.groovy`.
- **Front-end (`Frontend/`)**: TypeScript, Vite, HTML e CSS. Consome os
  endpoints HTTP do back-end em `http://localhost:8080`, mantém dados de apoio
  no `localStorage`, calcula afinidade por competências e renderiza a interface.
  Usa Chart.js e `@countrystatecity/countries-browser`.
- **Microsserviço de cadastros (`CadastroMicroService/`)**: aplicação Groovy
  6.0.0/Gradle 9.8.2 baseada em `com.sun.net.httpserver.HttpServer`. Expõe o
  fluxo de cadastro separado do WAR, valida os dados em `ServicoCadastro` e
  persiste com `RepositorioPostgresCadastro`. A porta padrão é `8081`.

Não há build multi-projeto na raiz: cada módulo possui seu próprio
`settings.gradle` e wrapper Gradle.

### Responsabilidade das pastas

No back-end, o código fica em
`Backend/src/main/groovy/br/com/zg/acelera/`:

- `model/`: entidades Groovy (`Pessoa`, `Candidato`, `Empresa`, `Vaga`,
  `Curtidas` e `Match`).
- `interfaces/`: contratos de DAO, conexão e comandos.
- `dao/`: JDBC, consultas PostgreSQL, mapeamento de registros e fábrica de
  conexão.
- `service/`: validação e regras de perfis, vagas, competências e consultas de
  matches.
- `controller/`: converte operações de serviço em respostas com `status`,
  `mensagem` e dados.
- `servlet/`: endpoints HTTP (`CandidatoServlet`, `EmpresaServlet`,
  `VagaServlet` e `MatchServlet`).
- `command/` e `view/`: fluxo de comandos e menus da aplicação CLI.
- `filter/` e `utils/`: CORS e utilitários de validação/formatação.
- `Backend/src/test/groovy/`: testes Spock/JUnit organizados por camada.

No front-end, `Frontend/src/` separa `model/` (interfaces), `dao/`
(`BancodeDados` e `localStorage`), `service/` (cadastros, validações, CEP e
afinidade), `view/` (telas e renderização) e `components/` (menu, notificações,
confirmações e animações). `main.ts` inicializa a aplicação.

No microsserviço, `controller/` encaminha o cadastro, `model/` contém serviço,
contratos, exceção e repositório, e `view/` contém o manipulador HTTP.
`AplicacaoCadastro.groovy` monta e inicia o servidor.

### Executar e testar

Pré-requisitos: JDK compatível com as versões de Groovy, PostgreSQL com o banco
`LinkeTinder_BD`, e Node.js/npm para o front-end.

1. **Banco**: aplique `BancoDeDados/LinkeTinder_BD.sql` em PostgreSQL antes de
   usar o back-end. A conexão JDBC principal está fixa em
   `localhost:5432/LinkeTinder_BD`, usuário `postgres` e senha `postgres` em
   `Backend/.../dao/ConexaoPostgres.groovy`.
2. **Back-end**:
   ```bash
   cd Backend
   ./gradlew test
   ./gradlew appRun
   ```
   `appRun` é a tarefa do plugin Gretty e publica o WAR no Tomcat 10. A CLI
   pode ser executada pelo método `main` de
   `src/main/groovy/br/com/zg/acelera/Main.groovy` na IDE. O README menciona
   `./gradlew run`, mas o `build.gradle` atual não aplica o plugin
   `application` ao back-end e, portanto, não declara essa tarefa.
3. **Front-end**:
   ```bash
   cd Frontend
   npm install
   npm run dev
   ```
   Para verificar o build de produção, use `npm run build`; para servir o
   artefato, use `npm run preview`. Não há script de testes npm configurado.
4. **Microsserviço de cadastros**:
   ```bash
   cd CadastroMicroService
   DB_USER=postgres DB_PASSWORD=postgres ./gradlew run
   ```
   `DB_URL` (padrão `jdbc:postgresql://localhost:5432/LinkeTinder_BD`),
   `PORT` (padrão `8081`) e `FRONTEND_ORIGIN` (padrão
   `http://localhost:5173`) são variáveis opcionais. Os testes são executados
   com `./gradlew test`; os testes de repositório dependem de PostgreSQL.

### Convenções e restrições observadas

- Pacotes Groovy usam minúsculas (`br.com.zg.acelera`); classes, interfaces e
  componentes principais usam `PascalCase`; métodos, propriedades e variáveis
  usam `camelCase`.
- Os arquivos de teste terminam em `Spec.groovy` e acompanham a camada testada.
  O front-end usa módulos TypeScript com interfaces em `model/` e imports
  relativos com extensão `.ts` em boa parte dos arquivos.
- O banco usa tabelas, colunas e constraints em `snake_case`; IDs de vaga,
  competência, curtida e match são inteiros `SERIAL`, enquanto CPF e CNPJ são
  chaves textuais.
- A persistência do back-end usa DAOs JDBC e statements parametrizados; regras
  de negócio ficam nos serviços, não nos servlets ou views.
- Competências são listas de `String` no modelo Groovy/TypeScript e são
  normalizadas/relacionadas por tabelas de associação no PostgreSQL.
- Validações existentes devem ser preservadas: campos obrigatórios, e-mail,
  CPF/CNPJ, CEP, estado, descrição, competências e idade de candidato (18 a
  120 no microsserviço).

## 2. Domínio do Linketinder

### Entidades de aplicação

- **Candidato**: estende `Pessoa` e acrescenta `idade` e `cpf`. `Pessoa`
  fornece nome, e-mail, país, estado, CEP, descrição e lista de competências.
- **Empresa**: estende `Pessoa` e acrescenta `cnpj`; é dona de uma ou mais
  vagas.
- **Vaga**: possui `id`, título, país, estado, descrição, competências exigidas
  e uma `Empresa`.
- **Competência**: não possui uma classe de domínio própria no back-end; é uma
  `String` na aplicação e uma linha em `competencia` no banco. As relações com
  candidato, empresa e vaga usam `candidato_competencia`,
  `empresa_competencia` e `vaga_competencia`.
- **Curtida**: `Curtidas` agrupa um `Candidato`, uma `Vaga` e opcionalmente uma
  `Empresa`. No banco, `curtida` usa `cpf_candidato`, `id_vaga` e
  `cnpj_empresa` para representar a curtida do candidato na vaga, a curtida da
  empresa no candidato para uma vaga, ou registros legados de empresa sem vaga.
- **Match**: `Match` agrupa candidato, empresa e vaga. No banco, `match_vaga`
  registra a reciprocidade e possui unicidade em
  `(cpf_candidato, cnpj_empresa, id_vaga)`.

### Relacionamentos no banco

`candidato.cpf` e `empresa.cnpj` são chaves primárias. `vaga.cnpj_empresa`
referencia a empresa e a constraint composta `(id_vaga, cnpj_empresa)` garante
que uma curtida/match use a empresa proprietária da vaga. As tabelas de
competências são associações muitos-para-muitos com chaves primárias compostas.
Curtidas e matches têm foreign keys para candidato, empresa e vaga com
`ON DELETE CASCADE`.

## 3. Regras de negócio centrais

Uma curtida do candidato é registrada por `curtirVaga(cpf, idVaga)` e contém o
par candidato-vaga, sem `cnpj_empresa`. Uma curtida da empresa é registrada por
`curtirCandidato(cnpj, cpf, idVaga)` e exige que a vaga informada pertença à
empresa que está curtindo. Registros duplicados são evitados pelo DAO e pelo
índice único parcial do banco. Curtidas podem ser removidas enquanto não houver
match; a remoção de curtida da empresa ou do candidato é bloqueada quando já
existe match.

O match é criado por `GerenciadorDePerfisService.processarPossivelMatch` quando
`CurtidaDAO.verificarMatch` encontra **as duas curtidas para a mesma tupla**:

1. o candidato curtiu a vaga (`cpf_candidato` + `id_vaga` e
   `cnpj_empresa IS NULL`); e
2. a empresa proprietária curtiu o mesmo candidato **para o mesmo
   `id_vaga`** (`cpf_candidato` + `cnpj_empresa` + `id_vaga`).

Se a tupla ainda não existir em `match_vaga`, ela é inserida. Portanto, uma
curtida da empresa em outra vaga não cria match para esta vaga.

## 4. Princípio do anonimato — restrição explícita

> **REGRA INQUEBRÁVEL PARA TODO AGENTE:** antes de existir um registro em
> `match_vaga` para o candidato, a empresa e a vaga envolvida, nenhuma
> alteração pode revelar a identidade ou o contato da outra parte. O mesmo
> vale no sentido inverso. Não contorne esta regra por endpoint, DTO, consulta,
> log, tela, payload ou dado derivado.

Antes do match, a visualização de uma **vaga para o candidato** pode conter
somente dados profissionais da oportunidade: título, descrição, país/estado,
competências exigidas e afinidade. O nome, CNPJ, e-mail, CEP/endereço e outros
dados identificadores da empresa devem aparecer apenas depois do match.

Antes do match, a visualização de um **candidato para a empresa** pode conter
somente dados profissionais anonimizados: competências e afinidade (e,
quando necessário à decisão, informações profissionais não identificadoras).
Nome, CPF, e-mail, CEP/endereço e demais dados de identificação/contato só
podem aparecer depois do match. O candidato também não deve ser identificado
por atributos equivalentes ou por respostas de API.

Depois do match, a identidade e os dados de contato previstos pelo produto
podem ser exibidos às duas partes; a autorização continua limitada à tupla
exata candidato-empresa-vaga que gerou o match.

O código já mascara o nome da empresa em `PerfilVaga` e o nome do candidato em
linhas sem match, e os cartões de curtidas pendentes do candidato ocultam a
empresa. Porém, a implementação atual ainda possui superfícies que violam a
regra e não devem ser tomadas como comportamento desejado: listagens
administrativas (`ListaCandidatos`, `ListaEmpresas`), perfis completos e alguns
dados retornados por endpoints expõem e-mail/CPF/CNPJ; além disso,
`GerenciadorDePerfisService.listarMatchesCompletosEmpresa` atualmente inclui o
nome do candidato em curtida pendente. Qualquer manutenção que tocar essas
superfícies deve corrigir a exposição, não ampliá-la.

## 5. Banco de dados

Os scripts estão exatamente em **`BancoDeDados/`**:

- `LinkeTinder_BD.sql`: criação das tabelas, constraints, índices e dados
  iniciais;
- `Migracao_CurtidaEmpresaPorVaga.sql`: alteração da tabela `curtida` para
  associar a curtida da empresa à vaga, adicionar a foreign key composta e
  criar o índice de unicidade;
- `Queries_LinkeTinder.sql`: consultas manuais de leitura/exemplo.

O projeto não usa Flyway, Liquibase, migrations Gradle ou outro executor de
migrações. O versionamento é feito pelo Git, mantendo o schema inicial e
scripts SQL incrementais nomeados manualmente; cada alteração deve ser
aplicada explicitamente no PostgreSQL (por exemplo, com `psql` ou uma IDE).
A migração de curtida é parcialmente idempotente (`IF EXISTS`,
`IF NOT EXISTS` e teste de constraint), mas não há tabela de controle de versão
de migrações.
