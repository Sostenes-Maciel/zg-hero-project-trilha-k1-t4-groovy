package br.com.zg.aceleraspec.service

import br.com.zg.acelera.dao.*
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.service.GerenciadorDePerfisService
import spock.lang.Specification

class GerenciadorDePerfisServiceSpec extends Specification {

    GerenciadorDePerfisService service
    CandidatoDAO candidatoDAOMock
    EmpresaDAO empresaDAOMock
    CompetenciaDAO competenciaDAOMock
    CurtidaDAO curtidaDAOMock
    MatchDAO matchDAOMock

    def setup() {
        IConexao conexaoMock = Mock(IConexao)
        service = new GerenciadorDePerfisService(conexaoMock)

        candidatoDAOMock = Mock(CandidatoDAO)
        empresaDAOMock = Mock(EmpresaDAO)
        competenciaDAOMock = Mock(CompetenciaDAO)
        curtidaDAOMock = Mock(CurtidaDAO)
        matchDAOMock = Mock(MatchDAO)

        service.candidatoDAO = candidatoDAOMock
        service.empresaDAO = empresaDAOMock
        service.competenciaDAO = competenciaDAOMock
        service.curtidaDAO = curtidaDAOMock
        service.matchDAO = matchDAOMock
    }

    def "deve cadastrar candidato com sucesso"() {
        given:
        Candidato candidato = new Candidato(nome: "Joao", email: "joao@email.com", cpf: "12345678901", idade: 25, estado: "SP", cep: "12345678", descricao: "Dev")

        when:
        service.cadastrarCandidato(candidato)

        then:
        1 * candidatoDAOMock.cadastrar(candidato)
    }

    def "deve cadastrar empresa com sucesso"() {
        given:
        Empresa empresa = new Empresa(nome: "Zup", email: "zup@email.com", cnpj: "12345678901234", pais: "Brasil", estado: "SP", cep: "12345678", descricao: "Tech")

        when:
        service.cadastrarEmpresa(empresa)

        then:
        1 * empresaDAOMock.cadastrar(empresa)
    }

    def "deve listar todos os candidatos e empresas"() {
        when:
        service.listarCandidatos()
        service.listarEmpresas()

        then:
        1 * candidatoDAOMock.listarTodos()
        1 * empresaDAOMock.listarTodos()
    }

    def "deve processar match corretamente quando as duas partes se curtem"() {
        given:
        String cpf = "12345678901"
        String cnpj = "12345678901234"
        int vagaId = 1
        curtidaDAOMock.verificarMatch(cpf, cnpj, vagaId) >> true
        matchDAOMock.existeMatch(cpf, cnpj, vagaId) >> false

        when:
        boolean deuMatch = service.processarPossivelMatch(cpf, cnpj, vagaId)

        then:
        deuMatch == true
        1 * matchDAOMock.registrarMatch(cpf, cnpj, vagaId)
    }

    def "nao deve registrar match se ja existir"() {
        given:
        String cpf = "12345678901"
        String cnpj = "12345678901234"
        int vagaId = 1
        curtidaDAOMock.verificarMatch(cpf, cnpj, vagaId) >> true
        matchDAOMock.existeMatch(cpf, cnpj, vagaId) >> true

        when:
        boolean deuMatch = service.processarPossivelMatch(cpf, cnpj, vagaId)

        then:
        deuMatch == true
        0 * matchDAOMock.registrarMatch(cpf, cnpj, vagaId)
    }

    def "deve curtir vaga e verificar match"() {
        given:
        String cpf = "12345678901"
        Empresa empresa = new Empresa(cnpj: "12345678901234")
        Vaga vaga = new Vaga(id: 1, empresa: empresa)
        curtidaDAOMock.verificarMatch(cpf, empresa.cnpj, vaga.id) >> false

        when:
        boolean deuMatch = service.curtirVaga(cpf, vaga)

        then:
        1 * curtidaDAOMock.registrarCurtidaCandidato(cpf, vaga.id)
        deuMatch == false
    }
}