package br.com.zg.aceleraspec.service

import br.com.zg.acelera.service.ValidarEmpresaService
import spock.lang.Specification

class ValidarEmpresaSpec extends Specification {

    def "deve aceitar um nome valido"() {
        when:
        ValidarEmpresaService.validarNome("Tech Corp")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um nome invalido"() {
        when:
        ValidarEmpresaService.validarNome("Tech Corp 123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Nome da empresa inválido."
    }

    def "deve aceitar um email valido"() {
        when:
        ValidarEmpresaService.validarEmail("contato@empresa.com")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um email invalido"() {
        when:
        ValidarEmpresaService.validarEmail("contato@empresa")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "E-mail inválido."
    }

    def "deve aceitar um CNPJ valido com ou sem tracos"() {
        when:
        ValidarEmpresaService.validarCnpj(cnpj)

        then:
        noExceptionThrown()

        where:
        cnpj << ["12.345.678/0001-90", "12345678000190"]
    }

    def "deve rejeitar CNPJ com tamanho invalido"() {
        when:
        ValidarEmpresaService.validarCnpj("1234567800019")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CNPJ deve conter 14 números."
    }

    def "deve aceitar um pais valido"() {
        when:
        ValidarEmpresaService.validarPais("Brasil")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um pais invalido"() {
        when:
        ValidarEmpresaService.validarPais("Brasil123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "País inválido."
    }

    def "deve aceitar um estado valido"() {
        when:
        ValidarEmpresaService.validarEstado("Pernambuco")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um estado invalido"() {
        when:
        ValidarEmpresaService.validarEstado("PE 123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Estado inválido."
    }

    def "deve aceitar um CEP valido com ou sem formatacao"() {
        when:
        ValidarEmpresaService.validarCep(cep)

        then:
        noExceptionThrown()

        where:
        cep << ["55299-300", "55299300"]
    }

    def "deve rejeitar CEPs com tamanho invalido"() {
        when:
        ValidarEmpresaService.validarCep("5529930")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CEP deve conter 8 números."
    }

    def "deve aceitar uma descricao preenchida"() {
        when:
        ValidarEmpresaService.validarDescricao("Empresa de tecnologia")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma descricao vazia"() {
        when:
        ValidarEmpresaService.validarDescricao("")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Descrição não pode estar vazia."
    }

    def "deve aceitar uma lista de competencias preenchida"() {
        when:
        ValidarEmpresaService.validarCompetencias(["Java", "Spring"])

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma lista de competencias vazia ou nula"() {
        when:
        ValidarEmpresaService.validarCompetencias(competencias)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Informe pelo menos uma competência."

        where:
        competencias << [[], null]
    }
}