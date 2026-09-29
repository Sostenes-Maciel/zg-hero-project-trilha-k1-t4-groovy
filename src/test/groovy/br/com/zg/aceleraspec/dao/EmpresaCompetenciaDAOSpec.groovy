package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Empresa
import spock.lang.Specification

class EmpresaCompetenciaDAOSpec extends Specification {

    private EmpresaDAO empresaDAO
    private CompetenciaDAO competenciaDAO

    def setup() {
        empresaDAO = new EmpresaDAO()
        competenciaDAO = new CompetenciaDAO()

        empresaDAO.excluir("99.888.777/0001-66")
        competenciaDAO.excluir("Java Empresa Teste")
    }

    def "deve vincular competencia a uma empresa"() {
        given:
        Empresa empresa = new Empresa(
                cnpj: "99.888.777/0001-66",
                nome: "Empresa Competencia Teste",
                email: "empresa.competencia@teste.com",
                pais: "Brasil",
                estado: "PE",
                cep: "55299-300",
                descricao: "Teste da relação empresa e competencia"
        )

        empresaDAO.cadastrar(empresa)
        competenciaDAO.cadastrar("Java Empresa Teste")

        when:
        competenciaDAO.adicionarAoEmpresa(
                empresa.cnpj,
                "Java Empresa Teste"
        )

        then:
        competenciaDAO.listarDaEmpresa(
                empresa.cnpj
        ) == ["Java Empresa Teste"]

        when:
        competenciaDAO.removerDaEmpresa(
                empresa.cnpj,
                "Java Empresa Teste"
        )

        then:
        competenciaDAO.listarDaEmpresa(
                empresa.cnpj
        ).isEmpty()

        cleanup:
        empresaDAO.excluir(empresa.cnpj)
        competenciaDAO.excluir("Java Empresa Teste")
    }
}