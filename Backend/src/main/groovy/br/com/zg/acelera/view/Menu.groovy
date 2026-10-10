package br.com.zg.acelera.view

import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.service.GerenciadorDePerfis
import br.com.zg.acelera.service.GerenciadorDeMatches

class Menu {

    GerenciadorDePerfis gerenciador = new GerenciadorDePerfis()
    Scanner sc = new Scanner(System.in)

    // Método utilitário para aplicar o princípio DRY na leitura de inteiros
    private int lerOpcaoInt(Scanner sc) {
        if (!sc.hasNextInt()) {
            sc.nextLine() // limpa o buffer
            return -1     // retorna valor que cai no default do switch
        }
        int opcao = sc.nextInt()
        sc.nextLine()
        return opcao
    }

    void iniciar() {
        int opcao = 0
        println "Bem-vindo ao Linkertinder <3"

        while (opcao != 5) {
            println "\n1 - Gerenciar candidatos"
            println "2 - Gerenciar Empresas"
            println "3 - Cadastrar"
            println "4 - Gerenciar Vagas"
            println "5 - Sair"
            print "Escolha uma opção: "

            opcao = lerOpcaoInt(sc)

            switch (opcao) {
                case 1: gerenciarCandidatos(sc); break
                case 2: gerenciarEmpresas(sc); break
                case 3: gerenciador.cadastroNovo(sc); break
                case 4: gerenciador.gerenciarVagas(sc); break
                case 5: println("Saindo do Linketinder. Até logo!"); break
                default: println("Opção inválida.")
            }
        }
    }

    void gerenciarCandidatos(Scanner sc) {
        Candidato candidato = gerenciador.selecionarCandidato(sc)
        if (candidato == null) return

        int opcao = 0
        while (opcao != 6) {
            println "\n--- CANDIDATO: ${candidato.nome} ---"
            println "1 - Atualizar dados"
            println "2 - Gerenciar competências"
            println "3 - Curtir vaga"
            println "4 - Ver meus matches"
            println "5 - Excluir perfil do candidato"
            println "6 - Voltar"
            print "Escolha uma opção: "

            opcao = lerOpcaoInt(sc)

            switch (opcao) {
                case 1: gerenciador.atualizarCandidatoPeloTerminal(sc, candidato); break
                case 2: gerenciarCompetenciasCandidato(sc, candidato); break
                case 3: gerenciador.curtirVagaPeloTerminal(sc, candidato); break
                case 4: GerenciadorDeMatches.exibirMatchesDoCandidato(candidato.cpf); break
                case 5:
                    gerenciador.excluirCandidatoPeloTerminal(sc, candidato)
                    opcao = 6 // Sai do loop após excluir
                    break
                case 6: println("Voltando..."); break
                default: println("Opção inválida.")
            }
        }
    }

    void gerenciarEmpresas(Scanner sc) {
        Empresa empresa = gerenciador.selecionarEmpresa(sc)
        if (empresa == null) return

        int opcao = 0
        while (opcao != 6) {
            println "\n--- EMPRESA: ${empresa.nome} ---"
            println "1 - Atualizar dados"
            println "2 - Gerenciar competências desejadas"
            println "3 - Curtir candidato"
            println "4 - Ver meus matches"
            println "5 - Excluir perfil da empresa"
            println "6 - Voltar"
            print "Escolha uma opção: "

            opcao = lerOpcaoInt(sc)

            switch (opcao) {
                case 1: gerenciador.atualizarEmpresaPeloTerminal(sc, empresa); break
                case 2: gerenciarCompetenciasEmpresa(sc, empresa); break
                case 3: gerenciador.curtirCandidatoPeloTerminal(sc, empresa); break
                case 4: GerenciadorDeMatches.exibirMatchesDaEmpresa(empresa.cnpj); break
                case 5:
                    gerenciador.excluirEmpresaPeloTerminal(sc, empresa)
                    opcao = 6
                    break
                case 6: println("Voltando..."); break
                default: println("Opção inválida.")
            }
        }
    }

