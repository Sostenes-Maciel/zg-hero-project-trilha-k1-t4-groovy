# Linketinder MVP — Acelera ZG

Projeto desenvolvido como parte do desafio técnico do **Acelera ZG**. O **Linketinder** é uma plataforma de conexões profissionais executada via terminal (CLI), voltada para o cadastro, visualização e match entre **candidatos e empresas**.

O projeto conta com um backend em **Groovy** e um frontend web em **TypeScript**, estruturado com foco em Programação Orientada a Objetos (POO), separação de responsabilidades, Clean Code e versionamento semântico.

---

##  Tecnologias Utilizadas

### Backend
*   **Groovy:** Linguagem principal do backend.
*   **Gradle:** Gerenciamento do projeto, build e execução.
*   **PostgreSQL & JDBC:** Banco de dados relacional e persistência.
*   **Spock Framework:** Testes automatizados.

### Frontend
*   **TypeScript, HTML5, CSS3:** Estrutura e estilização.
*   **Vite:** Ferramenta de desenvolvimento e build.
*   **Chart.js:** Gráficos de competências.
*   **Country State City:** API para carregamento de países e estados.
*   **LocalStorage:** Persistência local no navegador.

### Controle de Versão
*   **Git e GitHub**

---

##  Funcionalidades

### Listagem de Perfis
*   **Candidatos:** Apresenta Nome, CPF, Estado e Competências.
*   **Empresas:** Apresenta Nome, CNPJ, País, Estado e Competências desejadas.

### Cadastro Dinâmico
Permite cadastrar novos perfis durante a execução do programa. As competências podem ser informadas separadas por vírgula (ex: `Java, Groovy, Python`), e o sistema as converte automaticamente em uma lista.

---

##  Como Executar

### Via IntelliJ IDEA (Backend)
1. Clone este repositório e abra a pasta no IntelliJ IDEA.
2. Aguarde a sincronização do Gradle.
3. Execute o método `main` em: `src/main/groovy/br/com/zg/acelera/Main.groovy`.
4. Interaja através do console da IDE.

### Via Terminal (Backend)
Na raiz do projeto:
*   **Linux / macOS:** `./gradlew run`
*   **Windows:** `gradlew run`

---

##  Arquitetura e Estrutura do Projeto (Backend)

O projeto utiliza uma organização baseada na separação de responsabilidades:

```text
src/main/groovy/br/com/zg/acelera/
├── Main.groovy       # Ponto de entrada da aplicação
├── model/            # Entidades e contratos (IPessoa, Candidato, Empresa)
├── view/             # Interação com o usuário via terminal (MenuView)
├── service/          # Lógica de negócio e fluxo (GerenciadorDePerfisService)
├── dao/              # Persistência de dados e conexão JDBC
└── repository/       # Armazenamento e gerenciamento de dados em memória
```
## Atualizações e Evolução do Projeto

### 1. Sistema de Curtidas e Painel de Matches

Entidade Curtida: Elo central para registrar interesse do candidato na vaga e o aceite da empresa.

Match Automatizado: O Match exige reciprocidade na mesma instância de relacionamento.

Painel Visual: Relatório em tempo real no terminal, diferenciando os status [ MATCH! ] e [ PENDENTE ].

Isolamento Seguro: Uso de repositório em memória (curtidasSimulacao) para proteger dados oficiais durante testes.

### 2. Testes Automatizados (Spock Framework)

Testes Unitários: Validação rigorosa de regras de negócio (CPF, CNPJ, e-mail, idade, CEP) independentes de infraestrutura.

Testes de Integração: Validação da comunicação direta dos DAOs com o PostgreSQL (CRUD completo e cleanup).

**Testes de Cadastro:** Implementados testes para verificar a inserção de novos candidatos e empresas nas listas do sistema, garantindo que o cadastro adicione corretamente os novos elementos. 

**Testes de Criação de Objetos:** Adicionados testes para validar a criação das entidades `Candidato` e `Empresa`, verificando seus principais atributos. 

**Testes de Validação:** Criados testes unitários para as regras de validação de candidatos e empresas, contemplando entradas válidas e inválidas. 

