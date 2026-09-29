package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class VagaDAOSpec extends Specification {

    private VagaDAO vagaDAO
    private EmpresaDAO empresaDAO

    def setup() {
        vagaDAO = new VagaDAO()
        empresaDAO = new EmpresaDAO()

        vagaDAO.listarPorEmpresa("11.222.333/0001-44").each {
            vagaDAO.excluir(it.id)
        }

        empresaDAO.excluir("11.222.333/0001-44")
    }

    def "deve realizar o CRUD de vaga vinculada a uma empresa"() {
        given:
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

        when:
        vagaDAO.cadastrar(vaga)

        then:
        List<Vaga> vagas = vagaDAO.listarPorEmpresa(empresa.cnpj)
        vagas.size() == 1
        vagas[0].titulo == "Desenvolvedor Groovy"
        vagas[0].empresa.cnpj == empresa.cnpj

        and:
        Vaga vagaBanco = vagas[0]
        vagaBanco.id != null

        when:
        vagaBanco.titulo = "Desenvolvedor Groovy Atualizado"
        vagaDAO.atualizar(vagaBanco)

        then:
        vagaDAO.buscarPorId(vagaBanco.id).titulo == "Desenvolvedor Groovy Atualizado"
        vagaDAO.buscarPorId(vagaBanco.id).empresa.cnpj == empresa.cnpj

        when:
        vagaDAO.excluir(vagaBanco.id)

        then:
        vagaDAO.buscarPorId(vagaBanco.id) == null

        cleanup:
        vagaDAO.listarPorEmpresa(empresa.cnpj).each {
            vagaDAO.excluir(it.id)
        }

        empresaDAO.excluir(empresa.cnpj)
    }
}