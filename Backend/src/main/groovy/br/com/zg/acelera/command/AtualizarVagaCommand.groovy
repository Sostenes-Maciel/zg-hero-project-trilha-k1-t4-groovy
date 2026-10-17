package br.com.zg.acelera.command

import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class AtualizarVagaCommand implements ICommand {

    private final VagaController vagaController
    private final Scanner sc
    private final Empresa empresa

    AtualizarVagaCommand(VagaController vagaController, Scanner sc, Empresa empresa) {
        this.vagaController = vagaController
        this.sc = sc
        this.empresa = empresa
    }

    @Override
    void executar() {
        print "Digite o ID da vaga que deseja atualizar: "
        int idVaga = lerOpcaoInt(sc)

        if (idVaga == -1) return

        Vaga vaga = new Vaga(id: idVaga, empresa: empresa)
        print "Novo Título da Vaga: "
        vaga.titulo = sc.nextLine().trim()
        print "Novas Competências (separadas por vírgula): "
        vaga.competencia = sc.nextLine().split(",").collect { it.trim() }

        def resposta = vagaController.atualizar(vaga)
        println "\n[${resposta.status}] - ${resposta.mensagem}"
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