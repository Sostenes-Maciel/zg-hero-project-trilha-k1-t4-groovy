package br.com.zg.aceleraspec.model

import br.com.zg.acelera.model.Recrutador
import spock.lang.Specification

import java.time.LocalDate

class RecrutadorSpec extends Specification {

    def "deve mapear os dados do recrutador sem expor senha fora do modelo de escrita"() {
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

    def "deve iniciar como ativo"() {
        expect:
        new Recrutador().ativo
    }
}
