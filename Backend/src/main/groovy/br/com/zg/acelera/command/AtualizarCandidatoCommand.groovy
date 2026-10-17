package br.com.zg.acelera.command

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Candidato

class AtualizarCandidatoCommand implements ICommand {

    private final CandidatoController candidatoController
    private final Scanner sc
    private final Candidato candidato

    AtualizarCandidatoCommand(CandidatoController candidatoController, Scanner sc, Candidato candidato) {
        this.candidatoController = candidatoController
        this.sc = sc
        this.candidato = candidato
    }

    @Override
    void executar() {
        print "Novo Nome (deixe em branco para manter o atual): "
        String novoNome = sc.nextLine().trim()

        if (!novoNome.isEmpty()) {
            candidato.nome = novoNome
        }

        def resposta = candidatoController.atualizar(candidato)
        println "\n[${resposta.status}] - ${resposta.mensagem}"
    }
}