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

    def "rejeita menor de idade, email sem arroba e senha fora do limite"() {
        when:
            ValidarRecrutadorService.validarDataNascimento(LocalDate.now().minusYears(17))
        then:
        thrown(IllegalArgumentException)

        when:
            ValidarRecrutadorService.validarEmail('invalido')
        then:
        thrown(IllegalArgumentException)

        when:
            ValidarRecrutadorService.validarSenha('12345')
        then:
        thrown(IllegalArgumentException)
    }
}
