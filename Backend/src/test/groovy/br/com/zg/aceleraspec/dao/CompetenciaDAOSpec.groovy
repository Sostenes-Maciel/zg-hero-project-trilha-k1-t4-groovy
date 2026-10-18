package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Candidato
import spock.lang.Specification

class CompetenciaDAOSpec extends Specification {

    private CompetenciaDAO competenciaDAO
    private CandidatoDAO candidatoDAO

    def setup() {
        IConexao conexao = ConexaoFactory.getBancoDeDados("POSTGRES")

        competenciaDAO = new CompetenciaDAO(conexao)
        candidatoDAO = new CandidatoDAO(conexao, competenciaDAO)

        candidatoDAO.excluir("99999999999")
        competenciaDAO.excluir("Java Teste")
    }

    def "deve cadastrar e vincular competencia ao candidato"() {
        given: "um candidato e uma competência cadastrados no banco"
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

        when: "adicionamos a competência ao candidato"
        competenciaDAO.adicionarAoCandidato(
                candidato.cpf,
                "Java Teste"
        )

        then: "o candidato deve possuir a competência ao listar"
        competenciaDAO.listarDoCandidato(
                candidato.cpf
        ) == ["Java Teste"]

        when: "removemos a competência do candidato"
        competenciaDAO.removerDoCandidato(
                candidato.cpf,
                "Java Teste"
        )

        then: "a lista de competências do candidato deve estar vazia"
        competenciaDAO.listarDoCandidato(
                candidato.cpf
        ).isEmpty()

        cleanup:
        candidatoDAO.excluir("99999999999")
        competenciaDAO.excluir("Java Teste")
    }
}