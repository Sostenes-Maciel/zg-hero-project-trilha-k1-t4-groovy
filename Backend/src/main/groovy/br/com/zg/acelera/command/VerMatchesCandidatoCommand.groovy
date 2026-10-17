package br.com.zg.acelera.command

import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Candidato

class VerMatchesCandidatoCommand implements ICommand {

    private final MatchController matchController
    private final Candidato candidato

    VerMatchesCandidatoCommand(MatchController matchController, Candidato candidato) {
        this.matchController = matchController
        this.candidato = candidato
    }

    @Override
    void executar() {
        def respMatches = matchController.listarMatchesCandidato(candidato.cpf)
        println "\n--- MEUS MATCHES ---"
        if (respMatches.status == 200 && respMatches.dados) {
            respMatches.dados.each { m -> println "Vaga: ${m.tituloVaga} | Empresa: ${m.nomeEmpresa}" }
        } else {
            println "Nenhum match encontrado ainda."
        }
    }
}