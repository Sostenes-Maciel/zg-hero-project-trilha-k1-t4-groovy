package br.com.zg.acelera.service

import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class GerenciadorDeVagas {

    private VagaDAO vagaDAO = new VagaDAO()
    private CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    Integer cadastrarVaga(Vaga vaga) {
        Integer idVaga = vagaDAO.cadastrar(vaga)

        vaga.id = idVaga

        vaga.competencia.each { competencia ->
            competenciaDAO.garantirCompetencia(competencia)
            competenciaDAO.adicionarAVaga(idVaga, competencia)
        }

        return idVaga
    }

    List<Vaga> listarVagas() {
        List<Vaga> vagas = vagaDAO.listarTodos()

        vagas.each { vaga ->
            vaga.competencia = competenciaDAO.listarDaVaga(vaga.id)
        }

        return vagas
    }

    List<Vaga> listarVagasDaEmpresa(String cnpj) {
        List<Vaga> vagas = vagaDAO.listarPorEmpresa(cnpj)

        vagas.each { vaga ->
            vaga.competencia = competenciaDAO.listarDaVaga(vaga.id)
        }

        return vagas
    }

    Vaga buscarVaga(Integer id) {
        Vaga vaga = vagaDAO.buscarPorId(id)

        if (vaga != null) {
            vaga.competencia = competenciaDAO.listarDaVaga(vaga.id)
        }

        return vaga
    }

    void atualizarVaga(Vaga vaga) {
        vagaDAO.atualizar(vaga)
    }

    void excluirVaga(Integer id) {
        vagaDAO.excluir(id)
    }
    void cadastrarVagaPeloTerminal(Scanner sc, Empresa empresa) {

        println "\n--- CADASTRAR NOVA VAGA ---"
        println "Empresa: ${empresa.nome}"

        print "Título da vaga: "
        String titulo = sc.nextLine().trim()

        print "País: "
        String pais = sc.nextLine().trim()

        print "Estado: "
        String estado = sc.nextLine().trim()

        print "Descrição: "
        String descricao = sc.nextLine().trim()

        print "Competências exigidas: "
        String entradaComps = sc.nextLine()

        List<String> competencias = entradaComps
                .tokenize(',')
                .collect { it.trim() }
                .findAll { it }

        Vaga vaga = new Vaga(
                titulo: titulo,
                pais: pais,
                estado: estado,
                descricao: descricao,
                competencia: competencias,
                empresa: empresa
        )

        Integer idVaga = cadastrarVaga(vaga)

        println "Vaga cadastrada com sucesso!"
        println "ID da vaga: ${idVaga}"
    }
}