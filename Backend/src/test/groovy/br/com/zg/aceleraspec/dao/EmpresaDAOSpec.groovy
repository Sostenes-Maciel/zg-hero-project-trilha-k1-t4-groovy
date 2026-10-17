package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.ConexaoPostgres
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Empresa
import spock.lang.Specification

class EmpresaDAOSpec extends Specification {

    private EmpresaDAO empresaDAO

    def setup() {
        empresaDAO = new EmpresaDAO(ConexaoFactory.getBancoDeDados("POSTGRES") as IConexao)

        empresaDAO.excluir("99.888.777/0001-66")
    }

    def "deve realizar o CRUD de empresa no banco"() {
        given: "uma empresa valida para teste"
        Empresa empresa = new Empresa(
                cnpj: "99.888.777/0001-66",
                nome: "Empresa Teste",
                email: "empresa@teste.com",
                pais: "Brasil",
                estado: "PE",
                cep: "55299-300",
                descricao: "Empresa usada no teste do DAO"
        )

        when: "acionamos o metodo de cadastro"
        empresaDAO.cadastrar(empresa)

        then: "ao buscar por ID, o nome deve coincidir com o cadastrado"
        empresaDAO.buscarPorId("99.888.777/0001-66").nome == "Empresa Teste"

        when: "alteramos o nome e salvamos no banco"
        empresa.nome = "Empresa Atualizada"
        empresaDAO.atualizar(empresa)

        then: "a nova busca deve refletir o nome atualizado"
        empresaDAO.buscarPorId("99.888.777/0001-66").nome == "Empresa Atualizada"

        when: "acionamos o metodo de exclusao"
        empresaDAO.excluir("99.888.777/0001-66")

        then: "a empresa nao deve mais ser encontrada"
        empresaDAO.buscarPorId("99.888.777/0001-66") == null

        cleanup:
        empresaDAO.excluir("99.888.777/0001-66")
    }
}