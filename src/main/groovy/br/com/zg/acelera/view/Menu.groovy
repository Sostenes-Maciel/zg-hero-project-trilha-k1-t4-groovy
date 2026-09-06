package br.com.zg.acelera.view

import br.com.zg.acelera.service.GerenciadorDePerfis
import br.com.zg.acelera.service.GerenciadordeMatches

class Menu {

    GerenciadorDePerfis gerenciador = new GerenciadorDePerfis()
    Scanner sc = new Scanner(System.in)

    void iniciar() {
        int opcao = 0
        println "Bem-vindo ao Linkertinder <3\n"

        while (opcao != 5) {

            println("1 - Listar candidatos")
            println("2 - Listar Empresas")
            println("3 - Cadastrar")
            println("4 - Ver Resultados de Curtidas e Matches")
            println("5 - Sair")

            try {
                print "\nEscolha uma opção: "
                opcao = sc.nextInt()
                sc.nextLine()

                switch (opcao) {
                    case 1:
                        gerenciador.listarCandidatos()
                        break
                    case 2:
                        gerenciador.listarEmpresas()
                        break
                    case 3:
                        gerenciador.cadastroNovo(sc)
                        break
                    case 4:
                        GerenciadordeMatches.simularInteracoes()
                        GerenciadordeMatches.exibirPainel()
                        break
                    case 5:
                        println("Saindo do Linketinder. Até logo!")
                        break
                    default:
                        println("Opção inválida! Tente novamente.")
                }
            } catch (InputMismatchException e) {
                println("Erro: Por favor, digite apenas números!")
                sc.nextLine()
                opcao = 0
            } catch (Exception e) {
                println "Erro! ${e.message}"
            }
        }
    }
}