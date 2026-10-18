package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.GerenciarCompetenciasCandidatoCommand
import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.model.Candidato
import spock.lang.Specification

class GerenciarCompetenciasCandidatoCommandSpec extends Specification {

    CandidatoController candidatoControllerMock

    def setup() {
        candidatoControllerMock = Mock(CandidatoController)
    }

    def "deve encerrar o loop imediatamente quando a opcao for 4"() {
        given:
        Candidato candidato = new Candidato(nome: "Carlos", competencias: [])
        String inputUsuario = "4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        0 * candidatoControllerMock.adicionarCompetencia(_, _)
        0 * candidatoControllerMock.removerCompetencia(_, _)
    }

    def "deve tratar opcao nao numerica ou invalida e continuar no loop ate sair"() {
        given:
        Candidato candidato = new Candidato(nome: "Carlos", competencias: [])
        String inputUsuario = "abc\n9\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        0 * candidatoControllerMock.adicionarCompetencia(_, _)
        0 * candidatoControllerMock.removerCompetencia(_, _)
    }

    def "deve adicionar nova competencia e atualizar lista do candidato"() {
        given:
        Candidato candidato = new Candidato(nome: "Carlos", competencias: ["Java"])
        String inputUsuario = "1\nGroovy\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.adicionarCompetencia(candidato, "Groovy")
        candidato.competencias == ["Java", "Groovy"]
    }

    def "nao deve adicionar competencia se o texto for vazio"() {
        given:
        Candidato candidato = new Candidato(nome: "Carlos", competencias: ["Java"])
        String inputUsuario = "1\n   \n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        0 * candidatoControllerMock.adicionarCompetencia(_, _)
        candidato.competencias == ["Java"]
    }

    def "deve remover competencia existente com sucesso"() {
        given:
        Candidato candidato = new Candidato(nome: "Carlos", competencias: ["Java", "Docker"])
        String inputUsuario = "2\nJava\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.removerCompetencia(candidato, "Java")
        candidato.competencias == ["Docker"]
    }

    def "nao deve chamar controller se a competencia informada para remocao nao existir"() {
        given:
        Candidato candidato = new Candidato(nome: "Carlos", competencias: ["Java"])
        String inputUsuario = "2\nPython\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        0 * candidatoControllerMock.removerCompetencia(_, _)
        candidato.competencias == ["Java"]
    }

    def "deve limpar todas as competencias quando houver itens na lista"() {
        given:
        Candidato candidato = new Candidato(nome: "Carlos", competencias: ["Java", "Spock", "Docker"])
        String inputUsuario = "3\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        1 * candidatoControllerMock.removerCompetencia(candidato, "Java")
        1 * candidatoControllerMock.removerCompetencia(candidato, "Spock")
        1 * candidatoControllerMock.removerCompetencia(candidato, "Docker")
        candidato.competencias.isEmpty()
    }

    def "nao deve chamar controller na opcao 3 se a lista ja estiver vazia"() {
        given:
        Candidato candidato = new Candidato(nome: "Carlos", competencias: [])
        String inputUsuario = "3\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasCandidatoCommand(candidatoControllerMock, scanner, candidato)

        when:
        comando.executar()

        then:
        0 * candidatoControllerMock.removerCompetencia(_, _)
    }
}