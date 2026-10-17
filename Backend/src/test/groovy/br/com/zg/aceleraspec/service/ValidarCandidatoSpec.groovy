package br.com.zg.aceleraspec.service

import br.com.zg.acelera.service.ValidarCandidatoService
import spock.lang.Specification

class ValidarCandidatoSpec extends Specification {


    def "deve aceitar um nome válido"() {
        when:
        ValidarCandidatoService.validarNome("João da Silva")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um nome inválido"() {
        when:
        ValidarCandidatoService.validarNome("João123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Nome inválido."
    }


    def "deve aceitar um email válido"() {
        when:
        ValidarCandidatoService.validarEmail("teste@email.com")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um email inválido"() {
        when:
        ValidarCandidatoService.validarEmail("teste@email")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "E-mail inválido."
    }


    def "deve aceitar um CPF válido"() {
        when:
        ValidarCandidatoService.validarCpf("123.456.789-00")

        then:
        noExceptionThrown()
    }

    def "deve aceitar CPF com 11 números"() {
        when:
        ValidarCandidatoService.validarCpf("12345678900")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um CPF inválido"() {
        when:
        ValidarCandidatoService.validarCpf("123.456.789")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF deve conter 11 números."
    }

    def "deve rejeitar CPF com 10 números"() {
        when:
        ValidarCandidatoService.validarCpf("1234567890")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF deve conter 11 números."
    }

    def "deve rejeitar CPF com 12 números"() {
        when:
        ValidarCandidatoService.validarCpf("123456789001")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF deve conter 11 números."
    }


    def "deve aceitar uma idade válida"() {
        when:
        ValidarCandidatoService.validarIdade(25)

        then:
        noExceptionThrown()
    }

    def "deve aceitar a idade mínima de 18 anos"() {
        when:
        ValidarCandidatoService.validarIdade(18)

        then:
        noExceptionThrown()
    }

    def "deve aceitar a idade máxima de 120 anos"() {
        when:
        ValidarCandidatoService.validarIdade(120)

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma idade menor que 18 anos"() {
        when:
        ValidarCandidatoService.validarIdade(17)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Idade inválida."
    }

    def "deve rejeitar uma idade maior que 120 anos"() {
        when:
        ValidarCandidatoService.validarIdade(121)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Idade inválida."
    }


    def "deve aceitar um estado válido"() {
        when:
        ValidarCandidatoService.validarEstado("Pernambuco")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um estado inválido"() {
        when:
        ValidarCandidatoService.validarEstado("PE123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Estado inválido."
    }

    def "deve aceitar um CEP valido com ou sem formatacao"() {
        when:
        ValidarCandidatoService.validarCep(cep)

        then:
        noExceptionThrown()

        where: "testa tanto com traco quanto sem traco"
        cep << ["55299-300", "55299300"]
    }

    def "deve rejeitar CEPs com tamanho invalido"() {
        when:
        ValidarCandidatoService.validarCep(cepInvalido)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CEP deve conter 8 números."

        where: "testa CEP faltando numero com traco, sem traco, e sobrando numero"
        cepInvalido << ["55299-30", "5529930", "552993000"]
    }


    def "deve aceitar uma descrição preenchida"() {
        when:
        ValidarCandidatoService.validarDescricao("Estudante de ADS")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma descrição vazia"() {
        when:
        ValidarCandidatoService.validarDescricao("")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Descrição não pode estar vazia."
    }


    def "deve aceitar uma lista de competências preenchida"() {
        when:
        ValidarCandidatoService.validarCompetencias(["Java", "Groovy"])

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma lista de competências vazia"() {
        when:
        ValidarCandidatoService.validarCompetencias([])

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Informe pelo menos uma competência."
    }

    def "deve rejeitar competências nulas"() {
        when:
        ValidarCandidatoService.validarCompetencias(null)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Informe pelo menos uma competência."
    }
}