**Testes de Fronteira:** Adicionados casos para verificar os limites das validações, como idade, CPF, CNPJ e CEP. 

**Organização dos Testes:** Os testes foram separados em classes `Spec`, mantendo cada unidade do sistema testada de forma independente. 

### 3. Implementação FrontEnd (TypeScript)

O sistema possui anonimização bilateral: candidatos são anônimos para a empresa, e a identidade da empresa é omitida na vaga vista pelo candidato.

**Tecnologias utilizadas**

TypeScript — linguagem utilizada para a implementação da lógica da aplicação. 

HTML5 — estrutura das páginas e formulários. 

CSS3 — estilização, responsividade e organização visual da interface. 

Vite — ferramenta utilizada para desenvolvimento e execução do frontend. 

Chart.js — criação do gráfico de competências no perfil da empresa. 

Country State City (@countrystatecity/countries-browser) — carregamento de países e estados no cadastro de empresas.

LocalStorage — persistência dos dados cadastrados no navegador. 


Estrutura do Frontend:
Plaintext

```text
Frontend/
├── index.html
├── package.json
├── src/
│   ├── main.ts
│   ├── style.css
│   ├── components/  # Componentes reutilizáveis (Menu)
│   ├── model/       # Entidades (Candidato, Empresa, Vaga)
│   ├── repository/  # Armazenamento local
│   ├── service/     # Regras de negócio e cadastros
│   └── view/        # Telas e listas
```

Como executar o frontend:

```text
cd Frontend
npm install
npm run dev
```
Para gerar a versão de produção: npm run build

### 4. Índice de Afinidade

Foi implementado um índice de afinidade entre candidatos e empresas com base nas competências em comum:
Afinidade = (competências em comum / competências do candidato) × 100
O sistema normaliza as entradas (ignorando case e espaços) e a regra foi centralizada em src/service/calcularAfinidade.ts.

Também foi adicionada, de forma opcional, a exibição do percentual de afinidade na lista de candidatos do perfil da empresa, mantendo a identidade dos candidatos anônima.

**Como funciona**

O percentual é calculado pela quantidade de competências em comum entre candidato e empresa, dividida pela quantidade total de competências do candidato:


```text

Afinidade = (competências em comum / competências do candidato) × 100

```


Exemplo:


```text

Candidato: Java, Groovy, Python, Git

Empresa: Java, Python, React


Competências em comum: Java e Python

Afinidade: 2 / 4 × 100 = 50%

```
O sistema também normaliza as competências, ignorando diferenças de maiúsculas, minúsculas e espaços.

### 5. Modelagem de Banco de Dados (Parte 1)

Desenvolvimento da modelagem e criação do Banco de Dados Relacional (PostgreSQL).

Estrutura Principal: candidato (PK CPF), empresa (PK CNPJ), competencia, vaga.

Tabelas Auxiliares: candidato_competencia, empresa_competencia, vaga_competencia, curtida e match para lidar com relações N:N.

Lógica de Match no Banco de Dados (Desafio Bônus)


Para suportar o principal caso de uso do LinkeTinder (o "Match"), o banco de dados foi modelado introduzindo duas tabelas estratégicas: `curtida` e `match`.


**Como funciona o fluxo de dados:**

**Registro da Curtida da Empresa:** Quando a empresa, através do recrutador, curte o perfil de um candidato, um novo registro é inserido na tabela `curtida`, desta vez preenchendo as chaves `empresa_id` e `candidato_id`.

**Validação e Integridade:** A tabela `curtida` possui uma `CHECK CONSTRAINT` que garante que uma curtida seja *ou* de um candidato para uma vaga, *ou* de uma empresa para um candidato, nunca ambos na mesma tupla.

**O Evento de Match:** 
A aplicação (backend) é responsável por consultar o banco e verificar a intersecção de interesses (Candidato curtiu a Vaga X `AND` Empresa dona da Vaga X curtiu o Candidato). Ao identificar essa condição verdadeira, um registro final é inserido na tabela `match`, consolidando a conexão e permitindo o contato entre as partes.


As queries de inserção e simulação desta lógica encontram-se no arquivo SQL principal do repositório.
## Modelo DER:

