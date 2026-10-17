package br.com.zg.acelera.command

import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Vaga
import java.util.Scanner

class AtualizarVagaCommand implements ICommand {

    private final VagaController vagaController
    private final Scanner sc
    private final Vaga vaga

    AtualizarVagaCommand(VagaController vagaController, Scanner sc, Vaga vaga) {
        this.vagaController = vagaController
        this.sc = sc
        this.vaga = vaga
    }

    @Override
    void executar() {
        println "\n--- ATUALIZAR VAGA ---"
        println "Dica: Para manter o valor atual, apenas aperte ENTER vazia."

        print "Título [${vaga.titulo}]: "
        String titulo = sc.nextLine().trim()
        if (!titulo.isEmpty()) vaga.titulo = titulo

        print "Competências [${vaga.competencias?.join(', ')}]: "
        String competencias = sc.nextLine().trim()
        if (!competencias.isEmpty()) {
            vaga.competencias = competencias.split(",").collect { it.trim() }
        }

        def resposta = vagaController.atualizar(vaga)
        println "\n[${resposta.status}] - ${resposta.mensagem}"
    }
}