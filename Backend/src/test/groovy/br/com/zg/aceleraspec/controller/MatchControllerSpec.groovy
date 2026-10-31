package br.com.zg.aceleraspec.controller

import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.service.GerenciadorDeMatchesService
import br.com.zg.acelera.service.GerenciadorDePerfisService
import spock.lang.Specification

class MatchControllerSpec extends Specification {

    GerenciadorDeMatchesService matchServiceMock
    GerenciadorDePerfisService perfilServiceMock
    MatchController controller

    def setup() {
        matchServiceMock = Mock(GerenciadorDeMatchesService)
        perfilServiceMock = Mock(GerenciadorDePerfisService)
        controller = new MatchController(matchServiceMock, perfilServiceMock)
    }

    def "deve curtir vaga e identificar match"() {
        given:
        perfilServiceMock.curtirVaga("12345678900", 1) >> true

        when:
        def resposta = controller.curtirVaga("12345678900", 1)

        then:
        resposta.status == 201
        resposta.match == true
        resposta.mensagem == "MATCH ENCONTRADO!"
    }

    def "deve curtir vaga sem identificar match"() {
        given:
        perfilServiceMock.curtirVaga("12345678900", 1) >> false

        when:
        def resposta = controller.curtirVaga("12345678900", 1)

        then:
        resposta.status == 201
        resposta.match == false
    }

    def "deve retornar status 500 ao ocorrer erro ao curtir vaga"() {
        given:
        perfilServiceMock.curtirVaga("12345678900", 1) >> { throw new RuntimeException("Erro") }

        when:
        def resposta = controller.curtirVaga("12345678900", 1)

        then:
        resposta.status == 500
    }

    def "deve curtir candidato e identificar match"() {
        given:
        perfilServiceMock.curtirCandidato("12345678901234", "12345678900", 1) >> true

        when:
        def resposta = controller.curtirCandidato("12345678901234", "12345678900", 1)

        then:
        resposta.status == 201
        resposta.match == true
    }

    def "deve rejeitar curtida empresarial sem vaga selecionada"() {
        given:
        perfilServiceMock.curtirCandidato("12345678901234", "12345678900", null) >>
                { throw new IllegalArgumentException("Selecione uma vaga para curtir o candidato.") }

        when:
        def resposta = controller.curtirCandidato("12345678901234", "12345678900", null)

        then:
        resposta.status == 400
        resposta.mensagem.contains("vaga")
    }

    def "deve remover curtida da empresa apenas quando o service permitir"() {
        given:
        perfilServiceMock.removerCurtidaEmpresa("12345678901234", "12345678900", 1) >> false

        when:
        def resposta = controller.removerCurtidaEmpresa("12345678901234", "12345678900", 1)

        then:
        resposta.status == 400
        resposta.mensagem.contains("Match")
    }

    def "deve listar matches do candidato"() {
        given:
        perfilServiceMock.listarStatusCurtidasCandidato("12345678900") >> [[idVaga: 1, status: "curtido"]]

        when:
        def resposta = controller.listarMatchesCandidato("12345678900")

        then:
        resposta.status == 200
        resposta.dados.size() == 1
    }

    def "deve listar matches da empresa"() {
        given:
        perfilServiceMock.listarStatusCurtidasEmpresa("12345678901234") >> [[cpfCandidato: "12345678900", idVaga: 1, status: "match"]]

        when:
        def resposta = controller.listarMatchesEmpresa("12345678901234")

        then:
        resposta.status == 200
        resposta.dados.size() == 1
    }
}