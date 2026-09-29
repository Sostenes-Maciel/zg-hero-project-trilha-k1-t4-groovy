package br.com.zg.acelera.service

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.dao.BancodeDados


class GerenciadorDePerfis {
    BancodeDados dados = new BancodeDados()

    CandidatoDAO candidatoDAO = new CandidatoDAO()
    CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    void listarCandidatos() {
        println("\n-- Candidatos Cadastrados --")

        List<Candidato> candidatos = candidatoDAO.listarTodos()

        if (candidatos.isEmpty()) {
            println("Nenhum candidato cadastrado ainda.\n")
            return
        }

        candidatos.each { candidato ->
            candidato.competencias = competenciaDAO.listarDoCandidato(candidato.cpf)

            println "Nome: ${candidato.nome} | CPF: ${candidato.cpf} | Estado: ${candidato.estado}"
            println "Competências: ${candidato.competencias.join(', ')}"
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
                    String nome = sc.nextLine().trim()
                    ValidarCandidato.validarNome(nome)

                    print "E-mail: "
                    String email = sc.nextLine().trim()
                    ValidarCandidato.validarEmail(email)

                    print "CPF: "
                    String cpf = sc.nextLine().trim()
                    ValidarCandidato.validarCpf(cpf)

                    print "Idade (apenas números): "
                    if (!sc.hasNextInt()) {
                        sc.nextLine()
                        throw new IllegalArgumentException("Idade deve ser um número inteiro.")
                    }

                    int idade = sc.nextInt()
                    sc.nextLine()
                    ValidarCandidato.validarIdade(idade)

                    print "País: "
                    String pais = sc.nextLine().trim()

                    print "Estado: "
                    String estado = sc.nextLine().trim()
                    ValidarCandidato.validarEstado(estado)

                    print "CEP: "
                    String cep = sc.nextLine().trim()
                    ValidarCandidato.validarCep(cep)

                    print "Descrição pessoal: "
                    String descricao = sc.nextLine().trim()
                    ValidarCandidato.validarDescricao(descricao)

                    print "Competências: "
                    String entradaComps = sc.nextLine()

                    List<String> competencias = entradaComps
                            .tokenize(',')
                            .collect { it.trim() }

                    ValidarCandidato.validarCompetencias(competencias)

                    Candidato novoCandidato = new Candidato(
                            nome: nome,
                            email: email,
                            cpf: cpf,
                            idade: idade,
                            pais: pais,
                            estado: estado,
                            cep: cep,
                            descricao: descricao,
                            competencias: competencias
                    )

                    candidatoDAO.cadastrar(novoCandidato)

                    competencias.each { competencia ->
                        competenciaDAO.garantirCompetencia(competencia)
                        competenciaDAO.adicionarAoCandidato(novoCandidato.cpf, competencia)
                    }

                    println "Candidato cadastrado com sucesso!\n"

                } catch (IllegalArgumentException e) {
                    println "Erro: ${e.message}"
                } catch (Exception e) {
                    println "Erro inesperado: ${e.message}"
                }

            } else if (opcaoadd == 2) {
                try {
                    println "\n--- CADASTRAR NOVA EMPRESA ---"

                    print "Nome da Empresa: "
                    String nome = sc.nextLine().trim()
                    ValidarEmpresa.validarNome(nome)

                    print "E-mail Corporativo: "
                    String email = sc.nextLine().trim()
                    ValidarEmpresa.validarEmail(email)

                    print "CNPJ: "
                    String cnpj = sc.nextLine().trim()
                    ValidarEmpresa.validarCnpj(cnpj)

                    print "País: "
                    String pais = sc.nextLine().trim()
                    ValidarEmpresa.validarPais(pais)

                    print "Estado: "
                    String estado = sc.nextLine().trim()
                    ValidarEmpresa.validarEstado(estado)

                    print "CEP: "
                    String cep = sc.nextLine().trim()
                    ValidarEmpresa.validarCep(cep)

                    print "Descrição da empresa: "
                    String descricao = sc.nextLine().trim()
                    ValidarEmpresa.validarDescricao(descricao)

                    print "Competências desejadas: "
                    String entradaComps = sc.nextLine()

                    List<String> competencias = entradaComps
                            .tokenize(',')
                            .collect { it.trim() }

                    ValidarEmpresa.validarCompetencias(competencias)

                    Empresa novaEmpresa = new Empresa(
                            nome: nome,
                            email: email,
                            cnpj: cnpj,
                            pais: pais,
                            estado: estado,
                            cep: cep,
                            descricao: descricao,
                            competencias: competencias
                    )

                    dados.cadastrarEmpresa(novaEmpresa)

                    println "Empresa cadastrada com sucesso!\n"

                } catch (IllegalArgumentException e) {
                    println "Erro ao cadastrar empresa: ${e.message}"

                } catch (Exception e) {
                    println "Erro no cadastro: ${e.message}"
                }
            }
        } catch (Exception e) {
            println "Erro ${e.message}"
        }
    }
}