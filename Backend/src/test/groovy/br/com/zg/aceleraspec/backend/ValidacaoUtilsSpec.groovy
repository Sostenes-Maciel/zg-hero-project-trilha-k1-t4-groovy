package br.com.zg.aceleraspec.backend

import spock.lang.Specification
import br.com.zg.acelera.utils.ValidacaoUtils

class ValidacaoUtilsSpec extends Specification {

    def "deve validar corretamente uma string numerica com o tamanho exato"() {
        when: "uma string valida eh passada"
        ValidacaoUtils.validarTamanhoNumerico("12345678901", 11, "CPF")

        then: "nenhuma excecao deve ser lancada"
        noExceptionThrown()
    }

    def "deve limpar pontos e tracos antes de contar o tamanho"() {
        when: "uma string formatada eh passada"
        ValidacaoUtils.validarTamanhoNumerico("12.345.678/0001-90", 14, "CNPJ")
        ValidacaoUtils.validarTamanhoNumerico("123.456.789-01", 11, "CPF")

        then: "a validacao ocorre com sucesso sem excecoes"
        noExceptionThrown()
    }

    def "deve lancar IllegalArgumentException quando o tamanho for menor que o exigido"() {
        when: "uma string incompleta eh passada"
        ValidacaoUtils.validarTamanhoNumerico("12345", 11, "CPF")

        then: "uma excecao com o nome do campo eh lancada"
        def exception = thrown(IllegalArgumentException)
        exception.message == "CPF deve conter 11 números."
    }

    def "deve lancar IllegalArgumentException quando o tamanho for maior que o exigido"() {
        when: "uma string com caracteres sobrando eh passada"
        ValidacaoUtils.validarTamanhoNumerico("12345678", 8, "CEP")
        ValidacaoUtils.validarTamanhoNumerico("123456789", 8, "CEP") // 9 digitos

        then:
        thrown(IllegalArgumentException)
    }
}