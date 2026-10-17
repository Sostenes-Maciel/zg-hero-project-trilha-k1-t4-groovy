package br.com.zg.acelera.command

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Candidato

class GerenciarCandidatosCommand implements ICommand {

    private final CandidatoController candidatoController
    private final VagaController vagaController
    private final MatchController matchController
    private final Scanner sc

    GerenciarCandidatosCommand(CandidatoController candidatoController, VagaController vagaController, MatchController matchController, Scanner sc) {
        this.candidatoController = candidatoController
        this.vagaController = vagaController
        this.matchController = matchController
        this.sc = sc
    }

    @Override
    void executar() {
        def respostaListagem = candidatoController.listar()
        if (respostaListagem.status != 200 || !respostaListagem.dados) {
            println "\nNenhum candidato cadastrado."
            return
        }

        println "\n--- SELECIONE O CANDIDATO ---"
        List<Candidato> candidatos = respostaListagem.dados as List<Candidato>
        candidatos.eachWithIndex { cand, i -> println "${i + 1} - ${cand.nome} " }
        print "Opção: "

        int index = lerOpcaoInt(sc) - 1
        if (index < 0 || index >= candidatos.size()) {
            println "Opção inválida."
            return
        }

        menuCandidato(candidatos[index])
    }

    private void menuCandidato(Candidato candidato) {
        Map<Integer, ICommand> acoesCandidato = [
                1: new AtualizarCandidatoCommand(candidatoController, sc, candidato),
                2: new GerenciarCompetenciasCandidatoCommand(candidatoController, sc, candidato),
                3: new CurtirVagaCommand(vagaController, matchController, sc, candidato),
                4: new VerMatchesCandidatoCommand(matchController, candidato),
                5: new ExcluirCandidatoCommand(candidatoController, candidato)
        ] as Map<Integer, ICommand>

        int opcao = 0
        while (opcao != 6) {
            println "\n--- PERFIL: ${candidato.nome} ---"
            println "1 - Atualizar dados"
            println "2 - Gerenciar competências"
            println "3 - Curtir vaga"
            println "4 - Ver matches"
            println "5 - Excluir perfil"
            println "6 - Voltar"
            print "Escolha: "

            opcao = lerOpcaoInt(sc)

            if (opcao == 5) {
                println "Voltando..."
                break
            }

            ICommand comando = acoesCandidato.get(opcao)
            if (comando != null) {
                comando.executar()
                if (opcao == 6) break
            } else {
                println "Opção inválida."
            }
        }
    }

    private static int lerOpcaoInt(Scanner sc) {
        if (!sc.hasNextInt()) {
            sc.nextLine()
            return -1
        }
        int op = sc.nextInt()
        sc.nextLine()
        return op
    }
}