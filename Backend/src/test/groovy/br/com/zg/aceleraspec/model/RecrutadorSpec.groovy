package br.com.zg.aceleraspec.model

import br.com.zg.acelera.model.Recrutador
import spock.lang.Specification

import java.time.LocalDate

class RecrutadorSpec extends Specification {

    def "deve instanciar e mapear todos os campos"() {
        when:
        Recrutador recrutador = new Recrutador(
                id: 7,
                empresaId: "98.765.432/0001-11",
                nome: "Recrutador Teste",
                areaAtuacao: "TI",
                nivelPermissao: "Total",
                descricao: "Seleção técnica",
                email: "recrutador@teste.com",
                cpf: "12345678900",
                dataNascimento: LocalDate.of(1990, 5, 20),
                senhaHash: "hash-seguro",
                ativo: true
        )

        then:
        recrutador.id == 7
        recrutador.empresaId == "98.765.432/0001-11"
        recrutador.areaAtuacao == "TI"
        recrutador.nivelPermissao == "Total"
        recrutador.dataNascimento == LocalDate.of(1990, 5, 20)
        recrutador.senhaHash == "hash-seguro"
        recrutador.ativo
    }

    def "deve iniciar ativo mesmo sem argumentos"() {
        expect:
        new Recrutador().ativo
    }

    def "deve aceitar campos opcionais nulos sem alterar a instanciação"() {
        when:
        Recrutador recrutador = new Recrutador(
                id: null, empresaId: null, nome: null, areaAtuacao: null,
                nivelPermissao: null, descricao: null, email: null, cpf: null,
                dataNascimento: null, senhaHash: null
        )

        then:
        recrutador.id == null
        recrutador.nome == null
        recrutador.dataNascimento == null
        recrutador.senhaHash == null
        recrutador.ativo
    }

    def "deve preservar limites de id e data sem coerção silenciosa"() {
        given:
        LocalDate data = LocalDate.of(1900, 1, 1)

        when:
        Recrutador recrutador = new Recrutador(id: Integer.MAX_VALUE, dataNascimento: data)

        then:
        recrutador.id == Integer.MAX_VALUE
        recrutador.dataNascimento == data
    }
}
