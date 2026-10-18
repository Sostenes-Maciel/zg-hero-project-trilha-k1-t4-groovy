package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.ListarVagasCommand
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class ListarVagasCommandSpec extends Specification {

    VagaController vagaControllerMock
    Empresa empresaTeste

    def setup() {
        vagaControllerMock = Mock(VagaController)
        empresaTeste = new Empresa(cnpj: "12345678000199", nome: "Empresa Teste")
    }

    def "deve listar as vagas cadastradas da empresa com sucesso quando status for 200 e houver dados"() {
        given:
        Vaga vaga1 = new Vaga(id: 1, titulo: "Dev Java", competencia: ["Java", "Spring"])
        Vaga vaga2 = new Vaga(id: 2, titulo: "Dev Groovy", competencia: ["Groovy", "Spock"])
        def comando = new ListarVagasCommand(vagaControllerMock, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [
                status: 200,
                dados: [vaga1, vaga2]
        ]
        notThrown(Exception)
    }

    def "deve exibir mensagem padrao quando a empresa nao possuir vagas cadastradas"() {
        given:
        def comando = new ListarVagasCommand(vagaControllerMock, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [
                status: 200,
                dados: []
        ]
        notThrown(Exception)
    }

    def "deve exibir mensagem padrao quando os dados retornados forem nulos"() {
        given:
        def comando = new ListarVagasCommand(vagaControllerMock, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [
                status: 200,
                dados: null
        ]
        notThrown(Exception)
    }

    def "deve lidar com status de erro do controller sem interromper execucao"() {
        given:
        def comando = new ListarVagasCommand(vagaControllerMock, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [
                status: 500,
                mensagem: "Erro ao consultar vagas no banco"
        ]
        notThrown(Exception)
    }
}