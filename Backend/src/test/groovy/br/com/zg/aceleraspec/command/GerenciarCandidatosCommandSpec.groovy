package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.GerenciarCandidatosCommand
import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.model.Candidato
import spock.lang.Specification

class GerenciarCandidatosCommandSpec extends Specification {

    CandidatoController candidatoControllerMock
    VagaController vagaControllerMock
    MatchController matchControllerMock

    def setup() {
        candidatoControllerMock = Mock(CandidatoController)
        vagaControllerMock = Mock(VagaController)
        matchControllerMock = Mock(MatchController)
    }

    def "deve listar candidatos, selecionar o primeiro e voltar do menu com sucesso"() {
        given: "um candidato cadastrado, input escolhendo o candidato (1) e depois escolhendo voltar (5)"
        def candidato = new Candidato(nome: "Maria", cpf: "12345678901")
        String inputUsuario = "1\n5\n"
        def scanner = new Scanner(inputUsuario)

        def comando = new GerenciarCandidatosCommand(candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "o controller lista os candidatos"
        1 * candidatoControllerMock.listar() >> [status: 200, dados: [candidato]]
    }

    def "deve encerrar a execucao caso nao exista nenhum candidato cadastrado"() {
        given: "uma listagem vazia de candidatos"
        String inputUsuario = "1\n"
        def scanner = new Scanner(inputUsuario)

        def comando = new GerenciarCandidatosCommand(candidatoControllerMock, vagaControllerMock, matchControllerMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "o controller tenta listar mas retorna vazio"
        1 * candidatoControllerMock.listar() >> [status: 200, dados: []]
    }
}