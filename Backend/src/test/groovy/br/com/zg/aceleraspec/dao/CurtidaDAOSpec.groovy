package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.dao.CurtidaDAO
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Recrutador
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class CurtidaDAOSpec extends Specification {

    private CurtidaDAO curtidaDAO
    private CandidatoDAO candidatoDAO
    private EmpresaDAO empresaDAO
    private VagaDAO vagaDAO
    private CompetenciaDAO competenciaDAO
    private br.com.zg.acelera.dao.RecrutadorDAO recrutadorDAO

    def setup() {
        IConexao conexao = ConexaoFactory.getBancoDeDados("POSTGRES")
        competenciaDAO = new CompetenciaDAO(conexao)
        curtidaDAO = new CurtidaDAO(conexao)
        candidatoDAO = new CandidatoDAO(conexao, competenciaDAO)
        empresaDAO = new EmpresaDAO(conexao, competenciaDAO)
        vagaDAO = new VagaDAO(conexao)
        recrutadorDAO = new br.com.zg.acelera.dao.RecrutadorDAO(conexao)
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

    def "deve registrar curtida da empresa no candidato para uma vaga"() {
        given: "uma empresa, vaga e candidato cadastrados"
        Empresa empresa = new Empresa(cnpj: "77.666.555/0001-33", nome: "Empresa Curtida", email: "e@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste")
        Vaga vaga = new Vaga(titulo: "Dev Teste", pais: "Brasil", estado: "PE", descricao: "Teste", empresa: empresa)
        Candidato candidato = new Candidato(cpf: "77788899900", nome: "Cand Curtida", email: "c@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste", idade: 25)

        empresaDAO.cadastrar(empresa)
        vagaDAO.cadastrar(vaga)
        candidatoDAO.cadastrar(candidato)

        when: "a empresa curte o candidato"
        curtidaDAO.registrarCurtidaEmpresa(candidato.cpf, empresa.cnpj, vaga.id)

        then: "a curtida fica associada a vaga e ainda não há reciprocidade"
        curtidaDAO.jaCurtiuCandidato(empresa.cnpj, candidato.cpf, vaga.id)
        !curtidaDAO.verificarMatch(candidato.cpf, empresa.cnpj, vaga.id)
        curtidaDAO.listarStatusCurtidasEmpresa(empresa.cnpj).find {
            it.cpfCandidato == candidato.cpf && it.idVaga == vaga.id
        }.status == "curtido"

        cleanup:
        vagaDAO.excluir(vaga.id)
        empresaDAO.excluir(empresa.cnpj)
        candidatoDAO.excluir(candidato.cpf)
    }

    def "so deve confirmar reciprocidade quando as duas partes escolherem a mesma vaga"() {
        given:
        Empresa empresa = new Empresa(cnpj: "77.666.555/0001-33", nome: "Empresa Curtida", email: "e@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste")
        Vaga vagaA = new Vaga(titulo: "Dev A", pais: "Brasil", estado: "PE", descricao: "Teste", empresa: empresa)
        Vaga vagaB = new Vaga(titulo: "Dev B", pais: "Brasil", estado: "PE", descricao: "Teste", empresa: empresa)
        Candidato candidato = new Candidato(cpf: "77788899900", nome: "Cand Curtida", email: "c@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste", idade: 25)
        empresaDAO.cadastrar(empresa)
        vagaDAO.cadastrar(vagaA)
        vagaDAO.cadastrar(vagaB)
        candidatoDAO.cadastrar(candidato)
        curtidaDAO.registrarCurtidaCandidato(candidato.cpf, vagaA.id)

        when:
        curtidaDAO.registrarCurtidaEmpresa(candidato.cpf, empresa.cnpj, vagaB.id)

        then:
        !curtidaDAO.verificarMatch(candidato.cpf, empresa.cnpj, vagaA.id)
        !curtidaDAO.verificarMatch(candidato.cpf, empresa.cnpj, vagaB.id)

        when:
        curtidaDAO.registrarCurtidaEmpresa(candidato.cpf, empresa.cnpj, vagaA.id)

        then:
        curtidaDAO.verificarMatch(candidato.cpf, empresa.cnpj, vagaA.id)

        cleanup:
        vagaDAO.excluir(vagaA.id)
        vagaDAO.excluir(vagaB.id)
        empresaDAO.excluir(empresa.cnpj)
        candidatoDAO.excluir(candidato.cpf)
    }

    def "nao deve permitir remover nenhuma das curtidas depois do match"() {
        given: "uma empresa, vaga e candidato com curtidas recíprocas e match"
        Empresa empresa = new Empresa(cnpj: "77.666.555/0001-33", nome: "Empresa Curtida", email: "e@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste")
        Vaga vaga = new Vaga(titulo: "Dev Teste", pais: "Brasil", estado: "PE", descricao: "Teste", empresa: empresa)
        Candidato candidato = new Candidato(cpf: "77788899900", nome: "Cand Curtida", email: "c@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste", idade: 25)
        empresaDAO.cadastrar(empresa)
        vagaDAO.cadastrar(vaga)
        candidatoDAO.cadastrar(candidato)
        curtidaDAO.registrarCurtidaCandidato(candidato.cpf, vaga.id)
        curtidaDAO.registrarCurtidaEmpresa(candidato.cpf, empresa.cnpj, vaga.id)
        new br.com.zg.acelera.dao.MatchDAO(ConexaoFactory.getBancoDeDados("POSTGRES"))
                .registrarMatch(candidato.cpf, empresa.cnpj, vaga.id)

        when:
        boolean removeuCurtidaEmpresa = curtidaDAO.removerCurtidaEmpresa(empresa.cnpj, candidato.cpf, vaga.id)
        boolean removeuCurtidaCandidato = curtidaDAO.removerCurtidaCandidato(candidato.cpf, vaga.id)
        def statusEmpresa = curtidaDAO.listarStatusCurtidasEmpresa(empresa.cnpj).find {
            it.cpfCandidato == candidato.cpf && it.idVaga == vaga.id
        }
        def statusCandidato = curtidaDAO.listarStatusCurtidas(candidato.cpf).find {
            it.idVaga == vaga.id
        }

        then:
        !removeuCurtidaEmpresa
        !removeuCurtidaCandidato
        curtidaDAO.jaCurtiuCandidato(empresa.cnpj, candidato.cpf, vaga.id)
        curtidaDAO.jaCurtiuVaga(candidato.cpf, vaga.id)
        statusEmpresa.status == "match"
        statusCandidato.status == "match"

        cleanup:
        vagaDAO.excluir(vaga.id)
        empresaDAO.excluir(empresa.cnpj)
        candidatoDAO.excluir(candidato.cpf)
    }

    def "deve registrar a autoria opcional da curtida da empresa"() {
        given:
        Empresa empresa = new Empresa(cnpj: "77.666.555/0001-33", nome: "Empresa Curtida", email: "e@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste")
        Vaga vaga = new Vaga(titulo: "Dev Teste", pais: "Brasil", estado: "PE", descricao: "Teste", empresa: empresa)
        Candidato candidato = new Candidato(cpf: "77788899900", nome: "Cand Curtida", email: "c@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste", idade: 25)
        Recrutador recrutador = new Recrutador(
                empresaId: empresa.cnpj,
                nome: "Recrutador Curtida",
                areaAtuacao: "TI",
                nivelPermissao: "Limitado",
                email: "recrutador.curtida.${System.nanoTime()}@teste.com",
                cpf: "${System.nanoTime()}".takeRight(11),
                dataNascimento: java.time.LocalDate.of(1990, 1, 1),
                senhaHash: "hash"
        )

        empresaDAO.cadastrar(empresa)
        vagaDAO.cadastrar(vaga)
        candidatoDAO.cadastrar(candidato)
        recrutadorDAO.salvar(recrutador)

        when:
        curtidaDAO.registrarCurtidaEmpresa(candidato.cpf, empresa.cnpj, vaga.id, recrutador.id)

        then:
        def curtida = curtidaDAO.listarStatusCurtidasEmpresa(empresa.cnpj).find {
            it.cpfCandidato == candidato.cpf && it.idVaga == vaga.id
        }
        curtida.recrutadorId == recrutador.id

        cleanup:
        removerRecrutador(recrutador.email)
        vagaDAO.excluir(vaga.id)
        empresaDAO.excluir(empresa.cnpj)
        candidatoDAO.excluir(candidato.cpf)
    }

    private static void removerRecrutador(String email) {
        def conexao = ConexaoFactory.getBancoDeDados("POSTGRES").conectar()
        def stmt = conexao.prepareStatement("DELETE FROM recrutador WHERE email = ?")
        try {
            stmt.setString(1, email)
            stmt.executeUpdate()
        } finally {
            stmt.close()
            conexao.close()
        }
    }
}