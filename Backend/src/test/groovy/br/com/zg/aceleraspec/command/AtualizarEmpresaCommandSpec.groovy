package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.AtualizarEmpresaCommand
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.model.Empresa
import spock.lang.Specification

class AtualizarEmpresaCommandSpec extends Specification {

    EmpresaController empresaControllerMock

    def setup() {
        empresaControllerMock = Mock(EmpresaController)
    }

    def "deve atualizar todos os campos da empresa e chamar o controller"() {
        given:
        Empresa empresa = new Empresa(
                cnpj: "12345678000199",
                nome: "Nome Antigo",
                email: "antigo@empresa.com",
                pais: "Brasil",
                estado: "SP",
                cep: "01001000",
                descricao: "Descricao Antiga",
                competencias: ["Java"]
        )

        String inputUsuario = "Novo Nome\nnovo@empresa.com\nPortugal\nLisboa\n1000001\nNova Descricao\nKotlin, Spring\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new AtualizarEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        1 * empresaControllerMock.atualizar({ Empresa e ->
            e.cnpj == "12345678000199" &&
                    e.nome == "Novo Nome" &&
                    e.email == "novo@empresa.com" &&
                    e.pais == "Portugal" &&
                    e.estado == "Lisboa" &&
                    e.cep == "1000001" &&
                    e.descricao == "Nova Descricao" &&
                    e.competencias == ["Kotlin", "Spring"]
        }) >> [status: 200, mensagem: "Empresa atualizada com sucesso!"]
    }

    def "deve manter os valores originais quando o usuario pressionar enter vazio em todos os campos"() {
        given:
        Empresa empresa = new Empresa(
                cnpj: "12345678000199",
                nome: "Empresa Original",
                email: "original@empresa.com",
                pais: "Brasil",
                estado: "PE",
                cep: "55299300",
                descricao: "Software House",
                competencias: ["Groovy", "SQL"]
        )

        String inputUsuario = "\n\n\n\n\n\n\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new AtualizarEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        1 * empresaControllerMock.atualizar({ Empresa e ->
            e.cnpj == "12345678000199" &&
                    e.nome == "Empresa Original" &&
                    e.email == "original@empresa.com" &&
                    e.pais == "Brasil" &&
                    e.estado == "PE" &&
                    e.cep == "55299300" &&
                    e.descricao == "Software House" &&
                    e.competencias == ["Groovy", "SQL"]
        }) >> [status: 200, mensagem: "Empresa atualizada com sucesso!"]
    }

    def "deve lidar com resposta de erro do controller sem interromper execucao"() {
        given:
        Empresa empresa = new Empresa(
                cnpj: "12345678000199",
                nome: "Empresa Teste",
                email: "contato@empresa.com"
        )

        String inputUsuario = "\n\n\n\n\n\n\n"
        Scanner scanner = new Scanner(inputUsuario)
        def comando = new AtualizarEmpresaCommand(empresaControllerMock, scanner, empresa)

        when:
        comando.executar()

        then:
        1 * empresaControllerMock.atualizar(_) >> [status: 400, mensagem: "Erro ao atualizar dados"]
        notThrown(Exception)
    }
}