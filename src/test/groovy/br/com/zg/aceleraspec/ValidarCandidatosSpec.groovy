package br.com.zg.aceleraspec

import br.com.zg.acelera.service.ValidarCandidato
import spock.lang.Specification

class ValidarCandidatoSpec extends Specification {

    def "deve aceitar um nome válido"() {

        when:
        ValidarCandidato.validarNome("João da Silva")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um nome inválido"() {

        when:
        ValidarCandidato.validarNome("João123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Nome inválido."
    }

    def "deve aceitar um email válido"() {

        when:
        ValidarCandidato.validarEmail("teste@email.com")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um email inválido"() {

        when:
        ValidarCandidato.validarEmail("teste@email")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "E-mail inválido."
    }


    def "deve aceitar um CPF válido"() {

        when:
        ValidarCandidato.validarCpf("123.456.789-00")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um CPF inválido"() {

        when:
        ValidarCandidato.validarCpf("123.456.789")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF deve conter 11 números."
    }

    def "deve aceitar CPF com 11 números"() {

        when:
        ValidarCandidato.validarCpf("12345678900")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar CPF com 10 números"() {

        when:
        ValidarCandidato.validarCpf("1234567890")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF deve conter 11 números."
    }

    def "deve rejeitar CPF com 12 números"() {

        when:
        ValidarCandidato.validarCpf("123456789001")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF deve conter 11 números."
    }


    def "deve aceitar uma idade válida"() {

        when:
        ValidarCandidato.validarIdade(25)

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma idade menor que 18 anos"() {

        when:
        ValidarCandidato.validarIdade(17)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Idade inválida."
    }

    def "deve rejeitar uma idade maior que 120 anos"() {

        when:
        ValidarCandidato.validarIdade(121)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Idade inválida."
    }

    def "deve aceitar a idade mínima de 18 anos"() {

        when:
        ValidarCandidato.validarIdade(18)

        then:
        noExceptionThrown()
    }

    def "deve aceitar a idade máxima de 120 anos"() {

        when:
        ValidarCandidato.validarIdade(120)

        then:
        noExceptionThrown()
    }


    def "deve aceitar um estado válido"() {

        when:
        ValidarCandidato.validarEstado("Pernambuco")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um estado inválido"() {

        when:
        ValidarCandidato.validarEstado("PE123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Estado inválido."
    }


    def "deve aceitar um CEP válido"() {

        when:
        ValidarCandidato.validarCep("55299-300")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um CEP inválido"() {

        when:
        ValidarCandidato.validarCep("55299-30")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CEP deve conter 8 números."
    }

    def "deve aceitar CEP com 8 números"() {

        when:
        ValidarCandidato.validarCep("55299300")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar CEP com 7 números"() {

        when:
        ValidarCandidato.validarCep("5529930")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CEP deve conter 8 números."
    }

    def "deve rejeitar CEP com 9 números"() {

        when:
        ValidarCandidato.validarCep("552993000")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CEP deve conter 8 números."
    }


    def "deve aceitar uma descrição preenchida"() {

        when:
        ValidarCandidato.validarDescricao("Estudante de ADS")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma descrição vazia"() {

        when:
        ValidarCandidato.validarDescricao("")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Descrição não pode estar vazia."
    }


    def "deve aceitar uma lista de competências preenchida"() {

        when:
        ValidarCandidato.validarCompetencias(["Java", "Groovy"])

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma lista de competências vazia"() {

        when:
        ValidarCandidato.validarCompetencias([])

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Informe pelo menos uma competência."
    }

    def "deve rejeitar competências nulas"() {

        when:
        ValidarCandidato.validarCompetencias(null)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Informe pelo menos uma competência."
    }


}