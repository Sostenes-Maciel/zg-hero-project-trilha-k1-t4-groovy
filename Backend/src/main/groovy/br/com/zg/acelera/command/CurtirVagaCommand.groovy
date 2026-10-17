package br.com.zg.acelera.command

import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Vaga

class CurtirVagaCommand implements ICommand {

    private final VagaController vagaController
    private final MatchController matchController
    private final Scanner sc
    private final Candidato candidato

    CurtirVagaCommand(VagaController vagaController, MatchController matchController, Scanner sc, Candidato candidato) {
        this.vagaController = vagaController
        this.matchController = matchController
        this.sc = sc
        this.candidato = candidato
    }

    @Override
    void executar() {
        def respVagas = vagaController.listar()
        if (respVagas.status != 200 || !respVagas.dados) {
            println "Nenhuma vaga disponível no momento."
            return
        }

        println "\n--- VAGAS DISPONÍVEIS ---"
        List<Vaga> vagas = respVagas.dados as List<Vaga>
        vagas.eachWithIndex { v, i -> println "${i + 1} - ${v.titulo} (Empresa ID: ${v.empresa?.id})" }
        print "Selecione o número da vaga para curtir (ou 0 para cancelar): "

        int idx = lerOpcaoInt(sc) - 1
        if (idx == -1) return
        if (idx >= 0 && idx < vagas.size()) {
            def respostaMatch = matchController.curtirVaga(candidato.cpf, vagas[idx])
            println "\n[${respostaMatch.status}] - ${respostaMatch.mensagem}"
        } else {
            println "Opção inválida."
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