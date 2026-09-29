package br.com.zg.acelera.view

import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.service.GerenciadorDePerfis
import br.com.zg.acelera.service.GerenciadordeMatches

class Menu {

    GerenciadorDePerfis gerenciador = new GerenciadorDePerfis()
    Scanner sc = new Scanner(System.in)

    void iniciar() {
        int opcao = 0
        println "Bem-vindo ao Linkertinder <3\n"

        while (opcao != 6) {

            println("1 - Gerenciar candidatos")
            println("2 - Gerenciar Empresas")
            println("3 - Cadastrar")
            println("4 - Ver Resultados de Curtidas e Matches")
            println("5 - Gerenciar Vagas")
            println("6 - Sair")

            try {
                print "\nEscolha uma opção: "
                opcao = sc.nextInt()
                sc.nextLine()

                switch (opcao) {
                    case 1:
                        gerenciarCandidatos(sc)
                        break
                    case 2:
                        gerenciarEmpresas(sc)
                        break
                    case 3:
                        gerenciador.cadastroNovo(sc)
                        break
                    case 4:
                        GerenciadordeMatches.simularInteracoes()
                        GerenciadordeMatches.exibirPainel()
                        break
                    case 5:
                        gerenciador.gerenciarVagas(sc)
                        break

                    case 6:
                        println("Saindo do Linketinder. Até logo!")
                        break
                }
            } catch (InputMismatchException ignored) {
                println("Erro: Por favor, digite apenas números!")
                sc.nextLine()
                opcao = 0
            } catch (Exception e) {
                println "Erro! ${e.message}"
            }
        }
    }
    void gerenciarCandidatos(Scanner sc) {

        int opcao = 0

        while (opcao != 5) {

            println "\n--- CANDIDATOS ---"
            println "1 - Listar candidatos"
            println "2 - Atualizar candidato"
            println "3 - Gerenciar competências"
            println "4 - Excluir candidato"
            println "5 - Voltar"

            print "Escolha uma opção: "

            if (!sc.hasNextInt()) {
                println "Opção inválida."
                sc.nextLine()
                continue
            }

            opcao = sc.nextInt()
            sc.nextLine()

            switch (opcao) {

                case 1:
                    gerenciador.listarCandidatos()
                    break
                case 2:
                    gerenciador.atualizarCandidatoPeloTerminal(sc)
                    break
                case 3:
                    gerenciarCompetenciasCandidato(sc)
                    break

                case 4:
                    gerenciador.excluirCandidatoPeloTerminal(sc)
                    break

                case 5:
                    println "Voltando..."
                    break

                default:
                    println "Opção inválida."
            }
        }
    }
    void gerenciarEmpresas(Scanner sc) {

        int opcao = 0

        while (opcao != 5) {

            println "\n--- EMPRESAS ---"
            println "1 - Listar empresas"
            println "2 - Atualizar empresa"
            println "3 - Gerenciar competências desejadas"
            println "4 - Excluir empresa"
            println "5 - Voltar"

            print "Escolha uma opção: "

            if (!sc.hasNextInt()) {
                println "Opção inválida."
                sc.nextLine()
                continue
            }

            opcao = sc.nextInt()
            sc.nextLine()

            switch (opcao) {
                case 1:
                    gerenciador.listarEmpresas()
                    break

                case 2:
                    gerenciador.atualizarEmpresaPeloTerminal(sc)
                    break
                case 3:
                    gerenciarCompetenciasEmpresa(sc)
                    break
                case 4:
                    gerenciador.excluirEmpresaPeloTerminal(sc)
                    break
                case 5:
                    println "Voltando..."
                    break

                default:
                    println "Opção inválida."
            }
        }
    }
    void gerenciarCompetenciasCandidato(Scanner sc) {

        Candidato candidato = gerenciador.selecionarCandidato(sc)

        if (candidato == null) {
            return
        }

        int opcao = 0

        while (opcao != 4) {

            println "\n--- COMPETÊNCIAS DE ${candidato.nome} ---"
            println "1 - Listar competências"
            println "2 - Adicionar competência"
            println "3 - Remover competência"
            println "4 - Voltar"

            print "Escolha uma opção: "

            if (!sc.hasNextInt()) {
                println "Opção inválida."
                sc.nextLine()
                continue
            }

            opcao = sc.nextInt()
            sc.nextLine()

            switch (opcao) {

                case 1:
                    List<String> competencias =
                            gerenciador.listarCompetenciasDoCandidato(candidato)

                    if (competencias.isEmpty()) {
                        println "Nenhuma competência cadastrada."
                    } else {
                        competencias.eachWithIndex { competencia, indice ->
                            println "${indice + 1} - ${competencia}"
                        }
                    }
                    break

                case 2:
                    print "Nome da competência: "
                    String competencia = sc.nextLine().trim()

                    if (competencia.isEmpty()) {
                        println "Competência inválida."
                        break
                    }

                    gerenciador.adicionarCompetenciaAoCandidato(
                            candidato,
                            competencia
                    )

                    println "Competência adicionada com sucesso!"
                    break

                case 3:
                    List<String> competenciasParaRemover =
                            gerenciador.listarCompetenciasDoCandidato(candidato)

                    if (competenciasParaRemover.isEmpty()) {
                        println "Nenhuma competência cadastrada."
                        break
                    }

                    println "\nCompetências:"
                    competenciasParaRemover.eachWithIndex { competencia, indice ->
                        println "${indice + 1} - ${competencia}"
                    }

                    print "Selecione a competência: "

                    if (!sc.hasNextInt()) {
                        println "Opção inválida."
                        sc.nextLine()
                        break
                    }

                    int indice = sc.nextInt()
                    sc.nextLine()

                    if (indice < 1 || indice > competenciasParaRemover.size()) {
                        println "Competência inválida."
                        break
                    }

                    gerenciador.removerCompetenciaDoCandidato(
                            candidato,
                            competenciasParaRemover[indice - 1]
                    )

                    println "Competência removida com sucesso!"
                    break

                case 4:
                    println "Voltando..."
                    break

                default:
                    println "Opção inválida."
            }
        }
    }
    void gerenciarCompetenciasEmpresa(Scanner sc) {

        Empresa empresa = gerenciador.selecionarEmpresaParaEdicao(sc)

        if (empresa == null) {
            return
        }

        int opcao = 0

        while (opcao != 4) {

            println "\n--- COMPETÊNCIAS DESEJADAS: ${empresa.nome} ---"
            println "1 - Listar competências"
            println "2 - Adicionar competência"
            println "3 - Remover competência"
            println "4 - Voltar"

            print "Escolha uma opção: "

            if (!sc.hasNextInt()) {
                println "Opção inválida."
                sc.nextLine()
                continue
            }

            opcao = sc.nextInt()
            sc.nextLine()

            switch (opcao) {

                case 1:
                    List<String> competencias =
                            gerenciador.listarCompetenciasDaEmpresa(empresa)

                    if (competencias.isEmpty()) {
                        println "Nenhuma competência desejada cadastrada."
                    } else {
                        competencias.eachWithIndex { competencia, indice ->
                            println "${indice + 1} - ${competencia}"
                        }
                    }
                    break

                case 2:
                    print "Nome da competência: "
                    String competencia = sc.nextLine().trim()

                    if (competencia.isEmpty()) {
                        println "Competência inválida."
                        break
                    }

                    gerenciador.adicionarCompetenciaAEmpresa(
                            empresa,
                            competencia
                    )

                    println "Competência adicionada com sucesso!"
                    break

                case 3:
                    List<String> competenciasParaRemover =
                            gerenciador.listarCompetenciasDaEmpresa(empresa)

                    if (competenciasParaRemover.isEmpty()) {
                        println "Nenhuma competência desejada cadastrada."
                        break
                    }

                    println "\nCompetências desejadas:"
                    competenciasParaRemover.eachWithIndex { competencia, indice ->
                        println "${indice + 1} - ${competencia}"
                    }

                    print "Selecione a competência: "

                    if (!sc.hasNextInt()) {
                        println "Opção inválida."
                        sc.nextLine()
                        break
                    }

                    int indice = sc.nextInt()
                    sc.nextLine()

                    if (indice < 1 ||
                            indice > competenciasParaRemover.size()) {
                        println "Competência inválida."
                        break
                    }

                    gerenciador.removerCompetenciaDaEmpresa(
                            empresa,
                            competenciasParaRemover[indice - 1]
                    )

                    println "Competência removida com sucesso!"
                    break

                case 4:
                    println "Voltando..."
                    break

                default:
                    println "Opção inválida."
            }
        }
    }
}