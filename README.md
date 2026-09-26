#  Linketinder MVP — Acelera ZG

Projeto desenvolvido como parte do desafio técnico do **Acelera ZG**.

O **Linketinder** é um sistema de conexões profissionais executado via terminal (CLI), permitindo o cadastro e a visualização de **candidatos e empresas**.
# Atualização

O Linketinder é uma plataforma voltada para a conexão entre candidatos e empresas, permitindo o cadastro e a visualização de perfis e oportunidades de trabalho.

O projeto possui uma versão de backend desenvolvida em Groovy e um frontend web desenvolvido com TypeScript, com foco em uma interface simples, organizada e fácil de utilizar.
O projeto foi desenvolvido com foco em:

* Programação Orientada a Objetos (POO)
* Separação de responsabilidades
* Organização e manutenção do código
* Tratamento de erros
* Versionamento semântico
* Boas práticas de desenvolvimento


---

## Tecnologias utilizadas

* **Groovy** — Linguagem utilizada no desenvolvimento, aproveitando sua sintaxe concisa, construtores nomeados e recursos de manipulação de coleções.
* **Gradle** — Gerenciamento do projeto, build e execução.
* **Git** — Controle de versão.
* **GitHub** — Hospedagem e gerenciamento do repositório.

---

## Funcionalidades

### Listagem de candidatos

Permite visualizar os candidatos cadastrados no sistema, apresentando:

* Nome
* CPF
* Estado
* Competências

### Listagem de empresas

Permite visualizar as empresas cadastradas, apresentando:

* Nome
* CNPJ
* País
* Estado
* Competências desejadas

### Cadastro dinâmico

Permite cadastrar novos perfis durante a execução do programa:

* Candidatos
* Empresas

As competências podem ser informadas separadas por vírgula.

**Exemplo:**

```text
Java, Groovy, Python
```

O sistema transforma automaticamente a entrada em uma lista de competências.


## Como executar

### Via IntelliJ IDEA

1. Clone este repositório.
2. Abra a pasta do projeto no **IntelliJ IDEA**.
3. Aguarde a sincronização do Gradle.
4. Navegue até:

```text
src/main/groovy/br/com/zg/acelera/Main.groovy
```

5. Execute o método `main`.
6. Interaja com o sistema através do console da IDE.

### Via terminal

Abra o terminal na raiz do projeto e execute:

#### Linux / macOS

```bash
./gradlew run
```

#### Windows

```bash
gradlew run
```

---

## Arquitetura e estrutura do projeto

O projeto utiliza uma organização baseada na **separação de responsabilidades**, distribuindo as classes em diferentes pacotes.

```text
src/
└── main/
    └── groovy/
        └── br/
            └── com/
                └── zg/
                    └── acelera/
                        ├── Main.groovy
                        │
                        ├── model/
                        │   ├── IPessoa.groovy
                        │   ├── Pessoa.groovy
                        │   ├── Candidato.groovy
                        │   └── Empresa.groovy
                        │
                        ├── view/
                        │   └── Menu.groovy
                        │
                        ├── service/
                        │   └── GerenciadorDePerfis.groovy
                        │
                        └── repository/
                            └── BancodeDados.groovy
```

### `Main`

Pacote:

```text
br.com.zg.acelera
```

Responsável pelo **ponto de entrada da aplicação** e inicialização do sistema.

### `view`

Pacote:

```text
br.com.zg.acelera.view
```

Contém a classe `Menu`, responsável pela interação com o usuário através do terminal, incluindo:

* Exibição do menu
* Captura das opções
* Entrada de dados
* Exibição das informações

### `service`

Pacote:

```text
br.com.zg.acelera.service
```

Contém o `GerenciadorDePerfis`, responsável pela **lógica de negócio** e pelo gerenciamento dos fluxos relacionados aos cadastros.

### `repository`

Pacote:

```text
br.com.zg.acelera.repository
```

Contém a classe `BancodeDados.ts`, responsável pelo armazenamento e gerenciamento dos dados em memória.

### `model`

Pacote:

```text
br.com.zg.acelera.model
```

Contém as entidades utilizadas pelo sistema:

* `IPessoa` — contrato das entidades de pessoa.
* `Pessoa` — classe abstrata com atributos e comportamentos compartilhados.
* `Candidato` — representa os candidatos cadastrados.
* `Empresa` — representa as empresas cadastradas.

---

## Menu do sistema

Ao executar a aplicação, o usuário encontra as seguintes opções:

```text
1 - Listar candidatos
2 - Listar Empresas
3 - Cadastrar
4 - Sair
```

A opção **Cadastrar** permite escolher entre:

```text
1 - Candidato
2 - Empresa
```

---

##  Atualização : Sistema de Curtidas e Painel de Matches

O projeto foi atualizado com a implementação do núcleo de interações (curtidas e matches) entre candidatos, empresas e vagas, garantindo uma visualização clara do status de relacionamento na plataforma.

### Principais Alterações

