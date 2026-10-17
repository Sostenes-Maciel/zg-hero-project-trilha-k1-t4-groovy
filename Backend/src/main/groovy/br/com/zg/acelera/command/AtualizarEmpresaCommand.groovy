package br.com.zg.acelera.command

import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa

class AtualizarEmpresaCommand implements ICommand {

    private final EmpresaController empresaController
    private final Scanner sc
    private final Empresa empresa

    AtualizarEmpresaCommand(EmpresaController empresaController, Scanner sc, Empresa empresa) {
        this.empresaController = empresaController
        this.sc = sc
        this.empresa = empresa
    }

    @Override
    void executar() {
        print "Novo Nome da Empresa (deixe em branco para manter o atual): "
        String novoNome = sc.nextLine().trim()

        if (!novoNome.isEmpty()) {
            empresa.nome = novoNome
        }

        def resposta = empresaController.atualizar(empresa)
        println "\n[${resposta.status}] - ${resposta.mensagem}"
    }
}