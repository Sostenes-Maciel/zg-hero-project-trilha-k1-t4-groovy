package br.com.zg.acelera.service

import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.repository.BancodeDados

class GerenciadorDePerfis {
    BancodeDados dados = new BancodeDados()

    void listarCandidatos() {
        println("\n-- Candidatos Cadastrados --")
        if (dados.candidatos.isEmpty()) {
            println("Nenhum candidato cadastrado ainda.\n")
            return
        }
        dados.candidatos.each { escrever ->
            println "Nome: ${escrever.nome} | CPF: ${escrever.cpf} | Estado: ${escrever.estado}"
            println "Competências: ${escrever.competencias.join(', ')}"
            println "-" * 60
        }
        println()
    }

    void listarEmpresas() {
        println("\n-- Empresas Cadastradas --")
        if (dados.empresas.isEmpty()) {
            println("Nenhuma empresa cadastrada ainda.\n")
            return
        }
        dados.empresas.each { escrever ->
            println "Nome: ${escrever.nome} | CNPJ: ${escrever.cnpj} | País: ${escrever.pais}"
            println "Competências desejadas: ${escrever.competencias.join(', ')}"
            println "-" * 60
        }
        println()
    }

    void cadastroNovo(Scanner sc) {
        println("\n-- Opções de Cadastro --")
        println("1 - Candidato")
        println("2 - Empresa")
        print("Qual deseja cadastrar: ")

        try {
            if (!sc.hasNextInt()) {
                println("Erro: Entrada inválida. Retornando ao menu.")
                sc.next()
                return
            }
            int opcaoadd = sc.nextInt()
            sc.nextLine()

            if (opcaoadd == 1) {
                try {
                    println "\n--- CADASTRAR NOVO CANDIDATO ---"
                    print "Nome: "
                    String nome = sc.nextLine()

                    print "E-mail: "
                    String email = sc.nextLine()

                    print "CPF: "
                    String cpf = sc.nextLine()

                    print "Idade (apenas números): "
                    if (!sc.hasNextInt()) {
                        println "Erro: A idade deve ser um número inteiro. Cadastro cancelado."
                        sc.next()
                        return
                    }
                    int idade = sc.nextInt()
                    sc.nextLine()

                    print "Estado: "
                    String estado = sc.nextLine()

                    print "CEP: "
                    String cep = sc.nextLine()

                    print "Descrição pessoal: "
                    String descricao = sc.nextLine()

                    print "Competências (separadas por vírgula, ex: Java, Groovy, SQL): "
                    String entradaComps = sc.nextLine()
                    List<String> competencias = entradaComps.tokenize(',').collect { it.trim() }

                    Candidato novoCandidato = new Candidato(
                            nome: nome, email: email, cpf: cpf, idade: idade,
                            estado: estado, cep: cep, descricao: descricao, competencias: competencias
                    )

                    dados.cadastrarCandidato(novoCandidato)
                    println("Candidato cadastrado com sucesso!\n")

                } catch (Exception e) {
                    println "Erro ao cadastrar candidato: ${e.message}"
                }

            } else if (opcaoadd == 2) {
                try {
                    println "\n--- CADASTRAR NOVA EMPRESA ---"
                    print "Nome da Empresa: "
                    String nome = sc.nextLine()

                    print "E-mail Corporativo: "
                    String email = sc.nextLine()

                    print "CNPJ: "
                    String cnpj = sc.nextLine()

                    print "País: "
                    String pais = sc.nextLine()

                    print "Estado: "
                    String estado = sc.nextLine()

                    print "CEP: "
                    String cep = sc.nextLine()

                    print "Descrição da empresa: "
                    String descricao = sc.nextLine()

                    print "Competências desejadas (separadas por vírgula): "
                    String entradaComps = sc.nextLine()
                    List<String> competencias = entradaComps.tokenize(',').collect { it.trim() }

                    Empresa novaEmpresa = new Empresa(
                            nome: nome, email: email, cnpj: cnpj, pais: pais,
                            estado: estado, cep: cep, descricao: descricao, competencias: competencias
                    )

                    dados.cadastrarEmpresa(novaEmpresa)
                    println("Empresa cadastrada com sucesso!\n")

                } catch (Exception e) {
                    println "Erro ao cadastrar empresa: ${e.message}"
                }
            } else {
                println("Opção de cadastro inválida! Retornando ao menu.")
            }
        } catch (Exception e) {
            println("Erro no cadastro: ${e.message}")
        }
    }
}