package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.ConexaoPostgres
import br.com.zg.acelera.dao.CurtidaDAO
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class CurtidaDAOSpec extends Specification {

    private CurtidaDAO curtidaDAO
    private CandidatoDAO candidatoDAO
    private EmpresaDAO empresaDAO
    private VagaDAO vagaDAO

    def setup() {
        def conexaoFactory = new ConexaoPostgres()
        curtidaDAO = new CurtidaDAO(conexaoFactory)
        candidatoDAO = new CandidatoDAO(conexaoFactory)
        empresaDAO = new EmpresaDAO(conexaoFactory)
        vagaDAO = new VagaDAO(conexaoFactory)
    }

    def "deve registrar curtida do candidato em uma vaga"() {
        given: "uma empresa, uma vaga e um candidato cadastrados"
        Empresa empresa = new Empresa(cnpj: "77.666.555/0001-33", nome: "Empresa Curtida", email: "e@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste")
        Vaga vaga = new Vaga(titulo: "Dev Teste", pais: "Brasil", estado: "PE", descricao: "Teste", empresa: empresa)
        Candidato candidato = new Candidato(cpf: "77788899900", nome: "Cand Curtida", email: "c@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste", idade: 25)

        empresaDAO.cadastrar(empresa)
        vagaDAO.cadastrar(vaga)
        candidatoDAO.cadastrar(candidato)

        when: "o candidato curte a vaga"
        curtidaDAO.registrarCurtidaCandidato(candidato.cpf, vaga.id)

        then: "o match direto ainda não deve existir sem a curtida da empresa"
        !curtidaDAO.verificarMatch(candidato.cpf, empresa.cnpj, vaga.id)

        cleanup:
        vagaDAO.excluir(vaga.id)
        empresaDAO.excluir(empresa.cnpj)
        candidatoDAO.excluir(candidato.cpf)
    }

    def "deve registrar curtida da empresa no candidato"() {
        given: "uma empresa e um candidato cadastrados"
        Empresa empresa = new Empresa(cnpj: "77.666.555/0001-33", nome: "Empresa Curtida", email: "e@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste")
        Candidato candidato = new Candidato(cpf: "77788899900", nome: "Cand Curtida", email: "c@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste", idade: 25)

        empresaDAO.cadastrar(empresa)
        candidatoDAO.cadastrar(candidato)

        when: "a empresa curte o candidato"
        curtidaDAO.registrarCurtidaEmpresa(candidato.cpf, empresa.cnpj)

        then: "a operacao ocorre sem erros"
        noExceptionThrown()

        cleanup:
        empresaDAO.excluir(empresa.cnpj)
        candidatoDAO.excluir(candidato.cpf)
    }
}