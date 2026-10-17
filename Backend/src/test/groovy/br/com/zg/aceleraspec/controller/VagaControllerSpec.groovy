package br.com.zg.aceleraspec.controller

import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.service.GerenciadorDeVagasService
import spock.lang.Specification

class VagaControllerSpec extends Specification {

    GerenciadorDeVagasService serviceMock
    VagaController controller

    def setup() {
        serviceMock = Mock(GerenciadorDeVagasService)
        controller = new VagaController(serviceMock)
    }

    def "deve retornar status 201 ao cadastrar vaga com sucesso"() {
        given:
        Vaga vaga = new Vaga(titulo: "Desenvolvedor")
        serviceMock.cadastrarVaga(vaga) >> 1

        when:
        def resposta = controller.cadastrar(vaga)

        then:
        resposta.status == 201
        resposta.id == 1
    }

    def "deve retornar status 500 ao ocorrer erro no cadastro da vaga"() {
        given:
        Vaga vaga = new Vaga(titulo: "Desenvolvedor")
        serviceMock.cadastrarVaga(vaga) >> { throw new RuntimeException("Erro BD") }

        when:
        def resposta = controller.cadastrar(vaga)

        then:
        resposta.status == 500
    }

    def "deve retornar status 200 e lista de vagas"() {
        given:
        serviceMock.listarVagas() >> [new Vaga(titulo: "Desenvolvedor")]

        when:
        def resposta = controller.listar()

        then:
        resposta.status == 200
        resposta.dados.size() == 1
    }

    def "deve retornar status 200 e lista de vagas da empresa"() {
        given:
        serviceMock.listarVagasDaEmpresa("12345678901234") >> [new Vaga(titulo: "Desenvolvedor")]

        when:
        def resposta = controller.listarDaEmpresa("12345678901234")

        then:
        resposta.status == 200
        resposta.dados.size() == 1
    }

    def "deve retornar status 200 ao atualizar vaga"() {
        given:
        Vaga vaga = new Vaga(titulo: "Desenvolvedor")

        when:
        def resposta = controller.atualizar(vaga)

        then:
        1 * serviceMock.atualizarVaga(vaga)
        resposta.status == 200
    }

    def "deve retornar status 200 ao excluir vaga"() {
        when:
        def resposta = controller.excluir(1)

        then:
        1 * serviceMock.excluirVaga(1)
        resposta.status == 200
    }
}