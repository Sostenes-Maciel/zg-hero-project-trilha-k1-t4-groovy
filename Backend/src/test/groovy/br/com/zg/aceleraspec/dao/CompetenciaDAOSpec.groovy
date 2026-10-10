package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.model.Candidato
import spock.lang.Specification

class CompetenciaDAOSpec extends Specification {

    private CompetenciaDAO competenciaDAO
    private CandidatoDAO candidatoDAO

    def setup() {
        competenciaDAO = new CompetenciaDAO()
        candidatoDAO = new CandidatoDAO()

        candidatoDAO.excluir("99999999999")
        competenciaDAO.excluir("Java Teste")
    }

    def "deve cadastrar e vincular competencia ao candidato"() {
        given:
        Candidato candidato = new Candidato(
                cpf: "99999999999",
                nome: "Candidato Competencia",
                email: "competencia@teste.com",
                pais: "Brasil",
                estado: "PE",
                cep: "55299-300",
                descricao: "Teste de competencia",
                idade: 25
        )

        competenciaDAO.cadastrar("Java Teste")
        candidatoDAO.cadastrar(candidato)

        when:
        competenciaDAO.adicionarAoCandidato(
                candidato.cpf,
                "Java Teste"
        )

        then:
        competenciaDAO.listarDoCandidato(
                candidato.cpf
        ) == ["Java Teste"]

        when:
        competenciaDAO.removerDoCandidato(
                candidato.cpf,
                "Java Teste"
        )

        then:
        competenciaDAO.listarDoCandidato(
                candidato.cpf
        ).isEmpty()

        cleanup:
        candidatoDAO.excluir("99999999999")
        competenciaDAO.excluir("Java Teste")
    }


}