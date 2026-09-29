package br.com.zg.acelera.view

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

        while (opcao != 4) {

            println "\n--- CANDIDATOS ---"
            println "1 - Listar candidatos"
            println "2 - Atualizar candidato"
            println "3 - Excluir candidato"
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
                    gerenciador.listarCandidatos()
                    break
                case 2:
                    gerenciador.atualizarCandidatoPeloTerminal(sc)
                    break

                case 3:
                    gerenciador.excluirCandidatoPeloTerminal(sc)
                    break

                case 4:
                    println "Voltando..."
                    break

                default:
                    println "Opção inválida."
            }
        }
    }
    void gerenciarEmpresas(Scanner sc) {

        int opcao = 0

        while (opcao != 4) {

            println "\n--- EMPRESAS ---"
            println "1 - Listar empresas"
            println "2 - Atualizar empresa"
            println "3 - Excluir empresa"
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
                    gerenciador.listarEmpresas()
                    break

                case 2:
                    gerenciador.atualizarEmpresaPeloTerminal(sc)
                    break

                case 3:
                    gerenciador.excluirEmpresaPeloTerminal(sc)
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