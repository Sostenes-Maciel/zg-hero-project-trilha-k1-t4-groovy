package br.com.zg.aceleraspec.controller

import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.service.GerenciadorDePerfisService
import spock.lang.Specification

class EmpresaControllerSpec extends Specification {

    GerenciadorDePerfisService serviceMock
    EmpresaController controller

    def setup() {
        serviceMock = Mock(GerenciadorDePerfisService)
        controller = new EmpresaController(serviceMock)
    }

    def "deve retornar status 201 ao cadastrar com sucesso"() {
        given:
        Empresa empresa = new Empresa(nome: "Tech")

        when:
        def resposta = controller.cadastrar(empresa)

        then:
        1 * serviceMock.cadastrarEmpresa(empresa)
        resposta.status == 201
    }

    def "deve retornar status 400 ao falhar na validacao do cadastro"() {
        given:
        Empresa empresa = new Empresa()
        serviceMock.cadastrarEmpresa(empresa) >> { throw new IllegalArgumentException("Erro") }

        when:
        def resposta = controller.cadastrar(empresa)

        then:
        resposta.status == 400
    }

    def "deve retornar status 200 e a lista de empresas"() {
        given:
        serviceMock.listarEmpresas() >> [new Empresa(nome: "Tech")]

        when:
        def resposta = controller.listar()

        then:
        resposta.status == 200
        resposta.dados.size() == 1
    }

    def "deve retornar status 200 ao atualizar com sucesso"() {
        given:
        Empresa empresa = new Empresa(nome: "Tech")

        when:
        def resposta = controller.atualizar(empresa)

        then:
        1 * serviceMock.atualizarEmpresa(empresa)
        resposta.status == 200
    }

    def "deve retornar status 200 ao excluir com sucesso"() {
        when:
        def resposta = controller.excluir("123")

        then:
        1 * serviceMock.excluirEmpresa("123")
        resposta.status == 200
    }
}