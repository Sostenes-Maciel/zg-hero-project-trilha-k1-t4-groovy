package br.com.zg.acelera.command

import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class ListarVagasCommand implements ICommand {

    private final VagaController vagaController
    private final Empresa empresa

    ListarVagasCommand(VagaController vagaController, Empresa empresa) {
        this.vagaController = vagaController
        this.empresa = empresa
    }

    @Override
    void executar() {
        def resposta = vagaController.listarDaEmpresa(empresa.cnpj)
        if (resposta.status == 200 && resposta.dados) {
            println "\n--- VAGAS CADASTRADAS ---"
            resposta.dados.each { Vaga v ->
                println "ID: ${v.id} | Título: ${v.titulo} | Competências: ${v.competencia}"
            }
        } else {
            println "Nenhuma vaga encontrada para esta empresa."
        }
    }
}