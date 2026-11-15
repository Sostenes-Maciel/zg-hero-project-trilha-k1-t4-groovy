package br.com.zg.aceleraspec.service

import br.com.zg.acelera.service.ValidarRecrutadorService
import spock.lang.Specification

import java.time.LocalDate

class ValidarRecrutadorSpec extends Specification {

    def "gera e valida hash sem armazenar a senha em texto puro"() {
        given:
        String senha = 'segura1'

        when:
        String hash = ValidarRecrutadorService.gerarHashSenha(senha)

        then:
        hash != senha
        ValidarRecrutadorService.verificarSenha(senha, hash)
        !ValidarRecrutadorService.verificarSenha('errada1', hash)
    }

    def "gera hashes diferentes para a mesma senha por causa do salt"() {
        when:
        String primeiro = ValidarRecrutadorService.gerarHashSenha("segura1")
        String segundo = ValidarRecrutadorService.gerarHashSenha("segura1")

        then:
        primeiro != segundo
        ValidarRecrutadorService.verificarSenha("segura1", primeiro)
        ValidarRecrutadorService.verificarSenha("segura1", segundo)
    }

    def "aceita os limites inclusivos de senha e maioridade"() {
        given:
        LocalDate aniversario = LocalDate.now().minusYears(18)

        when:
        ValidarRecrutadorService.validarSenha("123456")
        ValidarRecrutadorService.validarSenha("12345678")
        ValidarRecrutadorService.validarDataNascimento(aniversario)

        then:
        noExceptionThrown()
    }

    def "rejeita email nulo, vazio ou sem arroba"() {
        when:
        ValidarRecrutadorService.validarEmail(email)

        then:
        thrown(IllegalArgumentException)

        where:
        email << [null, "", "sem-dominio", "a"]
    }

    def "rejeita senhas nulas, curtas e longas"() {
        when:
        ValidarRecrutadorService.validarSenha(senha)

        then:
        thrown(IllegalArgumentException)

        where:
        senha << [null, "", "12345", "123456789"]
    }

    def "rejeita menor de idade e data ausente"() {
        when:
        ValidarRecrutadorService.validarDataNascimento(LocalDate.now().minusYears(17))
        then:
        thrown(IllegalArgumentException)

        when:
        ValidarRecrutadorService.validarDataNascimento(null)
        then:
        thrown(IllegalArgumentException)
    }

    def "retorna falso para hash ausente, corrompido ou senha incorreta"() {
        expect:
        !ValidarRecrutadorService.verificarSenha(null, null)
        !ValidarRecrutadorService.verificarSenha("123456", "hash-invalido")
        !ValidarRecrutadorService.verificarSenha("123456", "abc:not-base64:xyz")
    }
}
