package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.VerMatchesEmpresaCommand
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.model.Empresa
import spock.lang.Specification

class VerMatchesEmpresaCommandSpec extends Specification {

    MatchController matchControllerMock
    Empresa empresaTeste

    def setup() {
        matchControllerMock = Mock(MatchController)
        empresaTeste = new Empresa(cnpj: "12345678000199", nome: "Empresa Teste")
    }

    def "deve listar os matches da empresa com sucesso quando status for 200 e houver dados"() {
        given:
        def match1 = [nomeCandidato: "João Silva", tituloVaga: "Dev Backend Java"]
        def match2 = [nomeCandidato: "Maria Souza", tituloVaga: "Dev Groovy"]
        def comando = new VerMatchesEmpresaCommand(matchControllerMock, empresaTeste)

        when:
        comando.executar()

        then:
        1 * matchControllerMock.listarMatchesEmpresa(empresaTeste.cnpj) >> [
                status: 200,
                dados: [match1, match2]
        ]
        notThrown(Exception)
    }

    def "deve exibir mensagem padrao quando a empresa nao possuir matches cadastrados"() {
        given:
        def comando = new VerMatchesEmpresaCommand(matchControllerMock, empresaTeste)

        when:
        comando.executar()

        then:
        1 * matchControllerMock.listarMatchesEmpresa(empresaTeste.cnpj) >> [
                status: 200,
                dados: []
        ]
        notThrown(Exception)
    }

    def "deve exibir mensagem padrao quando os dados retornados forem nulos"() {
        given:
        def comando = new VerMatchesEmpresaCommand(matchControllerMock, empresaTeste)

        when:
        comando.executar()

        then:
        1 * matchControllerMock.listarMatchesEmpresa(empresaTeste.cnpj) >> [
                status: 200,
                dados: null
        ]
        notThrown(Exception)
    }

    def "deve tratar status de erro retornado pelo controller sem interromper execucao"() {
        given:
        def comando = new VerMatchesEmpresaCommand(matchControllerMock, empresaTeste)

        when:
        comando.executar()

        then:
        1 * matchControllerMock.listarMatchesEmpresa(empresaTeste.cnpj) >> [
                status: 500,
                mensagem: "Erro ao consultar matches no banco"
        ]
        notThrown(Exception)
    }
}