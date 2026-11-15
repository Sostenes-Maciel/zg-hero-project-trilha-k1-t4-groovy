package br.com.zg.aceleraspec.controller

import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.service.GerenciadorDeMatchesService
import br.com.zg.acelera.service.GerenciadorDePerfisService
import br.com.zg.acelera.service.GerenciadorDeRecrutadoresService
import br.com.zg.acelera.service.GerenciadorDeVagasService
import spock.lang.Specification

class MatchControllerSpec extends Specification {

    GerenciadorDeMatchesService matchServiceMock
    GerenciadorDePerfisService perfilServiceMock
    GerenciadorDeVagasService vagaServiceMock
    GerenciadorDeRecrutadoresService recrutadorServiceMock
    MatchController controller

    def setup() {
        // Criando os 4 mocks necessários para o novo construtor
        matchServiceMock = Mock(GerenciadorDeMatchesService)
        perfilServiceMock = Mock(GerenciadorDePerfisService)
        vagaServiceMock = Mock(GerenciadorDeVagasService)
        recrutadorServiceMock = Mock(GerenciadorDeRecrutadoresService)

        // Passando os 4 mocks na mesma ordem da classe real
        controller = new MatchController(matchServiceMock, perfilServiceMock, vagaServiceMock, recrutadorServiceMock)
    }

    def "deve curtir vaga e identificar match"() {
        given:
        perfilServiceMock.curtirVaga(*_) >> true
        // Mock novo: como deu match, simulamos a busca da Vaga para evitar NullPointerException no nomeRevelado
        vagaServiceMock.buscarVaga(*_) >> new Vaga(empresa: new Empresa(nome: "ZG Soluções"))

        when:
        def resposta = controller.curtirVaga("12345678900", 1)

        then:
        resposta.status == 201
        resposta.match == true
        resposta.mensagem == "MATCH ENCONTRADO!"
        resposta.nomeRevelado == "ZG Soluções"
    }

    def "deve curtir vaga sem identificar match"() {
        given:
        perfilServiceMock.curtirVaga(*_) >> false

        when:
        def resposta = controller.curtirVaga("12345678900", 1)

        then:
        resposta.status == 201
        resposta.match == false
    }

    def "deve retornar status 500 ao ocorrer erro ao curtir vaga"() {
        given:
        perfilServiceMock.curtirVaga(*_) >> { throw new RuntimeException("Erro") }

        when:
        def resposta = controller.curtirVaga("12345678900", 1)

        then:
        resposta.status == 500
    }

    def "deve curtir candidato e identificar match"() {
        given:
        perfilServiceMock.curtirCandidato(*_) >> true
        // Mock novo: como deu match, simulamos a busca do Candidato para retornar o nome real
        perfilServiceMock.buscarCandidatoPorCpf(*_) >> new Candidato(nome: "Sóstenes")

        when:
        def resposta = controller.curtirCandidato("12345678901234", "12345678900", 1)

        then:
        resposta.status == 201
        resposta.match == true
        resposta.nomeRevelado == "Sóstenes"
    }

    def "deve rejeitar curtida empresarial sem vaga selecionada"() {
        given:
        perfilServiceMock.curtirCandidato(*_) >> { throw new IllegalArgumentException("Selecione uma vaga para curtir o candidato.") }

        when:
        def resposta = controller.curtirCandidato("12345678901234", "12345678900", null)

        then:
        resposta.status == 400
        resposta.mensagem.contains("vaga")
    }

    def "deve remover curtida da empresa apenas quando o service permitir"() {
        given:
        perfilServiceMock.removerCurtidaEmpresa(*_) >> false

        when:
        def resposta = controller.removerCurtidaEmpresa("12345678901234", "12345678900", 1)

        then:
        resposta.status == 400
        resposta.mensagem.contains("Não foi possível remover")
    }

    def "deve listar matches do candidato"() {
        given:
        perfilServiceMock.listarStatusCurtidasCandidato(*_) >> [[idVaga: 1, status: "curtido"]]

        when:
        def resposta = controller.listarMatchesCandidato("12345678900")

        then:
        resposta.status == 200
        resposta.dados.size() == 1
    }

    def "deve listar matches da empresa"() {
        given:
        perfilServiceMock.listarStatusCurtidasEmpresa(*_) >> [[cpfCandidato: "12345678900", idVaga: 1, status: "match"]]

        when:
        def resposta = controller.listarMatchesEmpresa("12345678901234")

        then:
        resposta.status == 200
        resposta.dados.size() == 1
    }
}