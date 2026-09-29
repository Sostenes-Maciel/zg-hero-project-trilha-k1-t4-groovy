package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.model.Candidato
import spock.lang.Specification

class CandidatoDAOSpec extends Specification {

    private CandidatoDAO candidatoDAO

    def setup() {
        candidatoDAO = new CandidatoDAO()
        candidatoDAO.excluir("999.999.999-99")
    }

    def "deve realizar o CRUD de candidato no banco"() {
        given:
        Candidato candidato = new Candidato(
                cpf: "999.999.999-99",
                nome: "Candidato Teste",
                email: "teste@linketinder.com",
                pais: "Brasil",
                estado: "PE",
                cep: "55299-300",
                descricao: "Candidato usado no teste do DAO",
                idade: 25
        )

        when:
        candidatoDAO.cadastrar(candidato)

        then:
        candidatoDAO.buscarPorCpf("999.999.999-99").nome == "Candidato Teste"

        when:
        candidato.nome = "Candidato Atualizado"
        candidatoDAO.atualizar(candidato)

        then:
        candidatoDAO.buscarPorCpf("999.999.999-99").nome == "Candidato Atualizado"

        when:
        candidatoDAO.excluir("999.999.999-99")

        then:
        candidatoDAO.buscarPorCpf("999.999.999-99") == null
    }
}