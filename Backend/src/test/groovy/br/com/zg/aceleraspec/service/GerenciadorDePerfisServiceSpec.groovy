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
        Candidato candidato = new Candidato(nome: "Joao", email: "joao@email.com", cpf: "123.456.789-01", idade: 25, estado: "SP", cep: "12345678", descricao: "Dev")

        when:
        service.cadastrarCandidato(candidato)

        then:
        candidato.cpf == "12345678901"
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

    def "deve atualizar candidato e sincronizar competencias no banco"() {
        given:
        Candidato candidato = new Candidato(
                nome: "Joao",
                email: "joao@email.com",
                cpf: "123.456.789-01",
                idade: 25,
                estado: "SP",
                cep: "12345678",
                descricao: "Dev",
                competencias: ["Java", "Groovy"]
        )
        candidatoDAOMock.buscarPorId(candidato.cpf) >> new Candidato(cpf: candidato.cpf)
        competenciaDAOMock.listarDoCandidato(candidato.cpf) >> ["Java", "SQL"]

        when:
        service.atualizarCandidato(candidato)

        then:
        1 * candidatoDAOMock.atualizar(candidato)
        1 * competenciaDAOMock.removerDoCandidato(candidato.cpf, "SQL")
        1 * competenciaDAOMock.garantirCompetencia("Groovy")
        1 * competenciaDAOMock.adicionarAoCandidato(candidato.cpf, "Groovy")
    }

    def "deve atualizar empresa e sincronizar competencias no banco"() {
        given:
        Empresa empresa = new Empresa(
                nome: "Acme",
                email: "contato@acme.com",
                cnpj: "12345678901234",
                pais: "Brasil",
                estado: "SP",
                cep: "12345678",
                descricao: "Tecnologia",
                competencias: ["Java", "Groovy"]
        )
        empresaDAOMock.buscarPorId(empresa.cnpj) >> new Empresa(cnpj: empresa.cnpj)
        competenciaDAOMock.listarDaEmpresa(empresa.cnpj) >> ["Java", "SQL"]

        when:
        service.atualizarEmpresa(empresa)

        then:
        1 * empresaDAOMock.atualizar(empresa)
        1 * competenciaDAOMock.removerDaEmpresa(empresa.cnpj, "SQL")
        1 * competenciaDAOMock.garantirCompetencia("Groovy")
        1 * competenciaDAOMock.adicionarAoEmpresa(empresa.cnpj, "Groovy")
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
        deuMatch
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
        boolean deuMatch = service.curtirVaga(cpf, vaga.id)

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

    def "deve criar match quando empresa curte a mesma vaga que candidato curtiu antes"() {
        given:
        String cpf = "12345678901"
        String cnpj = "12345678901234"
        Integer idVaga = 1
        Vaga vaga = new Vaga(id: idVaga, empresa: new Empresa(cnpj: cnpj))
        vagaServiceMock.buscarVagaPorId(idVaga) >> vaga
        curtidaDAOMock.jaCurtiuCandidato(cnpj, cpf, idVaga) >> false
        curtidaDAOMock.verificarMatch(cpf, cnpj, idVaga) >> true
        matchDAOMock.existeMatch(cpf, cnpj, idVaga) >> false

        when:
        boolean deuMatch = service.curtirCandidato(cnpj, cpf, idVaga)

        then:
        1 * curtidaDAOMock.registrarCurtidaEmpresa(cpf, cnpj, idVaga)
        1 * matchDAOMock.registrarMatch(cpf, cnpj, idVaga)
        deuMatch
    }

    def "deve normalizar CPF antes de registrar curtida da empresa"() {
        given:
        String cpfFormatado = "123.456.789-01"
        String cpfNormalizado = "12345678901"
        String cnpj = "12345678901234"
        Integer idVaga = 1
        Vaga vaga = new Vaga(id: idVaga, empresa: new Empresa(cnpj: cnpj))
        vagaServiceMock.buscarVagaPorId(idVaga) >> vaga
        curtidaDAOMock.jaCurtiuCandidato(cnpj, cpfNormalizado, idVaga) >> false
        curtidaDAOMock.verificarMatch(cpfNormalizado, cnpj, idVaga) >> false

        when:
        boolean deuMatch = service.curtirCandidato(cnpj, cpfFormatado, idVaga)

        then:
        1 * curtidaDAOMock.registrarCurtidaEmpresa(cpfNormalizado, cnpj, idVaga)
        !deuMatch
    }

    def "deve criar match quando empresa curtiu primeiro e candidato curte a mesma vaga"() {
        given:
        String cpf = "12345678901"
        String cnpj = "12345678901234"
        Integer idVaga = 1
        Empresa empresa = new Empresa(cnpj: cnpj)
        Vaga vaga = new Vaga(id: idVaga, empresa: empresa)
        vagaServiceMock.buscarVagaPorId(idVaga) >> vaga
        curtidaDAOMock.jaCurtiuVaga(cpf, idVaga) >> false
        curtidaDAOMock.verificarMatch(cpf, cnpj, idVaga) >> true
        matchDAOMock.existeMatch(cpf, cnpj, idVaga) >> false

        when:
        boolean deuMatch = service.curtirVaga(cpf, idVaga)

        then:
        1 * curtidaDAOMock.registrarCurtidaCandidato(cpf, idVaga)
        1 * matchDAOMock.registrarMatch(cpf, cnpj, idVaga)
        deuMatch
    }

    def "nao deve criar match se a vaga selecionada nao pertencer a empresa"() {
        given:
        String cpf = "12345678901"
        String cnpj = "12345678901234"
        Integer idVaga = 1
        vagaServiceMock.buscarVagaPorId(idVaga) >> new Vaga(id: idVaga, empresa: new Empresa(cnpj: "99999999999999"))

        when:
        service.curtirCandidato(cnpj, cpf, idVaga)

        then:
        thrown(IllegalStateException)
        0 * curtidaDAOMock.registrarCurtidaEmpresa(_, _, _)
        0 * matchDAOMock.registrarMatch(_, _, _)
    }

    def "nao deve remover curtida da empresa se ja houver match"() {
        given:
        matchDAOMock.existeMatch("12345678901", "12345678901234", 1) >> true

        when:
        boolean removeu = service.removerCurtidaEmpresa("12345678901234", "12345678901", 1)

        then:
        !removeu
        0 * curtidaDAOMock.removerCurtidaEmpresa(_, _, _)
    }
}