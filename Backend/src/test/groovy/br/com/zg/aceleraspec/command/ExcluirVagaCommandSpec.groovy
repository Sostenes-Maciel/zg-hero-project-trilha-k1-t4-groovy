package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.ExcluirVagaCommand
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class ExcluirVagaCommandSpec extends Specification {

    VagaController vagaControllerMock
    Empresa empresaTeste

    def setup() {
        vagaControllerMock = Mock(VagaController)
        empresaTeste = new Empresa(cnpj: "12345678000199", nome: "Empresa Teste")
    }

    def "deve tratar falha do servidor ao listar vagas da empresa"() {
        given:
        Scanner scanner = new Scanner("")
        def comando = new ExcluirVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 500, mensagem: "Erro interno no banco"]
        0 * vagaControllerMock.excluir(_)
        notThrown(Exception)
    }

    def "deve tratar situacao onde a empresa nao possui vagas cadastradas"() {
        given:
        Scanner scanner = new Scanner("")
        def comando = new ExcluirVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: []]
        0 * vagaControllerMock.excluir(_)
        notThrown(Exception)
    }

    def "deve tratar entrada invalida quando id nao for inteiro"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev Java")
        String inputUsuario = "abc\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new ExcluirVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        0 * vagaControllerMock.excluir(_)
        notThrown(Exception)
    }

    def "deve tratar situacao onde o id da vaga nao pertence a empresa"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev Java")
        String inputUsuario = "99\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new ExcluirVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        0 * vagaControllerMock.excluir(_)
        notThrown(Exception)
    }

    def "deve cancelar a exclusao quando usuario responder diferente de s"() {
        given:
        Vaga vaga = new Vaga(id: 5, titulo: "Dev Groovy")
        String inputUsuario = "5\nn\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new ExcluirVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        0 * vagaControllerMock.excluir(_)
        notThrown(Exception)
    }

    def "deve excluir a vaga com sucesso quando usuario confirmar com s"() {
        given:
        Vaga vaga = new Vaga(id: 5, titulo: "Dev Groovy")
        String inputUsuario = "5\ns\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new ExcluirVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        1 * vagaControllerMock.excluir(5) >> [status: 200, mensagem: "Vaga excluída com sucesso!"]
        notThrown(Exception)
    }

    def "deve aceitar s maiusculo na confirmacao e excluir a vaga"() {
        given:
        Vaga vaga = new Vaga(id: 8, titulo: "Dev Frontend")
        String inputUsuario = "8\nS\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new ExcluirVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        1 * vagaControllerMock.excluir(8) >> [status: 200, mensagem: "Vaga excluída com sucesso!"]
        notThrown(Exception)
    }
}