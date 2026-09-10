package br.com.zg.aceleraspec

import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.repository.BancodeDados
import spock.lang.Specification

class BancodeDadosSpec extends Specification {

    def setup() {
        BancodeDados.candidatos.clear()
        BancodeDados.empresas.clear()
    }

    def "deve criar um candidato com os dados informados"() {

        given:
        Candidato candidato = new Candidato(
                nome: "Candidato Teste",
                idade: 25,
                email: "teste@email.com",
                cpf: "111.222.333-44",
                estado: "PE",
                cep: "55299-300",
                descricao: "Candidato para teste",
                competencias: ["Java", "Groovy"]
        )

        expect:
        candidato.nome == "Candidato Teste"
        candidato.idade == 25
        candidato.email == "teste@email.com"
        candidato.cpf == "111.222.333-44"
        candidato.estado == "PE"
        candidato.cep == "55299-300"
        candidato.descricao == "Candidato para teste"
        candidato.competencias == ["Java", "Groovy"]
    }

    def "deve criar uma empresa com os dados informados"() {

        given:
        Empresa empresa = new Empresa(
                nome: "Empresa Teste",
                email: "empresa@email.com",
                cnpj: "11.222.333/0001-44",
                pais: "Brasil",
                estado: "PE",
                cep: "55299-300",
                descricao: "Empresa para teste",
                competencias: ["Java", "Groovy"]
        )

        expect:
        empresa.nome == "Empresa Teste"
        empresa.email == "empresa@email.com"
        empresa.cnpj == "11.222.333/0001-44"
        empresa.pais == "Brasil"
        empresa.estado == "PE"
        empresa.cep == "55299-300"
        empresa.descricao == "Empresa para teste"
        empresa.competencias == ["Java", "Groovy"]
    }

    def "deve cadastrar um novo candidato"() {

        given:
        BancodeDados dados = new BancodeDados()

        Candidato candidato = new Candidato(
                nome: "Candidato Teste",
                idade: 25,
                email: "teste@email.com",
                cpf: "111.222.333-44",
                estado: "PE",
                cep: "55299-300",
                descricao: "Candidato para teste",
                competencias: ["Java"]
        )

        int quantidadeAntes = BancodeDados.candidatos.size()

        when:
        dados.cadastrarCandidato(candidato)

        then:
        BancodeDados.candidatos.size() == quantidadeAntes + 1
        BancodeDados.candidatos.contains(candidato)
    }

    def "deve cadastrar uma nova empresa"() {

        given:
        BancodeDados dados = new BancodeDados()

        Empresa empresa = new Empresa(
                nome: "Empresa Teste",
                email: "empresa@email.com",
                cnpj: "11.222.333/0001-44",
                pais: "Brasil",
                estado: "PE",
                cep: "55299-300",
                descricao: "Empresa para teste",
                competencias: ["Java", "Groovy"]
        )

        int quantidadeAntes = BancodeDados.empresas.size()

        when:
        dados.cadastrarEmpresa(empresa)

        then:
        BancodeDados.empresas.size() == quantidadeAntes + 1
        BancodeDados.empresas.contains(empresa)
    }
}