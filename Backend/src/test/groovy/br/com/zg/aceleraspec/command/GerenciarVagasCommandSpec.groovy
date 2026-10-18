package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.GerenciarVagasCommand
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.model.Empresa
import spock.lang.Specification

class GerenciarVagasCommandSpec extends Specification {

    EmpresaController empresaControllerMock
    VagaController vagaControllerMock
    Empresa empresaTeste

    def setup() {
        empresaControllerMock = Mock(EmpresaController)
        vagaControllerMock = Mock(VagaController)
        empresaTeste = new Empresa(nome: "ZG Solucoes", cnpj: "12345678901234")
    }

    def "deve exibir o menu de gerenciamento de vagas e voltar com a opcao 5"() {
        given: "input do usuario selecionando a opcao 5 para voltar"
        String inputUsuario = "5\n"
        def scanner = new Scanner(inputUsuario)

        def comando = new GerenciarVagasCommand(empresaControllerMock, vagaControllerMock, scanner, empresaTeste)

        when: "o comando eh executado"
        comando.executar()

        then: "a execucao finaliza sem erros"
        notThrown(Exception)
    }

    def "deve tratar entrada invalida e permitir sair em seguida"() {
        given: "input do usuario com opcao invalida seguida de 5 para voltar"
        String inputUsuario = "9\n5\n"
        def scanner = new Scanner(inputUsuario)

        def comando = new GerenciarVagasCommand(empresaControllerMock, vagaControllerMock, scanner, empresaTeste)

        when: "o comando eh executado"
        comando.executar()

        then: "a execucao finaliza sem erros"
        notThrown(Exception)
    }
}