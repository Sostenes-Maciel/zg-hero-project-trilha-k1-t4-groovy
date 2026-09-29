package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Empresa
import spock.lang.Specification

class EmpresaDAOSpec extends Specification {

    private EmpresaDAO empresaDAO

    def setup() {
        empresaDAO = new EmpresaDAO()
        empresaDAO.excluir("99.888.777/0001-66")
    }

    def "deve realizar o CRUD de empresa no banco"() {
        given:
        Empresa empresa = new Empresa(
                cnpj: "99.888.777/0001-66",
                nome: "Empresa Teste",
                email: "empresa@teste.com",
                pais: "Brasil",
                estado: "PE",
                cep: "55299-300",
                descricao: "Empresa usada no teste do DAO"
        )

        when:
        empresaDAO.cadastrar(empresa)

        then:
        empresaDAO.buscarPorCnpj("99.888.777/0001-66").nome == "Empresa Teste"

        when:
        empresa.nome = "Empresa Atualizada"
        empresaDAO.atualizar(empresa)

        then:
        empresaDAO.buscarPorCnpj("99.888.777/0001-66").nome == "Empresa Atualizada"

        when:
        empresaDAO.excluir("99.888.777/0001-66")

        then:
        empresaDAO.buscarPorCnpj("99.888.777/0001-66") == null

        cleanup:
        empresaDAO.excluir("99.888.777/0001-66")
    }
}