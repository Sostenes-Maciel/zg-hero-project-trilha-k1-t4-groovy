package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class VagaCompetenciaDAOSpec extends Specification {

    private VagaDAO vagaDAO
    private EmpresaDAO empresaDAO
    private CompetenciaDAO competenciaDAO

    def setup() {
        vagaDAO = new VagaDAO()
        empresaDAO = new EmpresaDAO()
        competenciaDAO = new CompetenciaDAO()

        vagaDAO.listarPorEmpresa("88.777.666/0001-55").each {
            vagaDAO.excluir(it.id)
        }

        empresaDAO.excluir("88.777.666/0001-55")
        competenciaDAO.excluir("Java Vaga Teste")
    }

    def "deve vincular competencia a uma vaga"() {
        given:
        Empresa empresa = new Empresa(
                cnpj: "88.777.666/0001-55",
                nome: "Empresa Vaga Competencia",
                email: "vaga.competencia@teste.com",
                pais: "Brasil",
                estado: "PE",
                cep: "55299-300",
                descricao: "Teste da relação vaga e competencia"
        )

        empresaDAO.cadastrar(empresa)
        competenciaDAO.cadastrar("Java Vaga Teste")

        Vaga vaga = new Vaga(
                titulo: "Desenvolvedor Java Teste",
                pais: "Brasil",
                estado: "PE",
                descricao: "Vaga usada no teste de competencia",
                empresa: empresa
        )

        when:
        vagaDAO.cadastrar(vaga)

        then:
        Vaga vagaBanco = vagaDAO.listarPorEmpresa(
                empresa.cnpj
        )[0]

        vagaBanco.id != null

        when:
        competenciaDAO.adicionarAVaga(
                vagaBanco.id,
                "Java Vaga Teste"
        )

        then:
        competenciaDAO.listarDaVaga(
                vagaBanco.id
        ) == ["Java Vaga Teste"]

        when:
        competenciaDAO.removerDaVaga(
                vagaBanco.id,
                "Java Vaga Teste"
        )

        then:
        competenciaDAO.listarDaVaga(
                vagaBanco.id
        ).isEmpty()

        cleanup:
        vagaDAO.listarPorEmpresa(empresa.cnpj).each {
            vagaDAO.excluir(it.id)
        }

        empresaDAO.excluir(empresa.cnpj)
        competenciaDAO.excluir("Java Vaga Teste")
    }
}