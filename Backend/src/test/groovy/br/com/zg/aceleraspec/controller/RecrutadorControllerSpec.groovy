package br.com.zg.aceleraspec.controller

import br.com.zg.acelera.controller.RecrutadorController
import br.com.zg.acelera.model.Recrutador
import br.com.zg.acelera.service.GerenciadorDeRecrutadoresService
import spock.lang.Specification

class RecrutadorControllerSpec extends Specification {

    def service = Mock(GerenciadorDeRecrutadoresService)
    def controller = new RecrutadorController(service)

    def "retorna 201 ao cadastrar recrutador"() {
        given:
        def recrutador = new Recrutador(id: 7)

        when:
        def resposta = controller.cadastrar(recrutador, "123")

        then:
        1 * service.cadastrar(recrutador, "123")
        resposta.status == 201
    }

    def "retorna 401 para credenciais inválidas"() {
        given:
        service.autenticar("a@b.com", "123456") >> null

        expect:
        controller.login("a@b.com", "123456").status == 401
    }

    def "retorna 403 quando serviço rejeita operação protegida"() {
        given:
        service.cadastrar(_, _) >> { throw new SecurityException("Permissão insuficiente.") }

        expect:
        controller.cadastrar(new Recrutador(), "123").status == 403
    }
}
