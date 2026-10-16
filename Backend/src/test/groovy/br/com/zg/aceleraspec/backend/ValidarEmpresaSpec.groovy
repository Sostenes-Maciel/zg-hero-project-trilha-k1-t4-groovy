package br.com.zg.aceleraspec.backend

import br.com.zg.acelera.service.ValidarEmpresaService
import spock.lang.Specification


class ValidarEmpresaSpec extends Specification {

    def "deve aceitar um nome de empresa válido"() {

        when:
        ValidarEmpresaService.validarNome("Tech Global")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um nome de empresa inválido"() {

        when:
        ValidarEmpresaService.validarNome("Tech123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Nome da empresa inválido."
    }


    def "deve aceitar um email válido"() {

        when:
        ValidarEmpresaService.validarEmail("empresa@email.com")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um email inválido"() {

        when:
        ValidarEmpresaService.validarEmail("empresa@email")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "E-mail inválido."
    }


    def "deve aceitar CNPJ com 14 números"() {

        when:
        ValidarEmpresaService.validarCnpj("12345678000199")

        then:
        noExceptionThrown()
    }

    def "deve aceitar CNPJ formatado"() {

        when:
        ValidarEmpresaService.validarCnpj("12.345.678/0001-99")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar CNPJ com 13 números"() {

        when:
        ValidarEmpresaService.validarCnpj("1234567800019")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CNPJ deve conter 14 números."
    }

    def "deve rejeitar CNPJ com 15 números"() {

        when:
        ValidarEmpresaService.validarCnpj("123456780001999")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CNPJ deve conter 14 números."
    }


    def "deve aceitar um país válido"() {

        when:
        ValidarEmpresaService.validarPais("Brasil")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um país inválido"() {

        when:
        ValidarEmpresaService.validarPais("Brasil123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "País inválido."
    }


    def "deve aceitar um estado válido"() {

        when:
        ValidarEmpresaService.validarEstado("Pernambuco")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um estado inválido"() {

        when:
        ValidarEmpresaService.validarEstado("PE123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Estado inválido."
    }


    def "deve aceitar CEP com 8 números"() {

        when:
        ValidarEmpresaService.validarCep("55299300")

        then:
        noExceptionThrown()
    }

    def "deve aceitar CEP formatado"() {

        when:
        ValidarEmpresaService.validarCep("55299-300")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar CEP com 7 números"() {

        when:
        ValidarEmpresaService.validarCep("5529930")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CEP deve conter 8 números."
    }

    def "deve rejeitar CEP com 9 números"() {

        when:
        ValidarEmpresaService.validarCep("552993000")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CEP deve conter 8 números."
    }


    def "deve aceitar uma descrição preenchida"() {

        when:
        ValidarEmpresaService.validarDescricao("Consultoria em tecnologia")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma descrição vazia"() {

        when:
        ValidarEmpresaService.validarDescricao("")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Descrição não pode estar vazia."
    }


    def "deve aceitar uma lista de competências preenchida"() {

        when:
        ValidarEmpresaService.validarCompetencias(["Java", "Groovy"])

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma lista de competências vazia"() {

        when:
        ValidarEmpresaService.validarCompetencias([])

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Informe pelo menos uma competência."
    }

    def "deve rejeitar competências nulas"() {

        when:
        ValidarEmpresaService.validarCompetencias(null)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Informe pelo menos uma competência."
    }
}