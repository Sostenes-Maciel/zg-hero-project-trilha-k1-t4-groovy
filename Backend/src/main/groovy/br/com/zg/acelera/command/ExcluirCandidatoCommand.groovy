package br.com.zg.acelera.command

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Candidato

class ExcluirCandidatoCommand implements ICommand {

    private final CandidatoController candidatoController
    private final Candidato candidato

    ExcluirCandidatoCommand(CandidatoController candidatoController, Candidato candidato) {
        this.candidatoController = candidatoController
        this.candidato = candidato
    }

    @Override
    void executar() {
        def respExcluir = candidatoController.excluir(candidato.cpf)
        println "\n[${respExcluir.status}] - ${respExcluir.mensagem}"
    }
}