    void gerenciarCompetenciasCandidato(Scanner sc, Candidato candidato) {
        int opcao = 0
        while (opcao != 4) {
            println "\n--- COMPETÊNCIAS DE ${candidato.nome} ---"
            println "1 - Listar competências"
            println "2 - Adicionar competência"
            println "3 - Remover competência"
            println "4 - Voltar"
            print "Escolha uma opção: "

            opcao = lerOpcaoInt(sc)

            switch (opcao) {
                case 1:
                    List<String> competencias = gerenciador.listarCompetenciasDoCandidato(candidato)
                    if (competencias.isEmpty()) {
                        println "Nenhuma competência cadastrada."
                    } else {
                        competencias.eachWithIndex { comp, i -> println "${i + 1} - ${comp}" }
                    }
                    break

                case 2:
                    print "Nome da competência: "
                    String competencia = sc.nextLine().trim()
                    if (competencia.isEmpty()) {
                        println "Competência inválida."
                    } else if (competencia.contains(",")) {
                        println "Digite apenas uma competência por vez."
                    } else {
                        gerenciador.adicionarCompetenciaAoCandidato(candidato, competencia)
                        println "Competência adicionada com sucesso!"
                    }
                    break

                case 3:
                    List<String> competenciasParaRemover = gerenciador.listarCompetenciasDoCandidato(candidato)
                    if (competenciasParaRemover.isEmpty()) {
                        println "Nenhuma competência cadastrada."
                        break
                    }
                    println "\nCompetências:"
                    competenciasParaRemover.eachWithIndex { comp, i -> println "${i + 1} - ${comp}" }

                    print "Selecione a competência: "
                    int indice = lerOpcaoInt(sc)

                    if (indice < 1 || indice > competenciasParaRemover.size()) {
                        println "Competência inválida."
                    } else {
                        gerenciador.removerCompetenciaDoCandidato(candidato, competenciasParaRemover[indice - 1])
                        println "Competência removida com sucesso!"
                    }
                    break

                case 4: println("Voltando..."); break
                default: println("Opção inválida.")
            }
        }
    }

    void gerenciarCompetenciasEmpresa(Scanner sc, Empresa empresa) {
        int opcao = 0
        while (opcao != 4) {
            println "\n--- COMPETÊNCIAS DESEJADAS: ${empresa.nome} ---"
            println "1 - Listar competências"
            println "2 - Adicionar competência"
            println "3 - Remover competência"
            println "4 - Voltar"
            print "Escolha uma opção: "

            opcao = lerOpcaoInt(sc)

            switch (opcao) {
                case 1:
                    List<String> competencias = gerenciador.listarCompetenciasDaEmpresa(empresa)
                    if (competencias.isEmpty()) {
                        println "Nenhuma competência desejada cadastrada."
                    } else {
                        competencias.eachWithIndex { comp, i -> println "${i + 1} - ${comp}" }
                    }
                    break

                case 2:
                    print "Nome da competência: "
                    String competencia = sc.nextLine().trim()
                    if (competencia.isEmpty()) {
                        println "Competência inválida."
                    } else if (competencia.contains(",")) {
                        println "Digite apenas uma competência por vez."
                    } else {
                        gerenciador.adicionarCompetenciaAEmpresa(empresa, competencia)
                        println "Competência adicionada com sucesso!"
                    }
                    break

                case 3:
                    List<String> competenciasParaRemover = gerenciador.listarCompetenciasDaEmpresa(empresa)
                    if (competenciasParaRemover.isEmpty()) {
                        println "Nenhuma competência desejada cadastrada."
                        break
                    }
                    println "\nCompetências desejadas:"
                    competenciasParaRemover.eachWithIndex { comp, i -> println "${i + 1} - ${comp}" }

                    print "Selecione a competência: "
                    int indice = lerOpcaoInt(sc)

                    if (indice < 1 || indice > competenciasParaRemover.size()) {
                        println "Competência inválida."
                    } else {
                        gerenciador.removerCompetenciaDaEmpresa(empresa, competenciasParaRemover[indice - 1])
                        println "Competência removida com sucesso!"
                    }
                    break

                case 4: println("Voltando..."); break
                default: println("Opção inválida.")
            }
        }
    }
}