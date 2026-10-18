package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.VerMatchesCandidatoCommand
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.model.Candidato
import spock.lang.Specification

class VerMatchesCandidatoCommandSpec extends Specification {

    MatchController matchControllerMock
    Candidato candidatoTeste

    def setup() {
        matchControllerMock = Mock(MatchController)
        candidatoTeste = new Candidato(cpf: "11122233344", nome: "Candidato Teste")
    }

    def "deve listar os matches do candidato com sucesso quando status for 200 e houver dados"() {
        given:
        def match1 = [tituloVaga: "Dev Backend Java", nomeEmpresa: "Empresa Alpha"]
        def match2 = [tituloVaga: "Dev Groovy", nomeEmpresa: "ZG Solucoes"]
        def comando = new VerMatchesCandidatoCommand(matchControllerMock, candidatoTeste)

        when:
        comando.executar()

        then:
        1 * matchControllerMock.listarMatchesCandidato(candidatoTeste.cpf) >> [
                status: 200,
                dados: [match1, match2]
        ]
        notThrown(Exception)
    }

    def "deve exibir mensagem padrao quando o candidato nao possuir matches cadastrados"() {
        given:
        def comando = new VerMatchesCandidatoCommand(matchControllerMock, candidatoTeste)

        when:
        comando.executar()

        then:
        1 * matchControllerMock.listarMatchesCandidato(candidatoTeste.cpf) >> [
                status: 200,
                dados: []
        ]
        notThrown(Exception)
    }

    def "deve exibir mensagem padrao quando os dados retornados forem nulos"() {
        given:
        def comando = new VerMatchesCandidatoCommand(matchControllerMock, candidatoTeste)

        when:
        comando.executar()

        then:
        1 * matchControllerMock.listarMatchesCandidato(candidatoTeste.cpf) >> [
                status: 200,
                dados: null
        ]
        notThrown(Exception)
    }

    def "deve tratar status de erro retornado pelo controller sem interromper execucao"() {
        given:
        def comando = new VerMatchesCandidatoCommand(matchControllerMock, candidatoTeste)

        when:
        comando.executar()

        then:
        1 * matchControllerMock.listarMatchesCandidato(candidatoTeste.cpf) >> [
                status: 500,
                mensagem: "Erro interno no banco de dados"
        ]
        notThrown(Exception)
    }
}