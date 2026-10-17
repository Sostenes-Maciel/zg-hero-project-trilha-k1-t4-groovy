package br.com.zg.aceleraspec.service

import br.com.zg.acelera.dao.*
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.service.GerenciadorDePerfisService
import spock.lang.Specification

class GerenciadorDePerfisServiceSpec extends Specification {

    private GerenciadorDePerfisService service

    private CompetenciaDAO competenciaDAOMock
    private CurtidaDAO curtidaDAOMock
    private MatchDAO matchDAOMock

    def setup() {
        IConexao conexaoMock = Mock(IConexao)

        competenciaDAOMock = Mock(CompetenciaDAO)
        curtidaDAOMock = Mock(CurtidaDAO)
        matchDAOMock = Mock(MatchDAO)

        service = new GerenciadorDePerfisService(conexaoMock)

        // 4. TRUQUE DO GROOVY: Sobrescrevemos os DAOs originais pelos nossos Mocks
        // O "@" permite alterar variáveis que são "private" e "final" durante o teste!
        service.competenciaDAO = competenciaDAOMock
        service.curtidaDAO = curtidaDAOMock
        service.matchDAO = matchDAOMock
    }

    def "deve adicionar competencia garantindo que ela seja salva no banco global antes"() {
        given: "um candidato e uma competencia"
        Candidato candidato = new Candidato(cpf: "123.456.789-00")
        String competencia = "Groovy"

        when: "acionamos a adicao de competencia"
        service.adicionarCompetenciaAoCandidato(candidato, competencia)

        then: "o DAO deve PRIMEIRO garantir que a competencia existe na tabela geral"
        1 * competenciaDAOMock.garantirCompetencia("Groovy")

        and: "DEPOIS, deve vincular a competencia ao CPF do candidato"
        1 * competenciaDAOMock.adicionarAoCandidato("123.456.789-00", "Groovy")
    }

    def "nao deve registrar match se ja existir um registrado"() {
        given: "dados do possivel match"
        String cpf = "111.111.111-11"
        String cnpj = "22.222.222/0001-22"
        int vagaId = 1

        when: "o sistema processa a curtida"
        // Outro truque: No Groovy podemos testar métodos private diretamente!
        service.processarPossivelMatch(cpf, cnpj, vagaId, "Joao", "Empresa ZG")

        then: "se a curtida for mutua..."
        1 * curtidaDAOMock.verificarMatch(cpf, cnpj, vagaId) >> true

        and: "...e o match JA EXISTIR no banco"
        1 * matchDAOMock.existeMatch(cpf, cnpj, vagaId) >> true

        and: "entao o DAO de registrar match NUNCA deve ser chamado"
        0 * matchDAOMock.registrarMatch(_, _, _)
    }

    def "deve registrar um match novo com sucesso"() {
        given: "dados do possivel match"
        String cpf = "111.111.111-11"
        String cnpj = "22.222.222/0001-22"
        int vagaId = 1

        when: "o sistema processa a curtida"
        service.processarPossivelMatch(cpf, cnpj, vagaId, "Joao", "Empresa ZG")

        then: "se a curtida for mutua..."
        1 * curtidaDAOMock.verificarMatch(cpf, cnpj, vagaId) >> true

        and: "...e o match AINDA NAO EXISTIR no banco"
        1 * matchDAOMock.existeMatch(cpf, cnpj, vagaId) >> false

        and: "entao o DAO de registrar match DEVE ser chamado exatamente 1 vez"
        1 * matchDAOMock.registrarMatch(cpf, cnpj, vagaId)
    }
}