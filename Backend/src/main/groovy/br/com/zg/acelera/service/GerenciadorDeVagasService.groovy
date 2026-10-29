package br.com.zg.acelera.service

import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.model.Vaga

class GerenciadorDeVagasService {

    private final VagaDAO vagaDAO
    private final CompetenciaDAO competenciaDAO

    GerenciadorDeVagasService(VagaDAO vagaDAO, CompetenciaDAO competenciaDAO) {
        this.vagaDAO = vagaDAO
        this.competenciaDAO = competenciaDAO
    }

    Integer cadastrarVaga(Vaga vaga) {
        vagaDAO.cadastrar(vaga)
        Integer idVaga = vaga.id

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

    Vaga buscarVagaPorId(Integer id) {
        return vagaDAO.buscarPorId(id)
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

        List<String> competenciasAntigas = competenciaDAO.listarDaVaga(vaga.id)

        competenciasAntigas.each { competencia ->
            competenciaDAO.removerDaVaga(vaga.id, competencia)
        }

        vaga.competencia.each { competencia ->
            competenciaDAO.garantirCompetencia(competencia)
            competenciaDAO.adicionarAVaga(vaga.id, competencia)
        }
    }

    void excluirVaga(Integer id) {
        vagaDAO.excluir(id)
    }

    static List<String> extrairCompetencias(String entrada) {
        if (!entrada) return []
        return entrada.tokenize(',').collect { it.trim() }.findAll { it }
    }
}