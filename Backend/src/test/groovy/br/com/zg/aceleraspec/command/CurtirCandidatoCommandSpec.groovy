package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.CurtirCandidatoCommand
import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class CurtirCandidatoCommandSpec extends Specification {

    CandidatoController candidatoControllerMock
    VagaController vagaControllerMock
    MatchController matchControllerMock
    Empresa empresaTeste

    def setup() {
        candidatoControllerMock = Mock(CandidatoController)
        vagaControllerMock = Mock(VagaController)
        matchControllerMock = Mock(MatchController)
        empresaTeste = new Empresa(cnpj: "12345678000199", nome: "Empresa Teste")
    }

    def "deve encerrar a execucao caso nao existam candidatos disponiveis"() {
        given:
        Scanner scanner = new Scanner("")
        def comando = new CurtirCandidatoCommand(candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.listar() >> [status: 200, dados: []]
        0 * vagaControllerMock.listarDaEmpresa(_)
        0 * matchControllerMock.curtirCandidato(_, _, _)
        notThrown(Exception)
    }

    def "deve cancelar a operacao caso a opcao digitada para candidato seja 0"() {
        given:
        Candidato candidato = new Candidato(cpf: "11122233344", nome: "Candidato 1")
        String inputUsuario = "0\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirCandidatoCommand(candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.listar() >> [status: 200, dados: [candidato]]
        0 * vagaControllerMock.listarDaEmpresa(_)
        0 * matchControllerMock.curtirCandidato(_, _, _)
        notThrown(Exception)
    }

    def "deve tratar entrada invalida nao numerica para selecao de candidato"() {
        given:
        Candidato candidato = new Candidato(cpf: "11122233344", nome: "Candidato 1")
        String inputUsuario = "abc\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirCandidatoCommand(candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.listar() >> [status: 200, dados: [candidato]]
        0 * vagaControllerMock.listarDaEmpresa(_)
        0 * matchControllerMock.curtirCandidato(_, _, _)
        notThrown(Exception)
    }

    def "deve tratar selecao de numero de candidato inexistente"() {
        given:
        Candidato candidato = new Candidato(cpf: "11122233344", nome: "Candidato 1")
        String inputUsuario = "9\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirCandidatoCommand(candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.listar() >> [status: 200, dados: [candidato]]
        0 * vagaControllerMock.listarDaEmpresa(_)
        0 * matchControllerMock.curtirCandidato(_, _, _)
        notThrown(Exception)
    }

    def "deve tratar situacao onde a empresa nao possui vagas cadastradas para curtir"() {
        given:
        Candidato candidato = new Candidato(cpf: "11122233344", nome: "Candidato 1")
        String inputUsuario = "1\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirCandidatoCommand(candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.listar() >> [status: 200, dados: [candidato]]
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: []]
        0 * matchControllerMock.curtirCandidato(_, _, _)
        notThrown(Exception)
    }

    def "deve cancelar a operacao caso a opcao digitada para vaga seja 0"() {
        given:
        Candidato candidato = new Candidato(cpf: "11122233344", nome: "Candidato 1")
        Vaga vaga = new Vaga(id: 10, titulo: "Dev Backend")
        String inputUsuario = "1\n0\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirCandidatoCommand(candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.listar() >> [status: 200, dados: [candidato]]
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        0 * matchControllerMock.curtirCandidato(_, _, _)
        notThrown(Exception)
    }

    def "deve selecionar candidato e vaga validos e registrar curtida com sucesso"() {
        given:
        Candidato candidato = new Candidato(cpf: "11122233344", nome: "Candidato 1", estado: "PE", competencias: ["Java"], descricao: "Dev")
        Vaga vaga = new Vaga(id: 10, titulo: "Dev Backend")
        String inputUsuario = "1\n1\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirCandidatoCommand(candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.listar() >> [status: 200, dados: [candidato]]
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        1 * matchControllerMock.curtirCandidato(empresaTeste.cnpj, "11122233344", 10) >> [status: 200, mensagem: "Curtida registrada!"]
        notThrown(Exception)
    }
}