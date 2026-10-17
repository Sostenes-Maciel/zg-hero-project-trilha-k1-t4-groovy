package br.com.zg.acelera.command

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Candidato

class GerenciarCompetenciasCandidatoCommand implements ICommand {

    private final CandidatoController candidatoController
    private final Scanner sc
    private final Candidato candidato

    GerenciarCompetenciasCandidatoCommand(CandidatoController candidatoController, Scanner sc, Candidato candidato) {
        this.candidatoController = candidatoController
        this.sc = sc
        this.candidato = candidato
    }

    @Override
    void executar() {
        int opcao = 0
        while (opcao != 4) {
            println "\n--- COMPETÊNCIAS DE ${candidato.nome.toUpperCase()} ---"
            println "Atuais: ${candidato.competencias.isEmpty() ? 'Nenhuma' : candidato.competencias.join(', ')}"
            println "1 - Adicionar nova competência"
            println "2 - Remover uma competência"
            println "3 - Limpar todas"
            println "4 - Voltar"
            print "Escolha: "

            try {
                opcao = Integer.parseInt(sc.nextLine().trim())
            } catch (NumberFormatException ignored) {
                opcao = 0
            }

            switch (opcao) {
                case 1:
                    print "Digite a nova competência: "
                    String nova = sc.nextLine().trim()
                    if (!nova.isEmpty()) {
                        candidatoController.adicionarCompetencia(candidato, nova)

                        if (!candidato.competencias.contains(nova)) {
                            candidato.competencias.add(nova)
                        }
                        println "Competência adicionada com sucesso!"
                    }
                    break
                case 2:
                    print "Qual competência deseja remover? (Digite exatamente igual): "
                    String remover = sc.nextLine().trim()
                    if (candidato.competencias.contains(remover)) {
                        candidatoController.removerCompetencia(candidato, remover)

                        candidato.competencias.remove(remover)
                        println "Competência removida com sucesso!"
                    } else {
                        println "Competência não encontrada no seu perfil."
                    }
                    break
                case 3:
                    if (!candidato.competencias.isEmpty()) {
                        candidato.competencias.each { comp ->
                            candidatoController.removerCompetencia(candidato, comp)
                        }
                        candidato.competencias.clear()
                        println "Todas as competências foram apagadas com sucesso!"
                    } else {
                        println "O candidato já não possui competências cadastradas."
                    }
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