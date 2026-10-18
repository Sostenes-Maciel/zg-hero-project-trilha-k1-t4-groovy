package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.interfaces.IConexao
import spock.lang.Specification

class CompetenciaCRUDSpec extends Specification {

    private CompetenciaDAO competenciaDAO

    def setup() {
        IConexao conexao = ConexaoFactory.getBancoDeDados("POSTGRES")
        competenciaDAO = new CompetenciaDAO(conexao)

        competenciaDAO.excluir("Competencia CRUD Teste")
        competenciaDAO.excluir("Competencia CRUD Atualizada")
    }

    def "deve realizar o CRUD de competencia no banco"() {
        when: "uma nova competência é cadastrada"
        competenciaDAO.cadastrar("Competencia CRUD Teste")

        then: "ela deve aparecer na listagem de todas as competências"
        competenciaDAO.listarTodos().contains("Competencia CRUD Teste")

        when: "o nome da competência é atualizado"
        competenciaDAO.atualizar(
                "Competencia CRUD Teste",
                "Competencia CRUD Atualizada"
        )

        then: "o nome novo deve constar na lista e o antigo não"
        competenciaDAO.listarTodos().contains("Competencia CRUD Atualizada")
        !competenciaDAO.listarTodos().contains("Competencia CRUD Teste")

        when: "a competência atualizada é excluída"
        competenciaDAO.excluir("Competencia CRUD Atualizada")

        then: "ela não deve mais constar na listagem"
        !competenciaDAO.listarTodos().contains("Competencia CRUD Atualizada")

        cleanup:
        competenciaDAO.excluir("Competencia CRUD Teste")
        competenciaDAO.excluir("Competencia CRUD Atualizada")
    }
}