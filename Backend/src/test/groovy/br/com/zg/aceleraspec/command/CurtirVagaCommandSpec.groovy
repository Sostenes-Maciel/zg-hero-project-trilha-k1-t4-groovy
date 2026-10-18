package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.CurtirVagaCommand
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class CurtirVagaCommandSpec extends Specification {

    VagaController vagaControllerMock
    MatchController matchControllerMock
    Candidato candidatoTeste

    def setup() {
        vagaControllerMock = Mock(VagaController)
        matchControllerMock = Mock(MatchController)
        candidatoTeste = new Candidato(cpf: "11122233344", nome: "Candidato Teste")
    }

    def "deve encerrar a execucao caso nao existam vagas disponiveis"() {
        given:
        Scanner scanner = new Scanner("")
        def comando = new CurtirVagaCommand(vagaControllerMock, matchControllerMock, scanner, candidatoTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listar() >> [status: 200, dados: []]
        0 * matchControllerMock.curtirVaga(_, _)
        notThrown(Exception)
    }

    def "deve cancelar a operacao caso a opcao digitada seja 0"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev Backend")
        String inputUsuario = "0\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirVagaCommand(vagaControllerMock, matchControllerMock, scanner, candidatoTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listar() >> [status: 200, dados: [vaga]]
        0 * matchControllerMock.curtirVaga(_, _)
        notThrown(Exception)
    }

    def "deve tratar entrada invalida nao numerica"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev Backend")
        String inputUsuario = "abc\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirVagaCommand(vagaControllerMock, matchControllerMock, scanner, candidatoTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listar() >> [status: 200, dados: [vaga]]
        0 * matchControllerMock.curtirVaga(_, _)
        notThrown(Exception)
    }

    def "deve tratar selecao de numero de vaga inexistente"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev Backend")
        String inputUsuario = "5\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirVagaCommand(vagaControllerMock, matchControllerMock, scanner, candidatoTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listar() >> [status: 200, dados: [vaga]]
        0 * matchControllerMock.curtirVaga(_, _)
        notThrown(Exception)
    }

    def "deve selecionar vaga valida e registrar curtida com sucesso"() {
        given:
        Vaga vaga1 = new Vaga(id: 10, titulo: "Dev Backend", estado: "PE", pais: "Brasil", competencia: ["Java"], descricao: "Vaga Java")
        Vaga vaga2 = new Vaga(id: 20, titulo: "Dev Frontend", estado: "SP", pais: "Brasil", competencia: ["React"], descricao: "Vaga React")
        String inputUsuario = "2\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirVagaCommand(vagaControllerMock, matchControllerMock, scanner, candidatoTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listar() >> [status: 200, dados: [vaga1, vaga2]]
        1 * matchControllerMock.curtirVaga(candidatoTeste.cpf, vaga2) >> [status: 200, mensagem: "Vaga curtida com sucesso!"]
        notThrown(Exception)
    }

    def "deve tratar excecoes genericas sem propagar erro"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev Backend")
        String inputUsuario = "1\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new CurtirVagaCommand(vagaControllerMock, matchControllerMock, scanner, candidatoTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listar() >> [status: 200, dados: [vaga]]
        1 * matchControllerMock.curtirVaga(candidatoTeste.cpf, vaga) >> { throw new RuntimeException("Falha no banco") }
        notThrown(Exception)
    }
}