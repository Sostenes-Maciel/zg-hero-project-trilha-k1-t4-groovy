package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.CadastrarVagaCommand
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class CadastrarVagaCommandSpec extends Specification {

    VagaController vagaControllerMock
    Empresa empresaTeste

    def setup() {
        vagaControllerMock = Mock(VagaController)
        empresaTeste = new Empresa(nome: "ZG Solucoes", cnpj: "12345678901234")
    }

    def "deve ler os dados do terminal e cadastrar uma nova vaga com sucesso"() {
        given: "entradas simulando todos os dados da vaga na ordem que o terminal exige"

        String inputUsuario = "Desenvolvedor Backend\n" +
                "Vaga para desenvolvedor com experiência em microserviços\n" +
                "PE\n" +
                "Brasil\n" +
                "Java, Spring Boot, Docker\n"

        def scanner = new Scanner(inputUsuario)

        def comando = new CadastrarVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when: "o comando eh executado"
        comando.executar()

        then: "o controller deve ser acionado para cadastrar a vaga preenchendo os dados corretamente"
        1 * vagaControllerMock.cadastrar(_ as Vaga) >> { Vaga v ->
            assert v.titulo == "Desenvolvedor Backend"

            assert v.competencia == ["Java", "Spring Boot", "Docker"]
            assert v.empresa == empresaTeste
            return [status: 201, mensagem: "Vaga cadastrada com sucesso"]
        }
    }
}