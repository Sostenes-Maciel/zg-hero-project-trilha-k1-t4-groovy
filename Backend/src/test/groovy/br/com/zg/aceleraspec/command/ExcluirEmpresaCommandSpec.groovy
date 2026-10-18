package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.ExcluirEmpresaCommand
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.model.Empresa
import spock.lang.Specification

class ExcluirEmpresaCommandSpec extends Specification {

    EmpresaController empresaControllerMock

    def setup() {
        empresaControllerMock = Mock(EmpresaController)
    }

    def "deve chamar o controller para excluir a empresa utilizando o cnpj"() {
        given:
        Empresa empresa = new Empresa(cnpj: "12345678000199", nome: "Empresa Teste")
        def comando = new ExcluirEmpresaCommand(empresaControllerMock, empresa)

        when:
        comando.executar()

        then:
        1 * empresaControllerMock.excluir("12345678000199") >> [status: 200, mensagem: "Empresa excluída com sucesso!"]
    }

    def "deve lidar com resposta de erro do controller sem interromper execucao"() {
        given:
        Empresa empresa = new Empresa(cnpj: "99888777000166", nome: "Empresa Inexistente")
        def comando = new ExcluirEmpresaCommand(empresaControllerMock, empresa)

        when:
        comando.executar()

        then:
        1 * empresaControllerMock.excluir("99888777000166") >> [status: 404, mensagem: "Empresa não encontrada"]
        notThrown(Exception)
    }
}