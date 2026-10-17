package br.com.zg.aceleraspec.service

import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.service.GerenciadorDeVagasService
import spock.lang.Specification

class GerenciadorDeVagasServiceSpec extends Specification {

    private GerenciadorDeVagasService service
    private VagaDAO vagaDAOMock
    private CompetenciaDAO competenciaDAOMock

    def setup() {
        vagaDAOMock = Mock(VagaDAO)
        competenciaDAOMock = Mock(CompetenciaDAO)

        service = new GerenciadorDeVagasService(vagaDAOMock, competenciaDAOMock)

    }

    def "deve cadastrar vaga e vincular todas as suas competencias corretamente"() {
        given: "uma vaga preenchida com duas competencias"
        Vaga vaga = new Vaga(titulo: "Desenvolvedor Groovy", competencia: ["Groovy", "Spock"])
        vaga.id = 10

        when: "o servico de cadastro eh acionado"
        Integer idRetornado = service.cadastrarVaga(vaga)

        then: "o DAO de vaga deve cadastrar a vaga 1 vez"
        1 * vagaDAOMock.cadastrar(vaga)

        and: "o DAO de competencia deve garantir e vincular cada uma das competencias"
        1 * competenciaDAOMock.garantirCompetencia("Groovy")
        1 * competenciaDAOMock.adicionarAVaga(10, "Groovy")

        1 * competenciaDAOMock.garantirCompetencia("Spock")
        1 * competenciaDAOMock.adicionarAVaga(10, "Spock")

        and: "o servico deve retornar o ID correto"
        idRetornado == 10
    }

    def "deve listar vagas da empresa preenchendo as competencias de cada uma"() {
        given: "o CNPJ de uma empresa e uma vaga simulada retornada pelo banco"
        String cnpj = "12.345.678/0001-99"
        Vaga vagaSimulada = new Vaga(id: 5, titulo: "Arquiteto Java")

        when: "pedimos para listar as vagas dessa empresa"
        List<Vaga> resultado = service.listarVagasDaEmpresa(cnpj)

        then: "o sistema busca as vagas daquela empresa no DAO principal"
        1 * vagaDAOMock.listarPorEmpresa(cnpj) >> [vagaSimulada]

        and: "para cada vaga encontrada, busca e preenche suas competencias"
        1 * competenciaDAOMock.listarDaVaga(5) >> ["Java", "Spring", "AWS"]

        and: "a vaga retornada contem a lista de competencias correta"
        resultado.size() == 1
        resultado[0].competencia == ["Java", "Spring", "AWS"]
    }

    def "deve extrair competencias de uma string suja perfeitamente"() {
        when: "passamos uma string com espacos a mais e virgulas sobrando"
        List<String> resultado = GerenciadorDeVagasService.extrairCompetencias("Java, Groovy ,  Spring ,")

        then: "ele deve limpar os espacos e ignorar itens vazios"
        resultado == ["Java", "Groovy", "Spring"]
    }
}