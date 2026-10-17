package br.com.zg.acelera.command

import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class CadastrarVagaCommand implements ICommand {

    private final VagaController vagaController
    private final Scanner sc
    private final Empresa empresa

    CadastrarVagaCommand(VagaController vagaController, Scanner sc, Empresa empresa) {
        this.vagaController = vagaController
        this.sc = sc
        this.empresa = empresa
    }

    @Override
    void executar() {
        Vaga vaga = new Vaga()
        vaga.empresa = empresa
        print "Título da Vaga: "
        vaga.titulo = sc.nextLine().trim()
        print "Competências necessárias (separadas por vírgula): "
        vaga.competencia = sc.nextLine().split(",").collect { it.trim() }

        def resposta = vagaController.cadastrar(vaga)
        println "\n[${resposta.status}] - ${resposta.mensagem}"
    }
}