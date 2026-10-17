package br.com.zg.aceleraspec.service

import br.com.zg.acelera.service.ValidarCandidatoService
import spock.lang.Specification

class ValidarCandidatoSpec extends Specification {

    def "deve aceitar um nome valido"() {
        when:
        ValidarCandidatoService.validarNome("Joao da Silva")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um nome invalido"() {
        when:
        ValidarCandidatoService.validarNome("Joao 123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Nome inválido."
    }

    def "deve aceitar um email valido"() {
        when:
        ValidarCandidatoService.validarEmail("teste@email.com")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um email invalido"() {
        when:
        ValidarCandidatoService.validarEmail("teste@email")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "E-mail inválido."
    }

    def "deve aceitar CPF com ou sem formatacao"() {
        when:
        ValidarCandidatoService.validarCpf(cpf)

        then:
        noExceptionThrown()

        where:
        cpf << ["123.456.789-00", "12345678900"]
    }

    def "deve rejeitar CPF com tamanho invalido"() {
        when:
        ValidarCandidatoService.validarCpf(cpfInvalido)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF deve conter 11 números."

        where:
        cpfInvalido << ["123.456.789", "1234567890", "123456789001"]
    }

    def "deve aceitar idades validas (entre 18 e 120)"() {
        when:
        ValidarCandidatoService.validarIdade(idade)

        then:
        noExceptionThrown()

        where:
        idade << [18, 25, 120]
    }

    def "deve rejeitar idades invalidas"() {
        when:
        ValidarCandidatoService.validarIdade(idade)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Idade inválida."

        where:
        idade << [17, 121]
    }

    def "deve aceitar um estado valido"() {
        when:
        ValidarCandidatoService.validarEstado("Pernambuco")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar um estado invalido"() {
        when:
        ValidarCandidatoService.validarEstado("PE123")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Estado inválido."
    }

    def "deve aceitar CEP com ou sem formatacao"() {
        when:
        ValidarCandidatoService.validarCep(cep)

        then:
        noExceptionThrown()

        where:
        cep << ["55299-300", "55299300"]
    }

    def "deve rejeitar CEPs com tamanho invalido"() {
        when:
        ValidarCandidatoService.validarCep(cepInvalido)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "CEP deve conter 8 números."

        where:
        cepInvalido << ["55299-30", "5529930", "552993000"]
    }

    def "deve aceitar uma descricao preenchida"() {
        when:
        ValidarCandidatoService.validarDescricao("Estudante de ADS")

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma descricao vazia"() {
        when:
        ValidarCandidatoService.validarDescricao("")

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Descrição não pode estar vazia."
    }

    def "deve aceitar uma lista de competencias preenchida"() {
        when:
        ValidarCandidatoService.validarCompetencias(["Java", "Groovy"])

        then:
        noExceptionThrown()
    }

    def "deve rejeitar uma lista de competencias vazia ou nula"() {
        when:
        ValidarCandidatoService.validarCompetencias(competencias)

        then:
        def erro = thrown(IllegalArgumentException)
        erro.message == "Informe pelo menos uma competência."

        where:
        competencias << [[], null]
    }
}