* **Nova Entidade `Curtida`:** Criada para atuar como o elo central do sistema[cite: 1], registrando o candidato, a vaga associada e o aceite da empresa.
* **Lógica de Match Automatizada:** Implementação de método validador que confirma o *Match* apenas quando há reciprocidade — ou seja, quando o candidato curte a vaga e a respectiva empresa retribui a curtida na mesma instância de relacionamento[cite: 1].
* **Isolamento de Dados (Amostra Segura):** Criação de um repositório dedicado na memória (`curtidasSimulacao`)[cite: 1] para gerenciar os dados de testes e demonstrações de forma isolada, evitando conflitos ou apagamentos indesejados no cadastro oficial de usuários e vagas do sistema.
* **Painel Visual no Terminal:** Adicionada uma nova opção interativa no menu do console que exibe um relatório formatado em tempo real, diferenciando visualmente os status entre **[  MATCH! ]** e **[ PENDENTE ]**.


* ## Atualização: Testes Unitários com Spock

O projeto foi atualizado com a implementação de testes unitários utilizando o **Spock Framework** e **Groovy**, com foco no cadastro e validação de candidatos e empresas.

### Principais Alterações

* **Testes de Cadastro:** Implementados testes para verificar a inserção de novos candidatos e empresas nas listas do sistema, garantindo que o cadastro adicione corretamente os novos elementos.
* **Testes de Criação de Objetos:** Adicionados testes para validar a criação das entidades `Candidato` e `Empresa`, verificando seus principais atributos.
* **Testes de Validação:** Criados testes unitários para as regras de validação de candidatos e empresas, contemplando entradas válidas e inválidas.
* **Testes de Fronteira:** Adicionados casos para verificar os limites das validações, como idade, CPF, CNPJ e CEP.
* **Organização dos Testes:** Os testes foram separados em classes `Spec`, mantendo cada unidade do sistema testada de forma independente.

## Atualização: Implementação FrontEnd

**Privacidade**

O sistema possui anonimização entre os dois lados da plataforma:

Na visão da empresa, os candidatos são apresentados de forma anônima.
Na visão do candidato, a empresa responsável pela vaga permanece confidencial.

**Tecnologias utilizadas**

Frontend

* TypeScript — linguagem utilizada para a implementação da lógica da aplicação.
* HTML5 — estrutura das páginas e formulários.
* CSS3 — estilização, responsividade e organização visual da interface.
* Vite — ferramenta utilizada para desenvolvimento e execução do frontend.
* Chart.js — criação do gráfico de competências no perfil da empresa.
* Country State City (@countrystatecity/countries-browser) — carregamento de países e estados no cadastro de empresas.
* LocalStorage — persistência dos dados cadastrados no navegador.

Backend

* Groovy — linguagem utilizada na implementação do backend.
* Gradle — gerenciamento e build do projeto.
* JUnit — testes automatizados.
* Git e GitHub — versionamento e hospedagem do código.

**Arquitetura**

O frontend utiliza uma organização baseada na separação de responsabilidades:

* Model: representa as entidades utilizadas pela aplicação.
* Repository: responsável pelo armazenamento dos dados.
* Service: concentra regras de negócio e cadastros.
* View: responsável pela apresentação das telas.
* Components: concentra componentes reutilizáveis, como o menu principal.

**Estrutura do frontend**

### Estrutura do Frontend

```text
Frontend/
├── index.html
├── package.json
├── src/
│   ├── main.ts
│   ├── style.css
│   │
│   ├── components/
│   │   └── Menu.ts
│   │
│   ├── model/
│   │   ├── Candidato.ts
│   │   ├── Empresa.ts
│   │   └── Vaga.ts
│   │
│   ├── repository/
│   │   └── BancodeDados.ts
│   │
│   ├── service/
│   │   ├── CadastroCandidato.ts
│   │   ├── CadastroEmpresa.ts
│   │   ├── CadastroVaga.ts
│   │   ├── ValidarCandidato.ts
│   │   └── ValidarEmpresa.ts
│   │
│   └── view/
│       ├── ListaCandidatos.ts
│       ├── ListaEmpresas.ts
│       ├── ListaVagas.ts
│       ├── PerfilCandidato.ts
│       ├── PerfilEmpresa.ts
│       └── PerfilVaga.ts
```


**Como executar o frontend**

Entre na pasta do frontend:
```text
cd Frontend
```

Instale as dependências:
```text
npm install
```
Execute o ambiente de desenvolvimento:
```text
npm run dev
```
O Vite exibirá no terminal o endereço local para acessar a aplicação pelo navegador.

Gerar a versão de produção

Para verificar a compilação do projeto:
```text
npm run build
```
O comando deve finalizar sem erros de TypeScript.

## Atualização: Índice de Afinidade

Foi implementado um índice de afinidade entre candidatos e empresas com base nas competências cadastradas.

Na lista de vagas, o candidato consegue visualizar o percentual de afinidade com a empresa responsável pela vaga, sem que a identidade da empresa seja revelada.

Também foi adicionada, de forma opcional, a exibição do percentual de afinidade na lista de candidatos do perfil da empresa, mantendo a identidade dos candidatos anônima.

### Como funciona

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

### Implementação

A regra de cálculo foi centralizada na função:

```text
src/service/calcularAfinidade.ts
```

Essa função é reutilizada tanto na lista de vagas quanto na lista de candidatos do perfil da empresa.


## Autor

**Sóstenes Marques Maciel**

Projeto desenvolvido durante o **Acelera ZG**.

---
