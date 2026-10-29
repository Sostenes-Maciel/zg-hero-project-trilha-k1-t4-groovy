package br.com.zg.aceleraspec.service

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.CurtidaDAO
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.service.GerenciadorDePerfisService
import br.com.zg.acelera.service.GerenciadorDeVagasService
import spock.lang.Specification

class GerenciadorDePerfisServiceSpec extends Specification {

    GerenciadorDePerfisService service
    CandidatoDAO candidatoDAOMock
    EmpresaDAO empresaDAOMock
    CompetenciaDAO competenciaDAOMock
    CurtidaDAO curtidaDAOMock
    MatchDAO matchDAOMock
    GerenciadorDeVagasService vagaServiceMock

    def setup() {
        candidatoDAOMock = Mock(CandidatoDAO)
        empresaDAOMock = Mock(EmpresaDAO)
        competenciaDAOMock = Mock(CompetenciaDAO)
        curtidaDAOMock = Mock(CurtidaDAO)
        matchDAOMock = Mock(MatchDAO)
        vagaServiceMock = Mock(GerenciadorDeVagasService)

        service = new GerenciadorDePerfisService(
                candidatoDAOMock,
                competenciaDAOMock,
                empresaDAOMock,
                curtidaDAOMock,
                matchDAOMock,
                vagaServiceMock
        )
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
        service.processarPossivelMatch(cpf, cnpj, vagaId)

        then:
        IllegalStateException e = thrown()
        e.message == "Atenção: Vocês já deram Match anteriormente!"
        0 * matchDAOMock.registrarMatch(cpf, cnpj, vagaId)
    }

    def "deve curtir vaga e verificar match"() {
        given:
        String cpf = "12345678901"
        Empresa empresa = new Empresa(cnpj: "12345678901234")
        Vaga vaga = new Vaga(id: 1, empresa: empresa)

        when:
        boolean deuMatch = service.curtirVaga(cpf, vaga)

        then:
        1 * vagaServiceMock.buscarVagaPorId(vaga.id) >> vaga
        1 * curtidaDAOMock.jaCurtiuVaga(cpf, vaga.id) >> false
        1 * curtidaDAOMock.registrarCurtidaCandidato(cpf, vaga.id)
        1 * curtidaDAOMock.verificarMatch(cpf, empresa.cnpj, vaga.id) >> false

        and: "Nenhum match deve ser registrado"
        0 * matchDAOMock.registrarMatch(_, _, _)

        and:
        !deuMatch
    }
}