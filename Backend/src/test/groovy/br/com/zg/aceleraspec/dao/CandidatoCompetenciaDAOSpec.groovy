package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.ConexaoPostgres
import br.com.zg.acelera.model.Candidato
import spock.lang.Specification

class CandidatoCompetenciaDAOSpec extends Specification {

    private CandidatoDAO candidatoDAO
    private CompetenciaDAO competenciaDAO

    def setup() {
        def conexaoFactory = new ConexaoPostgres()
        candidatoDAO = new CandidatoDAO(conexaoFactory)
        competenciaDAO = new CompetenciaDAO(conexaoFactory)

        candidatoDAO.excluir("111.222.333-44")
        competenciaDAO.excluir("Groovy Teste")
    }

    def "deve vincular competencia a um candidato"() {
        given:
        Candidato candidato = new Candidato(
                cpf: "111.222.333-44",
                nome: "Candidato Relacao Teste",
                email: "candidato.relacao@teste.com",
                pais: "Brasil",
                estado: "PE",
                cep: "55299-300",
                descricao: "Teste da relacao candidato e competencia",
                idade: 28
        )

        candidatoDAO.cadastrar(candidato)
        competenciaDAO.cadastrar("Groovy Teste")

        when:
        competenciaDAO.adicionarAoCandidato(
                candidato.cpf,
                "Groovy Teste"
        )

        then:
        competenciaDAO.listarDoCandidato(
                candidato.cpf
        ) == ["Groovy Teste"]

        when:
        competenciaDAO.removerDoCandidato(
                candidato.cpf,
                "Groovy Teste"
        )

        then:
        competenciaDAO.listarDoCandidato(
                candidato.cpf
        ).isEmpty()

        cleanup:
        candidatoDAO.excluir(candidato.cpf)
        competenciaDAO.excluir("Groovy Teste")
    }
}