package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.ConexaoBD
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import spock.lang.Specification

class MatchDAOSpec extends Specification {

    private MatchDAO matchDAO
    private CandidatoDAO candidatoDAO
    private EmpresaDAO empresaDAO
    private VagaDAO vagaDAO

    def setup() {
        def conexaoFactory = new ConexaoBD()
        matchDAO = new MatchDAO(conexaoFactory)
        candidatoDAO = new CandidatoDAO(conexaoFactory)
        empresaDAO = new EmpresaDAO(conexaoFactory)
        vagaDAO = new VagaDAO(conexaoFactory)

        // Limpeza prévia para garantir estado limpo
        empresaDAO.excluir("66.555.444/0001-22")
        candidatoDAO.excluir("55566677788")
    }

    def "deve registrar um match com sucesso"() {
        given: "uma empresa, uma vaga e um candidato cadastrados"
        Empresa empresa = new Empresa(cnpj: "66.555.444/0001-22", nome: "Empresa Match", email: "match@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste Match")
        Vaga vaga = new Vaga(titulo: "Dev Match", pais: "Brasil", estado: "PE", descricao: "Vaga Match", empresa: empresa)
        Candidato candidato = new Candidato(cpf: "55566677788", nome: "Cand Match", email: "cand.match@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Cand Match", idade: 30)

        empresaDAO.cadastrar(empresa)
        vagaDAO.cadastrar(vaga)
        candidatoDAO.cadastrar(candidato)

        when: "registramos o match"
        matchDAO.registrarMatch(candidato.cpf, empresa.cnpj, vaga.id)

        then: "o match deve existir no banco"
        matchDAO.existeMatch(candidato.cpf, empresa.cnpj, vaga.id) == true

        cleanup:
        vagaDAO.listarPorEmpresa(empresa.cnpj).each { vagaDAO.excluir(it.id) }
        empresaDAO.excluir(empresa.cnpj)
        candidatoDAO.excluir(candidato.cpf)
    }

    def "deve listar os matches cadastrados"() {
        given: "um match previamente cadastrado"
        Empresa empresa = new Empresa(cnpj: "66.555.444/0001-22", nome: "Empresa Match", email: "match@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Teste Match")
        Vaga vaga = new Vaga(titulo: "Dev Match", pais: "Brasil", estado: "PE", descricao: "Vaga Match", empresa: empresa)
        Candidato candidato = new Candidato(cpf: "55566677788", nome: "Cand Match", email: "cand.match@teste.com", pais: "Brasil", estado: "PE", cep: "55299-300", descricao: "Cand Match", idade: 30)

        empresaDAO.cadastrar(empresa)
        vagaDAO.cadastrar(vaga)
        candidatoDAO.cadastrar(candidato)
        matchDAO.registrarMatch(candidato.cpf, empresa.cnpj, vaga.id)

        when: "listamos os matches específicos do candidato"
        List<Map<String, Object>> matches = matchDAO.listarMatchesDoCandidato(candidato.cpf)

        then: "a lista deve conter exatamente o match registrado com os dados mapeados"
        !matches.isEmpty()
        matches[0].nomeCandidato == "Cand Match"
        matches[0].nomeEmpresa == "Empresa Match"
        matches[0].tituloVaga == "Dev Match"

        cleanup:
        vagaDAO.listarPorEmpresa(empresa.cnpj).each { vagaDAO.excluir(it.id) }
        empresaDAO.excluir(empresa.cnpj)
        candidatoDAO.excluir(candidato.cpf)
    }
}