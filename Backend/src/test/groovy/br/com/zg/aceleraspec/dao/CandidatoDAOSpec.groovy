package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.ConexaoPostgres
import br.com.zg.acelera.model.Candidato
import spock.lang.Specification

class CandidatoDAOSpec extends Specification {

    private CandidatoDAO candidatoDAO

    def setup() {
        candidatoDAO = new CandidatoDAO(new ConexaoPostgres())

        candidatoDAO.excluir("999.999.999-99")
    }

    def "deve realizar o CRUD de candidato no banco"() {
        given: "um candidato valido para teste"
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

        when: "acionamos o metodo de cadastro"
        candidatoDAO.cadastrar(candidato)

        then: "ao buscar por ID, o nome deve coincidir com o cadastrado"
        candidatoDAO.buscarPorId("999.999.999-99").nome == "Candidato Teste"

        when: "alteramos o nome e salvamos no banco"
        candidato.nome = "Candidato Atualizado"
        candidatoDAO.atualizar(candidato)

        then: "a nova busca deve refletir o nome atualizado"
        candidatoDAO.buscarPorId("999.999.999-99").nome == "Candidato Atualizado"

        when: "acionamos o metodo de exclusao"
        candidatoDAO.excluir("999.999.999-99")

        then: "o candidato nao deve mais ser encontrado"
        candidatoDAO.buscarPorId("999.999.999-99") == null
    }
}