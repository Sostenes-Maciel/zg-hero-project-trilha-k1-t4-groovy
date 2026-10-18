package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.AtualizarVagaCommand
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class AtualizarVagaCommandSpec extends Specification {

    VagaController vagaControllerMock
    Empresa empresaTeste

    def setup() {
        vagaControllerMock = Mock(VagaController)
        empresaTeste = new Empresa(cnpj: "12345678000199", nome: "Empresa Teste")
    }

    def "deve interromper a execucao se o controller retornar erro ao listar vagas"() {
        given:
        Scanner scanner = new Scanner("")
        def comando = new AtualizarVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 500, mensagem: "Erro de conexao"]
        0 * vagaControllerMock.atualizar(_)
    }

    def "deve interromper a execucao se a empresa nao possuir vagas cadastradas"() {
        given:
        Scanner scanner = new Scanner("")
        def comando = new AtualizarVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: []]
        0 * vagaControllerMock.atualizar(_)
    }

    def "deve cancelar a operacao se o id informado nao for um numero inteiro"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev Backend", empresa: empresaTeste)
        String inputUsuario = "abc\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new AtualizarVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        0 * vagaControllerMock.atualizar(_)
    }

    def "deve encerrar a operacao se a vaga com o id digitado nao pertencer a empresa"() {
        given:
        Vaga vaga = new Vaga(id: 1, titulo: "Dev Backend", empresa: empresaTeste)
        String inputUsuario = "99\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new AtualizarVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        0 * vagaControllerMock.atualizar(_)
    }

    def "deve atualizar os campos informados e chamar o controller com sucesso"() {
        given:
        Vaga vaga = new Vaga(
                id: 10,
                titulo: "Titulo Antigo",
                descricao: "Descricao Antiga",
                pais: "Brasil",
                estado: "BA",
                competencia: ["Java"],
                empresa: empresaTeste
        )
        String inputUsuario = "10\nNovo Titulo\nNova Descricao\nCanada\nQC\nGroovy, Spock\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new AtualizarVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        1 * vagaControllerMock.atualizar({ Vaga v ->
            v.id == 10 &&
                    v.titulo == "Novo Titulo" &&
                    v.descricao == "Nova Descricao" &&
                    v.pais == "Canada" &&
                    v.estado == "QC" &&
                    v.competencia == ["Groovy", "Spock"]
        }) >> [status: 200, mensagem: "Vaga atualizada com sucesso!"]
    }

    def "deve manter os dados originais quando o usuario apenas pressionar enter"() {
        given:
        Vaga vaga = new Vaga(
                id: 10,
                titulo: "Titulo Original",
                descricao: "Descricao Original",
                pais: "Brasil",
                estado: "PE",
                competencia: ["Java", "Docker"],
                empresa: empresaTeste
        )
        String inputUsuario = "10\n\n\n\n\n\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new AtualizarVagaCommand(vagaControllerMock, scanner, empresaTeste)

        when:
        comando.executar()

        then:
        1 * vagaControllerMock.listarDaEmpresa(empresaTeste.cnpj) >> [status: 200, dados: [vaga]]
        1 * vagaControllerMock.atualizar({ Vaga v ->
            v.id == 10 &&
                    v.titulo == "Titulo Original" &&
                    v.descricao == "Descricao Original" &&
                    v.pais == "Brasil" &&
                    v.estado == "PE" &&
                    v.competencia == ["Java", "Docker"]
        }) >> [status: 200, mensagem: "Vaga atualizada com sucesso!"]
    }
}