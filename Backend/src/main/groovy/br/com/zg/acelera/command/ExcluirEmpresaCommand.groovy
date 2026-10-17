package br.com.zg.acelera.command

import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa

class ExcluirEmpresaCommand implements ICommand {

    private final EmpresaController empresaController
    private final Empresa empresa

    ExcluirEmpresaCommand(EmpresaController empresaController, Empresa empresa) {
        this.empresaController = empresaController
        this.empresa = empresa
    }

    @Override
    void executar() {
        def respExcluir = empresaController.excluir(empresa.cnpj)
        println "\n[${respExcluir.status}] - ${respExcluir.mensagem}"
    }
}