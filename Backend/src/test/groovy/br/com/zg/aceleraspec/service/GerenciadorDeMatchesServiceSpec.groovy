package br.com.zg.aceleraspec.service

import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.service.GerenciadorDeMatchesService
import spock.lang.Specification

class GerenciadorDeMatchesServiceSpec extends Specification {

    private GerenciadorDeMatchesService service
    private MatchDAO matchDAOMock
    private InputStream tecladoOriginal

    def setup() {
        matchDAOMock = Mock(MatchDAO)

        service = new GerenciadorDeMatchesService(matchDAOMock)

        tecladoOriginal = System.in
        System.setIn(new ByteArrayInputStream("\n".getBytes()))
    }

    def cleanup() {
        System.setIn(tecladoOriginal)
    }

    def "deve exibir matches do candidato acionando o DAO corretamente"() {
        given: "um CPF e uma lista de matches simulada"
        String cpf = "123.456.789-00"
        def listaSimulada = [
                [nomeEmpresa: "Empresa ZG", tituloVaga: "Desenvolvedor Groovy"]
        ]

        when: "o servico eh chamado"
        service.exibirMatchesDoCandidato(cpf)

        then: "o DAO deve ser acionado 1 vez passando o CPF correto"
        1 * matchDAOMock.listarMatchesDoCandidato(cpf) >> listaSimulada
    }

    def "deve exibir matches da empresa acionando o DAO corretamente"() {
        given: "um CNPJ e uma lista de matches simulada"
        String cnpj = "12.345.678/0001-99"
        def listaSimulada = [
                [nomeCandidato: "Maria Silva", tituloVaga: "Engenheira de Dados"]
        ]

        when: "o servico eh chamado"
        service.exibirMatchesDaEmpresa(cnpj)

        then: "o DAO deve ser acionado 1 vez passando o CNPJ correto"
        1 * matchDAOMock.listarMatchesDaEmpresa(cnpj) >> listaSimulada
    }
}