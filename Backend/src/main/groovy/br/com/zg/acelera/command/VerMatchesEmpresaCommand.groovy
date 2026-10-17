package br.com.zg.acelera.command

import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa

class VerMatchesEmpresaCommand implements ICommand {

    private final MatchController matchController
    private final Empresa empresa

    VerMatchesEmpresaCommand(MatchController matchController, Empresa empresa) {
        this.matchController = matchController
        this.empresa = empresa
    }

    @Override
    void executar() {
        def respMatches = matchController.listarMatchesEmpresa(empresa.cnpj)
        println "\n--- MEUS MATCHES ---"
        if (respMatches.status == 200 && respMatches.dados) {
            respMatches.dados.each { m -> println "Candidato: ${m.nomeCandidato} | Vaga: ${m.tituloVaga}" }
        } else {
            println "Nenhum match encontrado ainda."
        }
    }
}