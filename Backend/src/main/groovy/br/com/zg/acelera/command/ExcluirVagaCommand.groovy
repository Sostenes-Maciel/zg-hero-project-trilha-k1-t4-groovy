package br.com.zg.acelera.command

import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand

class ExcluirVagaCommand implements ICommand {

    private final VagaController vagaController
    private final Scanner sc

    ExcluirVagaCommand(VagaController vagaController, Scanner sc) {
        this.vagaController = vagaController
        this.sc = sc
    }

    @Override
    void executar() {
        print "Digite o ID da vaga que deseja excluir: "
        int idVaga = lerOpcaoInt(sc)

        if (idVaga == -1) return

        def resposta = vagaController.excluir(idVaga)
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