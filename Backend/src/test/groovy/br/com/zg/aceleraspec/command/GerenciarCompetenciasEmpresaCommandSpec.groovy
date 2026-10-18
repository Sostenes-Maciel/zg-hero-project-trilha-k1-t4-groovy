package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.GerenciarCompetenciasEmpresaCommand
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.model.Empresa
import spock.lang.Specification

class GerenciarCompetenciasEmpresaCommandSpec extends Specification {

    EmpresaController empresaControllerMock

    def setup() {
        empresaControllerMock = Mock(EmpresaController)
    }

    def "deve encerrar o loop imediatamente quando a opcao for 4"() {
        given:
        Empresa empresa = new Empresa(nome: "ZG Solucoes", competencias: [])
        String inputUsuario = "4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        0 * empresaControllerMock.adicionarCompetencia(_, _)
        0 * empresaControllerMock.removerCompetencia(_, _)
    }

    def "deve tratar opcao nao numerica ou invalida e continuar no loop ate sair"() {
        given:
        Empresa empresa = new Empresa(nome: "ZG Solucoes", competencias: [])
        String inputUsuario = "invalido\n99\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        0 * empresaControllerMock.adicionarCompetencia(_, _)
        0 * empresaControllerMock.removerCompetencia(_, _)
    }

    def "deve adicionar nova competencia e atualizar lista da empresa"() {
        given:
        Empresa empresa = new Empresa(nome: "ZG Solucoes", competencias: ["Java"])
        String inputUsuario = "1\nGroovy\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        1 * empresaControllerMock.adicionarCompetencia(empresa, "Groovy")
        empresa.competencias == ["Java", "Groovy"]
    }

    def "nao deve adicionar competencia se o texto for vazio"() {
        given:
        Empresa empresa = new Empresa(nome: "ZG Solucoes", competencias: ["Java"])
        String inputUsuario = "1\n   \n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        0 * empresaControllerMock.adicionarCompetencia(_, _)
        empresa.competencias == ["Java"]
    }

    def "deve remover competencia existente com sucesso"() {
        given:
        Empresa empresa = new Empresa(nome: "ZG Solucoes", competencias: ["Java", "PostgreSQL"])
        String inputUsuario = "2\nJava\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        1 * empresaControllerMock.removerCompetencia(empresa, "Java")
        empresa.competencias == ["PostgreSQL"]
    }

    def "nao deve chamar controller se a competencia informada para remocao nao existir"() {
        given:
        Empresa empresa = new Empresa(nome: "ZG Solucoes", competencias: ["Java"])
        String inputUsuario = "2\nRust\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        0 * empresaControllerMock.removerCompetencia(_, _)
        empresa.competencias == ["Java"]
    }

    def "deve limpar todas as competencias quando houver itens na lista"() {
        given:
        Empresa empresa = new Empresa(nome: "ZG Solucoes", competencias: ["Java", "Docker", "AWS"])
        String inputUsuario = "3\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        1 * empresaControllerMock.removerCompetencia(empresa, "Java")
        1 * empresaControllerMock.removerCompetencia(empresa, "Docker")
        1 * empresaControllerMock.removerCompetencia(empresa, "AWS")
        empresa.competencias.isEmpty()
    }

    def "nao deve chamar controller na opcao 3 se a lista ja estiver vazia"() {
        given:
        Empresa empresa = new Empresa(nome: "ZG Solucoes", competencias: [])
        String inputUsuario = "3\n4\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new GerenciarCompetenciasEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        0 * empresaControllerMock.removerCompetencia(_, _)
    }
}