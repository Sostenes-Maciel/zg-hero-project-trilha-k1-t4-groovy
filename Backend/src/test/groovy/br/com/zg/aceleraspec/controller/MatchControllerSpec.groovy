package br.com.zg.aceleraspec.controller

import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.model.Vaga
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
        Vaga vaga = new Vaga(id: 1)
        perfilServiceMock.curtirVaga("12345678900", vaga) >> true

        when:
        def resposta = controller.curtirVaga("12345678900", vaga)

        then:
        resposta.status == 201
        resposta.match == true
        resposta.mensagem == "MATCH ENCONTRADO!"
    }

    def "deve curtir vaga sem identificar match"() {
        given:
        Vaga vaga = new Vaga(id: 1)
        perfilServiceMock.curtirVaga("12345678900", vaga) >> false

        when:
        def resposta = controller.curtirVaga("12345678900", vaga)

        then:
        resposta.status == 201
        resposta.match == false
    }

    def "deve retornar status 500 ao ocorrer erro ao curtir vaga"() {
        given:
        Vaga vaga = new Vaga(id: 1)
        perfilServiceMock.curtirVaga("12345678900", vaga) >> { throw new RuntimeException("Erro") }

        when:
        def resposta = controller.curtirVaga("12345678900", vaga)

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

    def "deve listar matches do candidato"() {
        given:
        matchServiceMock.listarMatchesDoCandidato("12345678900") >> [[nomeEmpresa: "ZG"]]

        when:
        def resposta = controller.listarMatchesCandidato("12345678900")

        then:
        resposta.status == 200
        resposta.dados.size() == 1
    }

    def "deve listar matches da empresa"() {
        given:
        matchServiceMock.listarMatchesDaEmpresa("12345678901234") >> [[nomeCandidato: "Joao"]]

        when:
        def resposta = controller.listarMatchesEmpresa("12345678901234")

        then:
        resposta.status == 200
        resposta.dados.size() == 1
    }
}