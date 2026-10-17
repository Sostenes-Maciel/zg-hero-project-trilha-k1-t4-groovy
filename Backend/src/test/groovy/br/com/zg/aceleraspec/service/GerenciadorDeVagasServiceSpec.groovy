package br.com.zg.aceleraspec.service

import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.service.GerenciadorDeVagasService
import spock.lang.Specification

class GerenciadorDeVagasServiceSpec extends Specification {

    VagaDAO vagaDAOMock
    CompetenciaDAO competenciaDAOMock
    GerenciadorDeVagasService service

    def setup() {
        vagaDAOMock = Mock(VagaDAO)
        competenciaDAOMock = Mock(CompetenciaDAO)
        service = new GerenciadorDeVagasService(vagaDAOMock, competenciaDAOMock)
    }

    def "deve cadastrar vaga e suas competencias com sucesso"() {
        given:
        Empresa empresa = new Empresa(cnpj: "12345678901234")
        Vaga vaga = new Vaga(titulo: "Desenvolvedor", competencia: ["Java", "Groovy"], empresa: empresa)

        when:
        Integer idRetornado = service.cadastrarVaga(vaga)

        then:
        1 * vagaDAOMock.cadastrar(vaga)
        1 * competenciaDAOMock.garantirCompetencia("Java")
        1 * competenciaDAOMock.adicionarAVaga(_, "Java")
        1 * competenciaDAOMock.garantirCompetencia("Groovy")
        1 * competenciaDAOMock.adicionarAVaga(_, "Groovy")
        idRetornado == vaga.id
    }

    def "deve listar vagas preenchendo as competencias de cada uma"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev")
        vagaDAOMock.listarTodos() >> [vaga]
        competenciaDAOMock.listarDaVaga(1) >> ["Java"]

        when:
        List<Vaga> vagas = service.listarVagas()

        then:
        vagas.size() == 1
        vagas[0].competencia == ["Java"]
    }

    def "deve buscar vaga por id e preencher suas competencias"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev")
        vagaDAOMock.buscarPorId(1) >> vaga
        competenciaDAOMock.listarDaVaga(1) >> ["Python"]

        when:
        Vaga vagaRetornada = service.buscarVaga(1)

        then:
        vagaRetornada.titulo == "Dev"
        vagaRetornada.competencia == ["Python"]
    }

    def "deve atualizar vaga substituindo competencias antigas pelas novas"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev", competencia: ["Go"])
        competenciaDAOMock.listarDaVaga(1) >> ["Java"]

        when:
        service.atualizarVaga(vaga)

        then:
        1 * vagaDAOMock.atualizar(vaga)
        1 * competenciaDAOMock.removerDaVaga(1, "Java")
        1 * competenciaDAOMock.garantirCompetencia("Go")
        1 * competenciaDAOMock.adicionarAVaga(1, "Go")
    }

    def "deve excluir vaga com sucesso"() {
        when:
        service.excluirVaga(1)

        then:
        1 * vagaDAOMock.excluir(1)
    }

    def "deve extrair competencias corretamente de uma string"() {
        expect:
        GerenciadorDeVagasService.extrairCompetencias(entrada) == resultado

        where:
        entrada            || resultado
        "Java, Groovy, Go" || ["Java", "Groovy", "Go"]
        " Python , C "     || ["Python", "C"]
        ""                 || []
        null               || []
    }
}