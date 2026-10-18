package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.AtualizarCandidatoCommand
import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.model.Candidato
import spock.lang.Specification

class AtualizarCandidatoCommandSpec extends Specification {

    CandidatoController candidatoControllerMock

    def setup() {
        candidatoControllerMock = Mock(CandidatoController)
    }

    def "deve atualizar todos os campos do candidato e chamar o controller"() {
        given:
        Candidato candidato = new Candidato(
                cpf: "11122233344",
                nome: "Nome Antigo",
                email: "antigo@email.com",
                idade: 20,
                estado: "BA",
                cep: "40000000",
                descricao: "Descricao Antiga",
                competencias: ["Java"]
        )

        String inputUsuario = "Novo Nome\nnovo@email.com\n25\nPE\n55000000\nNova Descricao\nGroovy, Spock\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new AtualizarCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.atualizar({ Candidato c ->
            c.cpf == "11122233344" &&
                    c.nome == "Novo Nome" &&
                    c.email == "novo@email.com" &&
                    c.idade == 25 &&
                    c.estado == "PE" &&
                    c.cep == "55000000" &&
                    c.descricao == "Nova Descricao" &&
                    c.competencias == ["Groovy", "Spock"]
        }) >> [status: 200, mensagem: "Candidato atualizado com sucesso!"]
    }

    def "deve manter os valores originais quando o usuario pressionar enter vazio"() {
        given:
        Candidato candidato = new Candidato(
                cpf: "11122233344",
                nome: "Nome Original",
                email: "original@email.com",
                idade: 30,
                estado: "PE",
                cep: "55299300",
                descricao: "Dev Backend",
                competencias: ["Java", "SQL"]
        )

        String inputUsuario = "\n\n\n\n\n\n\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new AtualizarCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.atualizar({ Candidato c ->
            c.nome == "Nome Original" &&
                    c.email == "original@email.com" &&
                    c.idade == 30 &&
                    c.estado == "PE" &&
                    c.cep == "55299300" &&
                    c.descricao == "Dev Backend" &&
                    c.competencias == ["Java", "SQL"]
        }) >> [status: 200, mensagem: "Candidato atualizado com sucesso!"]
    }

    def "deve manter a idade original se o usuario informar valor nao numerico"() {
        given:
        Candidato candidato = new Candidato(
                cpf: "11122233344",
                nome: "Carlos",
                email: "carlos@email.com",
                idade: 28,
                estado: "PE",
                cep: "55000000",
                descricao: "Dev",
                competencias: ["Kotlin"]
        )

        String inputUsuario = "\n\nidadeInvalida\n\n\n\n\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new AtualizarCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.atualizar({ Candidato c ->
            c.idade == 28
        }) >> [status: 200, mensagem: "Candidato atualizado com sucesso!"]
    }
}