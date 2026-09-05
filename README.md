#  Linketinder MVP — Acelera ZG

Projeto desenvolvido como parte do desafio técnico do **Acelera ZG**.

O **Linketinder** é um sistema de conexões profissionais executado via terminal (CLI), permitindo o cadastro e a visualização de **candidatos e empresas**.

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

Contém a classe `BancodeDados`, responsável pelo armazenamento e gerenciamento dos dados em memória.

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


## Autor

**Sóstenes Marques Maciel**

Projeto desenvolvido durante o **Acelera ZG**.

---
