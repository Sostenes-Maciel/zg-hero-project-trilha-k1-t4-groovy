package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.CadastrarCommand
import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import spock.lang.Specification

class CadastrarCommandSpec extends Specification {

    CandidatoController candidatoControllerMock
    EmpresaController empresaControllerMock

    def setup() {
        candidatoControllerMock = Mock(CandidatoController)
        empresaControllerMock = Mock(EmpresaController)
    }

    def "deve cadastrar um candidato quando escolher a opcao 1"() {
        given: "uma entrada simulando a escolha 1 e preenchendo todos os dados do candidato"
        String inputUsuario = "1\nJoao\n12345678901\njoao@email.com\n25\nSP\n00000000\nDesenvolvedor\nJava, Groovy\n"
        def scanner = new Scanner(inputUsuario)

        def comando = new CadastrarCommand(candidatoControllerMock, empresaControllerMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "o controller de candidato deve ser chamado exatamente 1 vez e o de empresa nenhuma"
        1 * candidatoControllerMock.cadastrar(_) >> [status: 201, mensagem: "Candidato cadastrado com sucesso"]
        0 * empresaControllerMock.cadastrar(_)
    }

    def "deve cadastrar uma empresa quando escolher a opcao 2"() {
        given: "uma entrada simulando a escolha 2 e preenchendo todos os dados da empresa"
        String inputUsuario = "2\nZG Solucoes\n12345678901234\ncontato@zg.com\nBrasil\nGO\n74000000\nEmpresa de Software\nJava, SQL, Groovy\n"
        def scanner = new Scanner(inputUsuario)

        def comando = new CadastrarCommand(candidatoControllerMock, empresaControllerMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "o controller de empresa deve ser chamado exatamente 1 vez e o de candidato nenhuma"
        1 * empresaControllerMock.cadastrar(_) >> [status: 201, mensagem: "Empresa cadastrada com sucesso"]
        0 * candidatoControllerMock.cadastrar(_)
    }

    def "deve ignorar e nao chamar nenhum controller se a opcao for invalida"() {
        given: "uma opcao que nao existe (3)"
        String inputUsuario = "3\n"
        def scanner = new Scanner(inputUsuario)

        def comando = new CadastrarCommand(candidatoControllerMock, empresaControllerMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "nenhum dos dois controllers deve ser chamado"
        0 * candidatoControllerMock.cadastrar(_)
        0 * empresaControllerMock.cadastrar(_)
    }
}