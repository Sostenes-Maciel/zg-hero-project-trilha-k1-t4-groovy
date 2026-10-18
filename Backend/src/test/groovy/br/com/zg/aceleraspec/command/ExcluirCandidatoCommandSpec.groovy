package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.ExcluirCandidatoCommand
import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.model.Candidato
import spock.lang.Specification

class ExcluirCandidatoCommandSpec extends Specification {

    CandidatoController candidatoControllerMock

    def setup() {
        candidatoControllerMock = Mock(CandidatoController)
    }

    def "deve chamar o controller para excluir o candidato utilizando o cpf"() {
        given:
        Candidato candidato = new Candidato(cpf: "11122233344", nome: "Candidato Teste")
        def comando = new ExcluirCandidatoCommand(candidatoControllerMock, candidato)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.excluir("11122233344") >> [status: 200, mensagem: "Candidato excluído com sucesso!"]
    }

    def "deve lidar com resposta de erro do controller sem interromper execucao"() {
        given:
        Candidato candidato = new Candidato(cpf: "99988877766", nome: "Candidato Inexistente")
        def comando = new ExcluirCandidatoCommand(candidatoControllerMock, candidato)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.excluir("99988877766") >> [status: 404, mensagem: "Candidato não encontrado"]
        notThrown(Exception)
    }
}