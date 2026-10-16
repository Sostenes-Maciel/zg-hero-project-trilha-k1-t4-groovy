package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.ConexaoPostgres
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class VagaDAOSpec extends Specification {

    private VagaDAO vagaDAO
    private EmpresaDAO empresaDAO

    def setup() {
        def conexaoFactory = new ConexaoPostgres()
        vagaDAO = new VagaDAO(conexaoFactory)
        empresaDAO = new EmpresaDAO(conexaoFactory)

        vagaDAO.listarPorEmpresa("11.222.333/0001-44").each {
            vagaDAO.excluir(it.id)
        }

        empresaDAO.excluir("11.222.333/0001-44")
    }

    def "deve realizar o CRUD de vaga vinculada a uma empresa"() {
        given: "uma empresa e uma vaga validas"
        Empresa empresa = new Empresa(
                cnpj: "11.222.333/0001-44",
                nome: "Empresa Vaga Teste",
                email: "vaga@teste.com",
                pais: "Brasil",
                estado: "PE",
                cep: "55299-300",
                descricao: "Empresa usada no teste de vaga"
        )

        Vaga vaga = new Vaga(
                titulo: "Desenvolvedor Groovy",
                pais: "Brasil",
                estado: "PE",
                descricao: "Vaga usada no teste do DAO",
                empresa: empresa
        )

        empresaDAO.cadastrar(empresa)

        when: "cadastramos a vaga"
        vagaDAO.cadastrar(vaga)

        then: "a model da vaga deve ter recebido o ID gerado pelo banco automaticamente"
        vaga.id != null

        and: "ao listar pela empresa, a vaga deve ser encontrada com os dados corretos"
        List<Vaga> vagas = vagaDAO.listarPorEmpresa(empresa.cnpj)
        vagas.size() == 1
        vagas[0].titulo == "Desenvolvedor Groovy"
        vagas[0].empresa.cnpj == empresa.cnpj

        and: "o id da vaga persistida no banco nao pode ser nulo"
        Vaga vagaBanco = vagas[0]
        vagaBanco.id != null

        when: "atualizamos os dados da vaga"
        vagaBanco.titulo = "Desenvolvedor Groovy Atualizado"
        vagaDAO.atualizar(vagaBanco)

        then: "a busca por id deve refletir as alteracoes"
        vagaDAO.buscarPorId(vagaBanco.id).titulo == "Desenvolvedor Groovy Atualizado"
        vagaDAO.buscarPorId(vagaBanco.id).empresa.cnpj == empresa.cnpj

        when: "excluimos a vaga pelo id"
        vagaDAO.excluir(vagaBanco.id)

        then: "a vaga nao deve mais existir no banco"
        vagaDAO.buscarPorId(vagaBanco.id) == null

        cleanup:
        vagaDAO.listarPorEmpresa(empresa.cnpj).each {
            vagaDAO.excluir(it.id)
        }
        empresaDAO.excluir(empresa.cnpj)
    }
}