![Diagrama DER](BancoDeDados/DER%20-%20LinkeTinder.png)

### 6. Migração para JDBC e DAO (Parte 2)

Transição do armazenamento em memória para PostgreSQL via JDBC.

Criada a classe ConexaoBD e os DAOs específicos (CandidatoDAO, EmpresaDAO, CompetenciaDAO, VagaDAO).

Fluxo: Menu → Service → DAO → JDBC → PostgreSQL.

 
### Atualização - Boas Práticas

Separação de Responsabilidades (SRP):** Desacoplamento da manipulação do DOM, regras de negócio e eventos em funções especializadas. 

Validações Padronizadas:** Isolamento de expressões regulares em constantes nomeadas e aplicação de métodos defensivos com `.trim()`. 

Integridade de Dados (Cascade Delete):** Remoção automática de registros órfãos (matches e curtidas) na camada de persistência simulada ao excluir entidades. 

Otimização DRY:** Reutilização de rotinas de normalização e cálculo de afinidade por competências. 




### Atualização - Aplicação dos princípios SOLID


Aplicação dos Princípios SOLID


A refatoração da camada de persistência e negócios foi guiada pelos pilares do SOLID:


1. (Princípio da Responsabilidade Única - SRP) 



Onde se aplica: As classes de validação (ValidarCandidato, ValidarEmpresa) e os DAOs possuem propósitos estritos. Os DAOs cuidam exclusivamente da persistência SQL, enquanto as regras de consistência de dados residem isoladamente nos serviços de validação, testadas de forma unitária e pura.

2. (Princípio Aberto/Fechado - OCP)

Onde se aplica: As classes DAO implementam contratos abstratos (ICandidatoDAO, ICurtidaDAO, IMatchDAO, etc.). É possível estender ou trocar a tecnologia de persistência no futuro sem modificar a estrutura de consumo dos dados.

3.  (Princípio da Substituição de Liskov - LSP)

Onde se aplica: Todas as classes concretas de persistência (CandidatoDAO, EmpresaDAO, etc.) podem ser substituídas por suas respectivas interfaces em qualquer ponto da aplicação ou nos testes automatizados, sem que isso afete o comportamento esperado do sistema.

4.  (Princípio da Segregação de Interfaces - ISP)

Onde se aplica: Em vez de uma interface monolítica genérica para todos os acessos a banco, criamos contratos enxutos e específicos para cada entidade (IMatchDAO, ICurtidaDAO, ICandidatoDAO), garantindo que nenhuma classe implemente métodos desnecessários.

5.  (Princípio da Inversão de Dependência - DIP)

Onde se aplica: Os DAOs deixaram de acoplar instâncias estáticas de conexão de banco de dados. Agora, utilizam o princípio de Injeção de Dependência via Construtor, recebendo a interface IConexaoFactory (ConexaoBD), o que facilita imensamente o isolamento em testes de integração.


### Atualização: Refatoração, Clean Code e Padrões de Projeto

O sistema passou por uma rigorosa revisão arquitetural focada em excelência técnica, aplicando princípios avançados de Orientação a Objetos, SOLID e Clean Code:

**Padrões de Projeto (Design Patterns):**

Command: Substituição da estrutura rígida de switch-case na camada de visualização (MenuView) por um roteamento dinâmico de comandos.

Factory & Singleton: Centralização e gerenciamento restrito das conexões com o banco de dados PostgreSQL.

**SOLID e Boas Práticas:**

Princípio DRY (Don't Repeat Yourself): Centralização das lógicas de validação (como tamanho de CPF, CNPJ e CEP) na classe utilitária ValidacaoUtils.

Princípio OCP (Open/Closed Principle): Menu principal refatorado para ser extensível sem necessidade de modificar o código base.

Otimização: Resolução de warnings apontados por análise estática, remoção de código morto (variáveis, parâmetros e DAOs ociosos) e conversão de métodos sem estado para static.

**Experiência de Uso (UX):**

Adição de prompts de confirmação de segurança (S/N) no terminal para prevenir exclusões acidentais de registros.

👤 Autor

Sóstenes Marques Maciel

Projeto desenvolvido durante o programa Acelera ZG.