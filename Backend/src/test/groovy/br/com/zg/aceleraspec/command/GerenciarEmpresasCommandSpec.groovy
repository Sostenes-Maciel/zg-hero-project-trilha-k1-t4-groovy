package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.GerenciarEmpresasCommand
import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.model.Empresa
import spock.lang.Specification

class GerenciarEmpresasCommandSpec extends Specification {

    EmpresaController empresaControllerMock
    CandidatoController candidatoControllerMock
    VagaController vagaControllerMock
    MatchController matchControllerMock

    def setup() {
        empresaControllerMock = Mock(EmpresaController)
        candidatoControllerMock = Mock(CandidatoController)
        vagaControllerMock = Mock(VagaController)
        matchControllerMock = Mock(MatchController)
    }

    def "deve listar empresas, selecionar a primeira e voltar do menu com sucesso"() {
        given: "uma empresa cadastrada, input escolhendo a empresa (1) e depois escolhendo voltar (5)"
        def empresa = new Empresa(nome: "ZG Solucoes", cnpj: "12345678901234")
        String inputUsuario = "1\n5\n"
        def scanner = new Scanner(inputUsuario)

        def comando = new GerenciarEmpresasCommand(empresaControllerMock, candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "o controller lista as empresas"
        1 * empresaControllerMock.listar() >> [status: 200, dados: [empresa]]
    }

    def "deve encerrar a execucao caso nao exista nenhuma empresa cadastrada"() {
        given: "uma listagem vazia de empresas"
        String inputUsuario = "1\n"
        def scanner = new Scanner(inputUsuario)

        def comando = new GerenciarEmpresasCommand(empresaControllerMock, candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "o controller tenta listar mas retorna vazio"
        1 * empresaControllerMock.listar() >> [status: 200, dados: []]
    }
}