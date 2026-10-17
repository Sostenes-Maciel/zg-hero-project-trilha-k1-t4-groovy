package br.com.zg.aceleraspec.controller

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.service.GerenciadorDePerfisService
import spock.lang.Specification

class CandidatoControllerSpec extends Specification {

    GerenciadorDePerfisService serviceMock
    CandidatoController controller

    def setup() {
        serviceMock = Mock(GerenciadorDePerfisService)
        controller = new CandidatoController(serviceMock)
    }

    def "deve retornar status 201 ao cadastrar com sucesso"() {
        given:
        Candidato candidato = new Candidato(nome: "Joao")

        when:
        def resposta = controller.cadastrar(candidato)

        then:
        1 * serviceMock.cadastrarCandidato(candidato)
        resposta.status == 201
    }

    def "deve retornar status 400 ao falhar na validacao do cadastro"() {
        given:
        Candidato candidato = new Candidato()
        serviceMock.cadastrarCandidato(candidato) >> { throw new IllegalArgumentException("Erro") }

        when:
        def resposta = controller.cadastrar(candidato)

        then:
        resposta.status == 400
    }

    def "deve retornar status 500 ao ocorrer erro interno no cadastro"() {
        given:
        Candidato candidato = new Candidato()
        serviceMock.cadastrarCandidato(candidato) >> { throw new RuntimeException("Falha BD") }

        when:
        def resposta = controller.cadastrar(candidato)

        then:
        resposta.status == 500
    }

    def "deve retornar status 200 e a lista de candidatos"() {
        given:
        serviceMock.listarCandidatos() >> [new Candidato(nome: "Joao")]

        when:
        def resposta = controller.listar()

        then:
        resposta.status == 200
        resposta.dados.size() == 1
    }

    def "deve retornar status 200 ao atualizar com sucesso"() {
        given:
        Candidato candidato = new Candidato(nome: "Joao")

        when:
        def resposta = controller.atualizar(candidato)

        then:
        1 * serviceMock.atualizarCandidato(candidato)
        resposta.status == 200
    }

    def "deve retornar status 200 ao excluir com sucesso"() {
        when:
        def resposta = controller.excluir("12345678900")

        then:
        1 * serviceMock.excluirCandidato("12345678900")
        resposta.status == 200
    }
}