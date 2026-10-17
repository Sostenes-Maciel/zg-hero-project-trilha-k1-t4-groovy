package br.com.zg.aceleraspec.service

import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.service.GerenciadorDeMatchesService
import spock.lang.Specification

class GerenciadorDeMatchesServiceSpec extends Specification {

    MatchDAO matchDAOMock
    GerenciadorDeMatchesService service

    def setup() {
        matchDAOMock = Mock(MatchDAO)
        service = new GerenciadorDeMatchesService(matchDAOMock)
    }

    def "deve listar matches do candidato"() {
        given:
        String cpf = "123"
        matchDAOMock.listarMatchesDoCandidato(cpf) >> [ [nomeEmpresa: "ZG", tituloVaga: "Dev"] ]

        when:
        def matches = service.listarMatchesDoCandidato(cpf)

        then:
        matches.size() == 1
        matches[0].nomeEmpresa == "ZG"
    }

    def "deve listar matches da empresa"() {
        given:
        String cnpj = "123"
        matchDAOMock.listarMatchesDaEmpresa(cnpj) >> [ [nomeCandidato: "Joao", tituloVaga: "Dev"] ]

        when:
        def matches = service.listarMatchesDaEmpresa(cnpj)

        then:
        matches.size() == 1
        matches[0].nomeCandidato == "Joao"
    }
}