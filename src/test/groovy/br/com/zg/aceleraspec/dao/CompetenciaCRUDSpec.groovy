package br.com.zg.acelera.dao

import spock.lang.Specification

class CompetenciaCRUDSpec extends Specification {

    private CompetenciaDAO competenciaDAO

    def setup() {
        competenciaDAO = new CompetenciaDAO()

        competenciaDAO.excluir("Competencia CRUD Teste")
        competenciaDAO.excluir("Competencia CRUD Atualizada")
    }

    def "deve realizar o CRUD de competencia no banco"() {
        when:
        competenciaDAO.cadastrar("Competencia CRUD Teste")

        then:
        competenciaDAO.listarTodos().contains("Competencia CRUD Teste")

        when:
        competenciaDAO.atualizar(
                "Competencia CRUD Teste",
                "Competencia CRUD Atualizada"
        )

        then:
        competenciaDAO.listarTodos().contains("Competencia CRUD Atualizada")
        !competenciaDAO.listarTodos().contains("Competencia CRUD Teste")

        when:
        competenciaDAO.excluir("Competencia CRUD Atualizada")

        then:
        !competenciaDAO.listarTodos().contains("Competencia CRUD Atualizada")

        cleanup:
        competenciaDAO.excluir("Competencia CRUD Teste")
        competenciaDAO.excluir("Competencia CRUD Atualizada")
    }
}